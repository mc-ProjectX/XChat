package kr.hqservice.x.chat.core.service

import kr.hqservice.framework.bukkit.core.extension.colorize
import kr.hqservice.framework.global.core.component.Service
import kr.hqservice.framework.netty.api.PacketSender
import kr.hqservice.x.chat.api.ChatData
import kr.hqservice.x.chat.api.ChatFormat
import kr.hqservice.x.chat.api.ChatMode
import kr.hqservice.x.chat.api.ChatSender
import kr.hqservice.x.chat.api.service.ChatService
import kr.hqservice.x.chat.core.ChatSenderImpl
import kr.hqservice.x.chat.core.component.registry.ChatModeComponentRegistry
import kr.hqservice.x.chat.core.database.service.PlayerChatModeService
import kr.hqservice.x.chat.core.network.packet.ChatPacket
import kr.hqservice.x.core.api.XPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Server
import org.bukkit.entity.Player
import java.util.*

@Service
class ChatServiceImpl(
    private val server: Server,
    private val packetSender: PacketSender,

    private val chatModeService: PlayerChatModeService,
    private val chatModeComponentRegistry: ChatModeComponentRegistry
) : ChatService {
    private val systemId = UUID.randomUUID()

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

    override fun sendChat(receiverIds: Set<UUID>, chat: String, format: ChatFormat, sender: String) {
        val data = wrapChatData(sender, chat, Component.text(""), false)
        sendChat(receiverIds, data, format)
    }

    override fun sendChat(receiverIds: Set<UUID>, chat: Component, format: ChatFormat, sender: String) {
        val data = wrapChatData(sender, "", chat, true)
        sendChat(receiverIds, data, format)
    }

    override fun sendChat(receiverIds: Set<UUID>, chat: String, format: ChatFormat, sender: XPlayer) {
        val data = wrapChatData(sender, LegacyComponentSerializer.legacySection().deserialize(chat.colorize()))
        sendChat(receiverIds, data, format)
    }

    override fun sendChat(receiverIds: Set<UUID>, chat: Component, format: ChatFormat, sender: XPlayer) {
        val data = wrapChatData(sender, chat)
        sendChat(receiverIds, data, format)
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

    private fun wrapChatData(xPlayer: XPlayer, message: Component): ChatData {
        val sender = wrapChatUser(xPlayer)
        return object : ChatData {
            override fun getSender(): ChatSender {
                return sender
            }

            override fun getMessage(): Component {
                return message
            }

            override fun getReceivers(): List<XPlayer> {
                return emptyList()
            }
        }
    }

    private fun sendChat(receiverIds: Set<UUID>, chatData: ChatData, format: ChatFormat) {
        val formattedChat = format.format(chatData)
        sendChat(receiverIds, formattedChat)
    }

    fun sendChat(receiverIds: Set<UUID>, formattedChat: Component, handled: Boolean = false) {
        if (!handled) {
            val packet = ChatPacket(JSONComponentSerializer.json().serialize(formattedChat), receiverIds)
            packetSender.sendPacketAll(packet)
        } else {
            if (receiverIds.isEmpty()) server.broadcast(formattedChat)
            else receiverIds
                .mapNotNull(server::getPlayer)
                .forEach { it.sendMessage(formattedChat) }
        }
    }

    private fun wrapChatData(
        sender: String,
        message: String,
        component: Component,
        isComponent: Boolean
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
                return emptyList()
            }
        }
    }
}