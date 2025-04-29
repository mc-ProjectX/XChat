package kr.hqservice.x.chat.core

import kr.hqservice.x.chat.api.XChatSender
import java.util.*

class XChatSenderImpl(
    private val uniqueId: UUID,
    private val displayName: String,
    private val originalName: String
) : XChatSender {
    override fun getUniqueId(): UUID {
        return uniqueId
    }

    override fun getDisplayName(): String {
        return displayName
    }

    override fun getOriginalName(): String {
        return originalName
    }
}