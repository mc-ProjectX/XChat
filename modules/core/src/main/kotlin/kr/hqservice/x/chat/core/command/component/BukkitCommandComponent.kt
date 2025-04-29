package kr.hqservice.x.chat.core.command.component

import kr.hqservice.framework.global.core.component.HQComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

interface BukkitCommandComponent : HQComponent, TabExecutor {
    fun getBukkitCommand(): String

    override fun onTabComplete(
        p0: CommandSender,
        p1: Command,
        p2: String,
        p3: Array<String>
    ): List<String> {
        return emptyList()
    }
}