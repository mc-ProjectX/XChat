package kr.hqservice.x.chat.api

interface XChatModeWithSpy : XChatMode {
    fun getSpyFormat(): XChatFormat
}