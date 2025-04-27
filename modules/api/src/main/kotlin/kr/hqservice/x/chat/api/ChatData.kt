package kr.hqservice.x.chat.api

import kr.hqservice.x.core.api.XPlayer
import net.kyori.adventure.text.Component

interface ChatData {
    fun getSender(): ChatSender

    fun getMessage(): Component

    fun getReceivers(): List<XPlayer>
}