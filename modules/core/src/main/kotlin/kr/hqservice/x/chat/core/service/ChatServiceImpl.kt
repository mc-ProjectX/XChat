package kr.hqservice.x.chat.core.service

import kr.hqservice.framework.bukkit.core.extension.colorize
import kr.hqservice.framework.global.core.component.Service
import kr.hqservice.framework.netty.api.PacketSender
import kr.hqservice.x.chat.api.ChatData
import kr.hqservice.x.chat.api.ChatFormat
import kr.hqservice.x.chat.api.ChatMode
import kr.hqservice.x.chat.api.ChatSender
import kr.hqservice.x.chat.api.format.WhisperReceiverFormat
import kr.hqservice.x.chat.api.format.WhisperSenderFormat
import kr.hqservice.x.chat.api.service.ChatService
import kr.hqservice.x.chat.core.ChatSenderImpl
import kr.hqservice.x.chat.core.component.registry.ChatModeComponentRegistry
import kr.hqservice.x.chat.core.database.service.PlayerChatModeService
import kr.hqservice.x.chat.core.network.packet.ChatPacket
import kr.hqservice.x.core.api.XPlayer
import kr.hqservice.x.core.api.service.XCoreService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.ChatColor
import org.bukkit.Server
import org.bukkit.entity.Player
import java.util.*
import java.util.logging.Logger

@Service
class ChatServiceImpl(
    private val server: Server,
    private val logger: Logger,
    private val packetSender: PacketSender,
    private val xCoreService: XCoreService,

    private val chatModeService: PlayerChatModeService,
    private val chatModeComponentRegistry: ChatModeComponentRegistry
) : ChatService {
    private val whisperSenderIds = mutableMapOf<UUID, UUID>()
    private val systemId = UUID.randomUUID()

    fun getWhisperSenderId(playerId: UUID): UUID? {
        return whisperSenderIds[playerId]
    }

    override fun setChatMode(userId: UUID, chatMode: ChatMode) {
        val modeId = chatMode.getId()
        val registeredId = chatModeComponentRegistry.findChatMode(modeId)?.getId()
        chatModeService.setChatMode(userId, registeredId)
    }

    override fun findChatMode(modeId: Int): ChatMode? {
        return chatModeComponentRegistry.findChatMode(modeId)
    }

    override fun getChatModes(): List<ChatMode> {
        return chatModeComponentRegistry.getAllChatModes().toList()
    }

    override fun sendChat(receiverIds: List<UUID>, chat: String, format: ChatFormat, sender: String, logging: Boolean) {
        val data = wrapChatData(sender, chat, Component.text(""), false, receiverIds)
        sendChat(receiverIds, data, format, logging)
    }

    override fun sendChat(receiverIds: List<UUID>, chat: Component, format: ChatFormat, sender: String, logging: Boolean) {
        val data = wrapChatData(sender, "", chat, true, receiverIds)
        sendChat(receiverIds, data, format, logging)
    }

    override fun sendChat(receiverIds: List<UUID>, chat: String, format: ChatFormat, sender: XPlayer, logging: Boolean) {
        val data = wrapChatData(sender, LegacyComponentSerializer.legacySection().deserialize(chat.colorize()), receiverIds)
        sendChat(receiverIds, data, format, logging)
    }

    override fun sendChat(receiverIds: List<UUID>, chat: Component, format: ChatFormat, sender: XPlayer, logging: Boolean) {
        val data = wrapChatData(sender, chat, receiverIds)
        sendChat(receiverIds, data, format, logging)
    }

    fun wrapChatUser(player: Player): ChatSender {
        val chatUserData = chatModeService.getPlayerChatModeData(player.uniqueId)
        val chatMode =
            chatUserData.chatModeId?.let {
                chatModeComponentRegistry.findChatMode(it)
            } ?: chatModeComponentRegistry.getDefaultChatMode()

        return ChatSenderImpl(
            uniqueId = player.uniqueId,
            displayName = LegacyComponentSerializer
                .legacySection()
                .serialize(player.displayName()),
            chatMode = chatMode,
            muteEndAt = chatUserData.muteEndAt ?: 0L
        )
    }

    private fun wrapChatUser(xPlayer: XPlayer): ChatSender {
        val chatUserData = chatModeService.getPlayerChatModeData(xPlayer.getUniqueId())
        val chatMode =
            chatUserData.chatModeId?.let {
                chatModeComponentRegistry.findChatMode(it)
            } ?: chatModeComponentRegistry.getDefaultChatMode()

        return ChatSenderImpl(
            uniqueId = xPlayer.getUniqueId(),
            displayName = xPlayer.getDisplayName(),
            chatMode = chatMode,
            muteEndAt = chatUserData.muteEndAt ?: 0L
        )
    }

    private fun wrapChatData(xPlayer: XPlayer, message: Component, receiverIds: List<UUID>): ChatData {
        val sender = wrapChatUser(xPlayer)
        return object : ChatData {
            override fun getSender(): ChatSender {
                return sender
            }

            override fun getMessage(): Component {
                return message
            }

            override fun getReceivers(): List<XPlayer> {
                return receiverIds.mapNotNull { xCoreService.getServer().findPlayer(it) }
            }
        }
    }

    private fun sendChat(receiverIds: List<UUID>, chatData: ChatData, format: ChatFormat, logging: Boolean) {
        val formattedChat = format.format(chatData)
        if (format is WhisperReceiverFormat) {
            val senderFormattedChat = WhisperSenderFormat.format(chatData)
            if (logging) logger.info(ChatColor.stripColor(LegacyComponentSerializer.legacySection().serialize(senderFormattedChat)))
            server.getPlayer(chatData.getSender().getUniqueId())?.sendMessage(senderFormattedChat)
        }

        sendChat(chatData.getSender().getUniqueId(), receiverIds, formattedChat, logging, format)
    }

    fun sendChat(sender: UUID, receiverIds: List<UUID>, formattedChat: Component, logging: Boolean, format: ChatFormat) {
        val packet = ChatPacket(sender, JSONComponentSerializer.json().serialize(formattedChat), receiverIds, logging, format is WhisperReceiverFormat)
        packetSender.sendPacketAll(packet)
    }

    fun sendChat(sender: UUID, receiverIds: List<UUID>, formattedChat: Component, logging: Boolean, whisper: Boolean) {
        if (receiverIds.isEmpty()) server.broadcast(formattedChat)
        else {
            val receiverPlayers = receiverIds.mapNotNull(server::getPlayer)
            if (receiverPlayers.isNotEmpty()) {
                if (logging) logger.info(ChatColor.stripColor(LegacyComponentSerializer.legacySection().serialize(formattedChat)))
                receiverPlayers.forEach { player ->
                    if (player.isOnline) {
                        if (whisper && receiverIds.first() == player.uniqueId)
                            whisperSenderIds[player.uniqueId] = sender
                        player.sendMessage(formattedChat)
                    }
                }
            }
        }
    }

    private fun wrapChatData(
        sender: String,
        message: String,
        component: Component,
        isComponent: Boolean,
        receiverIds: List<UUID>
    ): ChatData {
        return object : ChatData {
            private val message =
                if (isComponent) component
                else LegacyComponentSerializer
                    .legacySection()
                    .deserialize(message.colorize())

            override fun getSender(): ChatSender {
                return object : ChatSender {
                    override fun getUniqueId(): UUID {
                        return systemId
                    }

                    override fun getDisplayName(): String {
                        return sender
                    }

                    override fun getCurrentChatMode(): ChatMode {
                        TODO("Not yet implemented")
                    }

                    override fun getMuteEndAt(): Long {
                        return -1L
                    }
                }
            }

            override fun getMessage(): Component {
                return this.message
            }

            override fun getReceivers(): List<XPlayer> {
                return receiverIds.mapNotNull { xCoreService.getServer().findPlayer(it) }
            }
        }
    }
}