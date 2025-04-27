package kr.hqservice.x.chat.api.format

import kr.hqservice.x.chat.api.ChatData
import kr.hqservice.x.chat.api.ChatFormat
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

object ErrorFormat : ChatFormat {
    private val ERROR_MESSAGE_STYLE = Style.style(
        TextColor.color(0xcf3e54),
        TextDecoration.ITALIC.withState(false)
    )

    override fun format(chatData: ChatData): Component {
        val base = Component.text()
        base.append(Component.text("[!] "))
        base.append(Component.text(chatData.getSender().getDisplayName().run { if (isNotEmpty()) "$this " else this }))
        base.append(chatData.getMessage())
        base.style(ERROR_MESSAGE_STYLE)
        return base.build()
    }
}