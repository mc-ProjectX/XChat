package kr.hqservice.x.chat.core.command.component.executor

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.api.service.XChatUserService
import kr.hqservice.x.chat.core.command.component.BukkitCommandComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Component
class GuideChatCommandExecutor(
    private val xChatService: XChatService,
    private val playerChatModeService: XChatUserService
) : BukkitCommandComponent {
    override fun getBukkitCommand(): String {
        return "가이드채팅"
    }

    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>?): Boolean {
        if (p0 is Player) {
            if (playerChatModeService.getCurrentChatMode(p0.uniqueId).getKey() == "guide") {
                playerChatModeService.setChatMode(p0.uniqueId, null)
                xChatService.sendInfo(p0.uniqueId, "전체 채팅모드로 변경되었습니다.")
            } else {
                playerChatModeService.setChatMode(p0.uniqueId, xChatService.findChatMode("guide"))
                xChatService.sendInfo(p0.uniqueId, "가이드 채팅모드로 변경되었습니다.")
            }
        }
        return true
    }
}