package kr.hqservice.x.chat.api

import kr.hqservice.framework.global.core.component.HQComponent
import kr.hqservice.x.core.api.XPlayer
import java.util.function.Predicate

interface ChatMode : HQComponent {
    fun getId(): Int

    fun getName(): String

    fun getFormat(): ChatFormat

    fun getReceiverFilter(): Predicate<XPlayer>
}