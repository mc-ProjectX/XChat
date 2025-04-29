package kr.hqservice.x.chat.core

import kr.hqservice.x.chat.api.XChatData
import kr.hqservice.x.chat.api.XChatSender
import kr.hqservice.x.core.api.XPlayer
import net.kyori.adventure.text.Component

class XChatWhisperData(
    private val sender: XChatSender,
    private val receiver: XPlayer,
    private val message: Component
) : XChatData {
    override fun getSender(): XChatSender {
        return sender
    }

    override fun getMessage(): Component {
        return message
    }

    fun getReceiver(): XPlayer {
        return receiver
    }
}