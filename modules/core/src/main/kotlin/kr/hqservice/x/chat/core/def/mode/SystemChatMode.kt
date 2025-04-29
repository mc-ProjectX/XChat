package kr.hqservice.x.chat.core.def.mode

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.XChatFormat
import kr.hqservice.x.chat.api.XChatMode
import kr.hqservice.x.chat.api.XChatSender
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.core.def.format.DefaultFormatBuilder

@Component
class SystemChatMode(
    private val xChatService: XChatService
) : XChatMode {
    private val format = DefaultFormatBuilder()
        .setColor(0xdbdbce)
        .setSeparator("")
        .build()

    override fun getKey(): String {
        return "system"
    }

    override fun getFormat(): XChatFormat {
        return format
    }

    override fun hasSendPermission(sender: XChatSender): Boolean {
        return xChatService.getConsoleSender().getUniqueId() == sender.getUniqueId()
    }

    override fun isLoggingEnabled(): Boolean {
        return false
    }
}