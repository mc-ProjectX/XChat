package kr.hqservice.x.chat.api.format

import kr.hqservice.x.chat.api.ChatData
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

data object WhisperSenderFormat : SystemFormat {
    private val WHISPER_MESSAGE_STYLE = Style.style(
        TextColor.color(0xadffe2),
        TextDecoration.ITALIC.withState(false)
    )

    override fun format(chatData: ChatData): Component {
        val base = Component.text()
        base.append(Component.text("${chatData.getReceivers().first().getDisplayName()} 님에게 보냄: "))
        base.append(chatData.getMessage())
        base.style(WHISPER_MESSAGE_STYLE)
        return base.build()
    }

    override fun getSystemId(): Int {
        return -5
    }
}