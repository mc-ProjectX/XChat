package kr.hqservice.x.chat.core

import kr.hqservice.x.chat.api.XChatSender
import java.util.*

class XChatSenderImpl(
    private val uniqueId: UUID,
    private val displayName: String
) : XChatSender {
    override fun getUniqueId(): UUID {
        return uniqueId
    }

    override fun getDisplayName(): String {
        return displayName
    }
}