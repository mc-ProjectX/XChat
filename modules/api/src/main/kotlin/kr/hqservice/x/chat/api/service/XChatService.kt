package kr.hqservice.x.chat.api.service

import kr.hqservice.x.chat.api.XChatMode
import kr.hqservice.x.chat.api.DefaultChatMode
import kr.hqservice.x.chat.api.XChatSender
import kr.hqservice.x.core.api.XPlayer
import net.kyori.adventure.text.Component
import java.util.*

interface XChatService {
    fun getConsoleSender(label: String = ""): XChatSender

    fun findChatMode(modeId: String): XChatMode?

    fun getChatModes(): List<XChatMode>

    fun getDefaultChatMode(modeType: DefaultChatMode): XChatMode

    fun sendError(targetId: UUID, message: String)

    fun sendInfo(targetId: UUID, message: String)

    fun sendChat(targetId: UUID?, senderId: UUID, chat: String, xChatMode: XChatMode, logging: Boolean = true): Boolean

    fun sendChat(targetId: UUID?, senderId: UUID, chat: Component, xChatMode: XChatMode, logging: Boolean = true): Boolean

    fun sendChat(targetId: UUID?, sender: XChatSender, chat: String, xChatMode: XChatMode, logging: Boolean = true): Boolean

    fun sendChat(targetId: UUID?, sender: XChatSender, chat: Component, xChatMode: XChatMode, logging: Boolean = true): Boolean
}