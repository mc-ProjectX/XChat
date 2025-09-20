package kr.hqservice.x.chat.core.server.nyang.command

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.api.service.XChatUserService
import kr.hqservice.x.chat.core.command.component.BukkitCommandComponent
import kr.hqservice.x.chat.core.server.nyang.service.ChatDeniedService
import kr.hqservice.x.core.api.service.XCoreService
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

@Component
class DefaultChatDeniedCommand(
    private val xCoreService: XCoreService,
    private val xChatModeService: XChatUserService,
    private val xChatService: XChatService,

    private val chatDeniedService: ChatDeniedService
) : BukkitCommandComponent {
    override fun getBukkitCommand(): String {
        return "전쳇차단"
    }

    override fun onTabComplete(
        p0: CommandSender,
        p1: Command,
        p2: String,
        p3: Array<String>
    ): List<String> {
        return emptyList()
    }

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>): Boolean {
        if (sender !is Player) return false
        if (xChatModeService.getMuteEndAt(sender.uniqueId) != null) {
            xChatModeService.setMute(sender.uniqueId, null)
            xChatService.sendInfo(sender.uniqueId, "§e전쳇차단§f을 해제했습니다.")
        } else {
            xChatModeService.setMute(sender.uniqueId, 1L)
            xChatService.sendInfo(sender.uniqueId, "§e전쳇차단§f을 설정했습니다.")
        }
        return true
    }
}