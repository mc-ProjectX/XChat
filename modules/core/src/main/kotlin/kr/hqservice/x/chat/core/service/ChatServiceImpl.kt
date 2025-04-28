package kr.hqservice.x.chat.core.service

import kr.hqservice.framework.bukkit.core.extension.colorize
import kr.hqservice.framework.global.core.component.Service
import kr.hqservice.x.chat.api.ChatData
import kr.hqservice.x.chat.api.ChatFormat
import kr.hqservice.x.chat.api.ChatMode
import kr.hqservice.x.chat.api.ChatSender
import kr.hqservice.x.chat.api.format.*
import kr.hqservice.x.chat.api.service.ChatService
import kr.hqservice.x.chat.core.ChatSenderImpl
import kr.hqservice.x.chat.core.component.registry.ChatModeComponentRegistry
import kr.hqservice.x.chat.core.database.service.PlayerChatModeService
import kr.hqservice.x.core.api.XPlayer
import kr.hqservice.x.core.api.service.XCoreService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import java.util.*

@Service
class ChatServiceImpl(
    private val xCoreService: XCoreService,

    private val chatModeService: PlayerChatModeService,
    private val chatModeComponentRegistry: ChatModeComponentRegistry,
    private val chatSendService: ChatSendService
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

    override fun sendChat(receiverIds: List<UUID>, chat: String, format: ChatFormat, sender: String, logging: Boolean) {
        val data = wrapChatData(sender, chat, Component.text(""), false, receiverIds)
        chatSendService.sendChat(receiverIds, data, format, logging)
    }

    override fun sendChat(receiverIds: List<UUID>, chat: Component, format: ChatFormat, sender: String, logging: Boolean) {
        val data = wrapChatData(sender, "", chat, true, receiverIds)
        chatSendService.sendChat(receiverIds, data, format, logging)
    }

    override fun sendChat(receiverIds: List<UUID>, chat: String, format: ChatFormat, sender: XPlayer, logging: Boolean) {
        val data = wrapChatData(sender, LegacyComponentSerializer.legacySection().deserialize(chat.colorize()), receiverIds)
        chatSendService.sendChat(receiverIds, data, format, logging)
    }

    override fun sendChat(receiverIds: List<UUID>, chat: Component, format: ChatFormat, sender: XPlayer, logging: Boolean) {
        val data = wrapChatData(sender, chat, receiverIds)
        chatSendService.sendChat(receiverIds, data, format, logging)
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