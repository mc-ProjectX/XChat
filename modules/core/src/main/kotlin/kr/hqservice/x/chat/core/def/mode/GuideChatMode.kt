package kr.hqservice.x.chat.core.def.mode

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.XChatFormat
import kr.hqservice.x.chat.api.XChatMode
import kr.hqservice.x.chat.api.XChatSender
import kr.hqservice.x.chat.api.XChatFormatBuilder
import kr.hqservice.x.core.api.XPlayer
import org.bukkit.Server

@Component
class GuideChatMode(
    private val server: Server
) : XChatMode {
    private val format = XChatFormatBuilder()
        // Server Front
        .setPrefix("铭")
        .setPrefixColor(0xffffff)

        .setColor(0xffe97d).build()

    override fun getKey(): String {
        return "guide"
    }

    override fun getFormat(): XChatFormat {
        return format
    }

    override fun hasSendPermission(sender: XChatSender): Boolean {
        return server.operators.any { it.uniqueId == sender.getUniqueId() } || server.getPlayer(sender.getUniqueId())?.hasPermission("project_x.group.guide") == true
    }

    override fun getReceiverFilter(sender: XChatSender, extraData: Any?): (XPlayer) -> Boolean {
        return { receiver -> server.operators.any { receiver.getUniqueId() == it.uniqueId }
                || server.getPlayer(receiver.getUniqueId())?.hasPermission("project_x.group.guide") == true }
    }
}