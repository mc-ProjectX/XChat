package kr.hqservice.x.chat.core.command

import kr.hqservice.framework.global.core.component.Bean
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.api.service.XChatUserService
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Bean
class OperatorChatCommandExecutor(
    private val xChatService: XChatService,
    private val playerChatModeService: XChatUserService
) : CommandExecutor {
    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>?): Boolean {
        if (p0 is Player) {
            if (playerChatModeService.getCurrentChatMode(p0.uniqueId)?.getKey() == "operator") {
                playerChatModeService.setChatMode(p0.uniqueId, null)
            } else playerChatModeService.setChatMode(p0.uniqueId, xChatService.findChatMode("operator"))
        }
        return true
    }
}