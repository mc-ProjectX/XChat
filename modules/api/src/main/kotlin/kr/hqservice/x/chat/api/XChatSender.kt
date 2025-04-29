package kr.hqservice.x.chat.api

import java.util.UUID

interface XChatSender {
    fun getUniqueId(): UUID

    fun getDisplayName(): String
}