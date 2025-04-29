package kr.hqservice.x.chat.core.def.format

import kr.hqservice.x.chat.api.XChatData
import kr.hqservice.x.chat.api.XChatFormat
import kr.hqservice.x.chat.api.XChatSender
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEventSource
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

class DefaultFormatBuilder() {
    private var color: Int = 0xffffff
    private var prefix: String = ""
    private var displayNameFormat: (String) -> String = { displayName -> displayName }
    private var separator: String = ": "

    private var hover: ((XChatData) -> HoverEventSource<*>)? = null
    private var click: ((XChatData) -> ClickEvent)? = null

    private var receiveEvent: ((XChatSender, Player) -> Unit)? = null

    fun setColor(color: Int): DefaultFormatBuilder {
        this.color = color
        return this
    }

    fun setPrefix(prefix: String): DefaultFormatBuilder {
        this.prefix = prefix
        return this
    }

    fun setDisplayNameFormat(format: (String) -> String): DefaultFormatBuilder {
        this.displayNameFormat = format
        return this
    }

    fun setSeparator(separator: String): DefaultFormatBuilder {
        this.separator = separator
        return this
    }

    fun setHover(hoverEvent: ((XChatData) -> HoverEventSource<*>)?): DefaultFormatBuilder {
        this.hover = hoverEvent
        return this
    }

    fun setClick(click: ((XChatData) -> ClickEvent)?): DefaultFormatBuilder {
        this.click = click
        return this
    }

    fun setReceiveEvent(receiveEvent: (XChatSender, Player) -> Unit): DefaultFormatBuilder {
        this.receiveEvent = receiveEvent
        return this
    }

    fun build(): XChatFormat {
        val style = Style.style(
            TextColor.color(color),
            TextDecoration.ITALIC.withState(false)
        )

        return object : XChatFormat {
            override fun hover(xChatData: XChatData): HoverEventSource<*>? {
                return hover?.invoke(xChatData)
            }

            override fun click(xChatData: XChatData): ClickEvent? {
                return click?.invoke(xChatData)
            }

            override fun format(xChatData: XChatData): Component {
                val base = Component.text()
                base.append(Component.text(prefix))
                base.append(Component.text(xChatData.getSender().getDisplayName().run(displayNameFormat)))
                base.append(Component.text(separator))
                base.append(xChatData.getMessage())
                base.style(style)

                val hoverEvent = hover(xChatData)
                val clickEvent = click(xChatData)

                if (hoverEvent != null) base.hoverEvent(hoverEvent)
                if (clickEvent != null) base.clickEvent(clickEvent)
                return base.build()
            }

            override fun received(sender: XChatSender, receiver: Player) {
                receiveEvent?.invoke(sender, receiver)
            }
        }
    }
}