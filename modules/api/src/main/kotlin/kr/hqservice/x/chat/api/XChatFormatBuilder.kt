package kr.hqservice.x.chat.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEventSource
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

class XChatFormatBuilder {
    private var color: Int = 0xffffff
    private var prefixColor: Int? = null
    private var prefix: String = ""
    private var displayNameFormat: (XChatData) -> String = { data -> data.getSender().getDisplayName() }
    private var separator: String = ": "

    private var hover: ((XChatData) -> HoverEventSource<*>)? = null
    private var click: ((XChatData) -> ClickEvent)? = null

    private var receiveEvent: ((XChatSender, Player) -> Unit)? = null

    fun setColor(color: Int): XChatFormatBuilder {
        this.color = color
        return this
    }

    fun setPrefixColor(prefixColor: Int?): XChatFormatBuilder {
        this.prefixColor = prefixColor
        return this
    }

    fun setPrefix(prefix: String): XChatFormatBuilder {
        this.prefix = prefix
        return this
    }

    fun setAfterPrefix(format: (XChatData) -> String): XChatFormatBuilder {
        this.displayNameFormat = format
        return this
    }

    fun setSeparator(separator: String): XChatFormatBuilder {
        this.separator = separator
        return this
    }

    fun setHover(hoverEvent: ((XChatData) -> HoverEventSource<*>)?): XChatFormatBuilder {
        this.hover = hoverEvent
        return this
    }

    fun setClick(click: ((XChatData) -> ClickEvent)?): XChatFormatBuilder {
        this.click = click
        return this
    }

    fun setReceiveEvent(receiveEvent: (XChatSender, Player) -> Unit): XChatFormatBuilder {
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
                var prefix = Component.text(prefix)
                if (prefixColor != null) prefix = prefix.style(Style.style(TextColor.color(prefixColor!!)))
                base.append(prefix)
                base.append(Component.text(xChatData.run(displayNameFormat)))
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