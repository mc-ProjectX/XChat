package kr.hqservice.x.chat.core

import kr.hqservice.framework.bukkit.core.extension.colorize
import kr.hqservice.x.chat.api.XChatData
import kr.hqservice.x.chat.api.XChatSender
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

class XChatDataImpl(
    private val sender: XChatSender,
    private val message: Component
) : XChatData {
    constructor(sender: XChatSender, message: String)
            : this(sender, LegacyComponentSerializer.legacySection().deserialize(message.colorize()))

    override fun getSender(): XChatSender {
        return sender
    }

    override fun getMessage(): Component {
        return message
    }
}