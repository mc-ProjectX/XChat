package kr.hqservice.x.chat.api

import kr.hqservice.framework.global.core.component.HQComponent
import kr.hqservice.x.core.api.XPlayer
import java.util.UUID

interface XChatMode : HQComponent {
    fun getKey(): String

    fun getFormat(): XChatFormat

    fun hasSendPermission(sender: XChatSender): Boolean = true

    fun permissionDeniedMessage(): String = "현재 채팅 모드로 채팅을 보낼 수 없습니다."

    fun getReceiverFilter(sender: XChatSender, extraData: Any?): (XPlayer) -> Boolean = { _ -> true }

    fun isLoggingEnabled(): Boolean = true

    fun extraData(senderId: UUID): Any? = null

    fun readExtraData(byteArray: ByteArray): Any? = null

    fun writeExtraData(extraData: Any): ByteArray? = null
}