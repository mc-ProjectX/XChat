package kr.hqservice.x.chat.api

import net.kyori.adventure.text.Component

interface XChatData {
    fun getSender(): XChatSender

    fun getMessage(): Component
}