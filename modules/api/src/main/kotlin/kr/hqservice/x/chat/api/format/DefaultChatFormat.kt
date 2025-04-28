package kr.hqservice.x.chat.api.format

import kr.hqservice.x.chat.api.ChatData
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

data object DefaultChatFormat : SystemFormat {
    private val DEFAULT_STYLE = Style.style(
        TextColor.color(0xffffff),
        TextDecoration.ITALIC.withState(false)
    )

    override fun format(chatData: ChatData): Component {
        val base = Component.text()
        base.append(Component.text(chatData.getSender().getDisplayName()))
        base.append(Component.text(": "))
        base.append(chatData.getMessage())
        base.style(DEFAULT_STYLE)
        return base.build()
    }

    override fun getSystemId(): Int {
        return -1
    }
}