package kr.hqservice.x.chat.core

import kr.hqservice.x.chat.api.ChatData
import kr.hqservice.x.chat.api.ChatSender
import kr.hqservice.x.core.api.XPlayer
import net.kyori.adventure.text.Component

class ChatDataImpl(
    private val sender: ChatSender,
    private val message: Component,
    private val receivers: List<XPlayer>,
) : ChatData {
    override fun getSender(): ChatSender {
        return sender
    }

    override fun getMessage(): Component {
        return message
    }

    override fun getReceivers(): List<XPlayer> {
        return receivers
    }
}