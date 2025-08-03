package kr.hqservice.x.chat.core.def.mode

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.XChatFormat
import kr.hqservice.x.chat.api.XChatMode
import kr.hqservice.x.chat.api.XChatSender
import kr.hqservice.x.chat.api.XChatFormatBuilder
import kr.hqservice.x.core.api.XPlayer
import org.bukkit.Server

@Component
class OperatorChatMode(
    private val server: Server
) : XChatMode {
    private val format = XChatFormatBuilder()
        .setPrefix("[관리자] ")
        .setColor(0xff5792).build()

    override fun getKey(): String {
        return "operator"
    }

    override fun getFormat(): XChatFormat {
        return format
    }

    override fun hasSendPermission(sender: XChatSender): Boolean {
        return server.operators.any { it.uniqueId == sender.getUniqueId() }
    }

    override fun getReceiverFilter(sender: XChatSender, extraData: Any?): (XPlayer) -> Boolean {
        return { receiver -> server.operators.any { receiver.getUniqueId() == it.uniqueId } }
    }
}