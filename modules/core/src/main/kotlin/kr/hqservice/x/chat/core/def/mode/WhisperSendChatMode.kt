package kr.hqservice.x.chat.core.def.mode

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.XChatFormat
import kr.hqservice.x.chat.api.XChatMode
import kr.hqservice.x.chat.api.XChatFormatBuilder

@Component
class WhisperSendChatMode : XChatMode {
    private val format = XChatFormatBuilder()
        .setAfterPrefix { "${it.getSender().getDisplayName()} 님에게 보냄" }
        .setColor(0xadffe2).build()

    override fun getKey(): String {
        return "whisper_send"
    }

    override fun getFormat(): XChatFormat {
        return format
    }

    override fun isLoggingEnabled(): Boolean {
        return false
    }
}