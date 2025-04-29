package kr.hqservice.x.chat.core.command.component.executor

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.api.service.XChatUserService
import kr.hqservice.x.chat.core.command.component.BukkitCommandComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Component
class SpyToggleCommandExecutor(
    private val xChatService: XChatService,
    private val playerChatModeService: XChatUserService
) : BukkitCommandComponent {
    override fun getBukkitCommand(): String {
        return "스파이"
    }

    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>?): Boolean {
        if (p0 is Player) {
            playerChatModeService.setSpyMode(p0.uniqueId, !playerChatModeService.isSpyMode(p0.uniqueId))
            xChatService.sendInfo(p0.uniqueId, "스파이 모드가 ${if (playerChatModeService.isSpyMode(p0.uniqueId)) "활성화" else "비활성화"} 되었습니다.")
        }
        return true
    }
}