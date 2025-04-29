package kr.hqservice.x.chat.core.command

import kr.hqservice.framework.global.core.component.Bean
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.api.service.XChatUserService
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Bean
class DefaultChatCommandExecutor(
    private val xChatService: XChatService,
    private val playerChatModeService: XChatUserService
) : CommandExecutor {
    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>?): Boolean {
        if (p0 is Player) {
            if (playerChatModeService.getCurrentChatMode(p0.uniqueId).getKey() == "default") {
                xChatService.sendError(p0.uniqueId, "이미 일반 채팅모드 입니다.")
            } else {
                playerChatModeService.setChatMode(p0.uniqueId, null)
                xChatService.sendInfo(p0.uniqueId, "일반 채팅모드로 변경되었습니다.")
            }
        }
        return true
    }
}