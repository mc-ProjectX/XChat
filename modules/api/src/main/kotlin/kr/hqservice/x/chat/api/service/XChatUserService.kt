package kr.hqservice.x.chat.api.service

import kr.hqservice.x.chat.api.XChatMode
import java.util.*

interface XChatUserService {
    fun getCurrentChatMode(userId: UUID): XChatMode

    fun getMuteEndAt(userId: UUID): Long?

    fun isSpyMode(userId: UUID): Boolean

    fun setChatMode(userId: UUID, xChatMode: XChatMode?)

    fun setMute(userId: UUID, muteEndAt: Long?)

    fun setSpyMode(userId: UUID, spyMode: Boolean)
}