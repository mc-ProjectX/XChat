package kr.hqservice.x.chat.core

import kr.hqservice.x.chat.api.ChatMode
import kr.hqservice.x.chat.api.ChatSender
import java.util.*

class ChatSenderImpl(
    private val uniqueId: UUID,
    private val displayName: String,
    private val chatMode: ChatMode,
    private val muteEndAt: Long
) : ChatSender {
    override fun getUniqueId(): UUID {
        return uniqueId
    }

    override fun getDisplayName(): String {
        return displayName
    }

    override fun getCurrentChatMode(): ChatMode {
        return chatMode
    }

    override fun getMuteEndAt(): Long {
        return muteEndAt
    }
}