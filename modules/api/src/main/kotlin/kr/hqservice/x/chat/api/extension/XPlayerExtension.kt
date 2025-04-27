package kr.hqservice.x.chat.api.extension

import kr.hqservice.framework.bukkit.core.extension.colorize
import kr.hqservice.x.chat.api.ChatData
import kr.hqservice.x.chat.api.ChatFormat
import kr.hqservice.x.chat.api.ChatMode
import kr.hqservice.x.chat.api.ChatSender
import kr.hqservice.x.core.api.XPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.util.*

fun XPlayer.send(message: String, format: ChatFormat, displayName: String = "") {

}

private fun createCustomChatData(message: String, displayName: String): ChatData = object : ChatData {
    override fun getSender(): ChatSender {
        return object : ChatSender {
            override fun getUniqueId(): UUID {
                return UUID.randomUUID()
            }

            override fun getDisplayName(): String {
                return displayName
            }

            override fun getCurrentChatMode(): ChatMode {
                throw UnsupportedOperationException("Not yet implemented")
            }

            override fun getMuteEndAt(): Long {
                return -1
            }
        }
    }

    override fun getMessage(): Component {
        return LegacyComponentSerializer.legacySection().deserialize(message.colorize())
    }

    override fun getReceivers(): List<XPlayer> {
        return emptyList()
    }
}