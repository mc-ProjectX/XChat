package kr.hqservice.x.chat.core.def.mode

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.XChatFormat
import kr.hqservice.x.chat.api.XChatMode
import kr.hqservice.x.chat.api.XChatSender
import kr.hqservice.x.chat.core.def.format.DefaultFormatBuilder
import kr.hqservice.x.core.api.XPlayer
import org.bukkit.Server

@Component
class DefaultChatMode(
    private val server: Server
) : XChatMode {
    private val format = DefaultFormatBuilder()
        .build()

    override fun getKey(): String {
        return "default"
    }

    override fun getFormat(): XChatFormat {
        return format
    }

    override fun getReceiverFilter(sender: XChatSender): (XPlayer) -> Boolean {
        val senderPlayer = server.getPlayer(sender.getUniqueId()) ?: return { _ -> false }
        return { receiver ->
            val receiverPlayer = server.getPlayer(receiver.getUniqueId())
            if (receiverPlayer != null) {
                receiverPlayer.location.world.name == senderPlayer.location.world.name
            } else false
        }
    }
}