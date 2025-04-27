package kr.hqservice.x.chat.core.component

import kr.hqservice.x.chat.api.ChatFormat
import kr.hqservice.x.chat.api.ChatMode
import kr.hqservice.x.chat.api.format.DefaultChatFormat
import kr.hqservice.x.core.api.XPlayer
import java.util.function.Predicate

object DefaultChatModeComponent : ChatMode {
    private val filter = Predicate<XPlayer> { true }

    override fun getId(): Int {
        return -1
    }

    override fun getName(): String {
        return "전체"
    }

    override fun getFormat(): ChatFormat {
        return DefaultChatFormat
    }

    override fun getReceiverFilter(): Predicate<XPlayer> {
        return filter
    }
}