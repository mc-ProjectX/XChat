package kr.hqservice.x.chat.core.registry

import kr.hqservice.framework.global.core.component.Bean
import kr.hqservice.x.chat.api.XChatSender
import java.util.UUID

@Bean
class WhisperReplyRegistry {
    private val replyMap = mutableMapOf<UUID, UUID>()

    fun addReply(sender: UUID, reply: XChatSender) {
        replyMap[sender] = reply.getUniqueId()
    }

    fun getReply(sender: UUID): UUID? {
        return replyMap[sender]
    }

    fun removeReply(sender: UUID) {
        replyMap.remove(sender)
    }
}