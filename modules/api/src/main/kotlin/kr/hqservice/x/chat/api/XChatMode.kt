package kr.hqservice.x.chat.api

import kr.hqservice.framework.global.core.component.HQComponent
import kr.hqservice.x.core.api.XPlayer

interface XChatMode : HQComponent {
    fun getKey(): String

    fun getFormat(): XChatFormat

    fun hasSendPermission(sender: XChatSender): Boolean = true

    fun permissionDeniedMessage(): String = "현재 채팅 모드로 채팅을 보낼 수 없습니다."

    fun getReceiverFilter(sender: XChatSender): (XPlayer) -> Boolean = { _ -> true }

    fun isLoggingEnabled(): Boolean = true
}