package kr.hqservice.x.chat.core.command.component.handler

import kr.hqservice.framework.bukkit.core.HQBukkitPlugin
import kr.hqservice.framework.global.core.component.handler.ComponentHandler
import kr.hqservice.framework.global.core.component.handler.HQComponentHandler
import kr.hqservice.x.chat.core.command.component.BukkitCommandComponent

@ComponentHandler
class BukkitCommandHandler(
    private val plugin: HQBukkitPlugin
) : HQComponentHandler<BukkitCommandComponent> {
    override fun setup(element: BukkitCommandComponent) {
        val command = element.getBukkitCommand()

        plugin.getCommand(command)?.apply {
            setExecutor(element)
            if (tabCompleter != null) {
                this.tabCompleter = element
            }
        }
    }
}