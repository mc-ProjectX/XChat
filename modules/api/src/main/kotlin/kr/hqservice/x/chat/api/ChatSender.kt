package kr.hqservice.x.chat.api

import java.util.UUID

interface ChatSender {
    fun getUniqueId(): UUID

    fun getDisplayName(): String

    fun getCurrentChatMode(): ChatMode

    fun getMuteEndAt(): Long
}