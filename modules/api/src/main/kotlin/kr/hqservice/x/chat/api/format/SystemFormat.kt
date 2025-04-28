package kr.hqservice.x.chat.api.format

import kr.hqservice.x.chat.api.ChatFormat

sealed interface SystemFormat : ChatFormat {
    override fun getModeId(): Int = getSystemId()

    fun getSystemId(): Int
}