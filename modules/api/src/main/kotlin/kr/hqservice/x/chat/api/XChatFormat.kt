package kr.hqservice.x.chat.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEventSource
import org.bukkit.entity.Player

interface XChatFormat {
    fun hover(xChatData: XChatData): HoverEventSource<*>? = null

    fun click(xChatData: XChatData): ClickEvent? = null

    fun format(xChatData: XChatData): Component

    fun received(sender: XChatSender, receiver: Player) = Unit
}