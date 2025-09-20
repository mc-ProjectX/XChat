package kr.hqservice.x.chat.api

import java.util.UUID

interface XChatSender {
    fun getUniqueId(): UUID

    fun getPrefix(): String

    fun getDisplayName(): String

    fun getOriginalName(): String

    fun toByteArray(): ByteArray
}