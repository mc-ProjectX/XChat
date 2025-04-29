package kr.hqservice.x.chat.core.def.mode

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.XChatFormat
import kr.hqservice.x.chat.api.XChatMode
import kr.hqservice.x.chat.api.XChatSender
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.core.def.format.DefaultFormatBuilder
import org.bukkit.Sound

@Component
class ErrorChatMode(
    private val xChatService: XChatService
) : XChatMode {
    private val format = DefaultFormatBuilder()
        .setPrefix("[!] ")
        .setDisplayNameFormat { "" }
        .setSeparator("")
        .setReceiveEvent { _, it -> it.playSound(it.location, Sound.BLOCK_NOTE_BLOCK_BASS, .7f, .7f) }
        .setColor(0xcf3e54).build()

    override fun getKey(): String {
        return "error"
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