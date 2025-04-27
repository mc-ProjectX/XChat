package kr.hqservice.x.chat.api.service

import kr.hqservice.x.chat.api.ChatFormat
import kr.hqservice.x.chat.api.ChatMode
import kr.hqservice.x.core.api.XPlayer
import net.kyori.adventure.text.Component
import java.util.UUID

interface ChatService {
    fun setChatMode(userId: UUID, chatMode: ChatMode)

    fun findChatMode(modeId: Int): ChatMode?

    fun getChatModes(): List<ChatMode>

    fun sendChat(receiverIds: Set<UUID>, chat: String, format: ChatFormat, sender: String = "")

    fun sendChat(receiverIds: Set<UUID>, chat: Component, format: ChatFormat, sender: String = "")

    fun sendChat(receiverIds: Set<UUID>, chat: String, format: ChatFormat, sender: XPlayer)

    fun sendChat(receiverIds: Set<UUID>, chat: Component, format: ChatFormat, sender: XPlayer)
}