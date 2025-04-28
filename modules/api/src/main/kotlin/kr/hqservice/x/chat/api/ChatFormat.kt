package kr.hqservice.x.chat.api

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface ChatFormat {
    fun getModeId(): Int

    fun hover(chatData: ChatData): Component? = null

    fun click(chatData: ChatData): Component? = null

    fun format(chatData: ChatData): Component

    fun received(player: Player) = Unit
}