package kr.hqservice.x.chat.api.format

import kr.hqservice.x.chat.api.ChatData
import kr.hqservice.x.chat.api.ChatFormat
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

object WhisperReceiverFormat : ChatFormat {
    private val WHISPER_MESSAGE_STYLE = Style.style(
        TextColor.color(0xddff8c),
        TextDecoration.ITALIC.withState(false)
    )

    override fun format(chatData: ChatData): Component {
        val base = Component.text()
        base.append(Component.text("${chatData.getSender().getDisplayName()} 님에게 받음: "))
        base.append(chatData.getMessage())
        base.style(WHISPER_MESSAGE_STYLE)
        return base.build()
    }
}