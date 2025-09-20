package kr.hqservice.x.chat.core.server.nyang.command

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.core.command.component.BukkitCommandComponent
import kr.hqservice.x.chat.core.server.nyang.service.ChatDeniedService
import kr.hqservice.x.core.api.service.XCoreService
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

@Component
class ChatDeniedCommand(
    private val xCoreService: XCoreService,
    private val xChatService: XChatService,

    private val chatDeniedService: ChatDeniedService
) : BukkitCommandComponent {
    override fun getBukkitCommand(): String {
        return "채팅차단"
    }

    override fun onTabComplete(
        p0: CommandSender,
        p1: Command,
        p2: String,
        p3: Array<String>
    ): List<String> {
        val set = xCoreService.getServer().getPlayers().map { it.getDisplayName() }.toSet() +
                xCoreService.getServer().getPlayers().map { it.getName() }.toSet()
        return if (p3.size in 0 .. 1) return StringUtil.copyPartialMatches(p3[0], set.toList(), mutableListOf())
        else emptyList()
    }

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>): Boolean {
        if (sender !is Player) return false

        if (args.isEmpty()) {
            xChatService.sendError(sender.uniqueId, "대상 플레이어를 입력해주세요.")
        } else {
            val target = args[0]
            val targetXPlayer = xCoreService
                .getServer()
                .getPlayers()
                .firstOrNull { it.getName().equals(target, true) || it.getDisplayName().equals(target, true) }
            if (targetXPlayer != null) {
                val data = chatDeniedService.getCache(sender.uniqueId)
                if (data == null) return false

                if (data.targets.contains(targetXPlayer.getUniqueId())) {
                    chatDeniedService.removeChatDenied(sender.uniqueId, targetXPlayer.getUniqueId())
                    xChatService.sendInfo(sender.uniqueId, "§e${targetXPlayer.getDisplayName()}§f님을 차단 해제했습니다.")
                } else {
                    chatDeniedService.addChatDenied(sender.uniqueId, targetXPlayer.getUniqueId())
                    xChatService.sendInfo(sender.uniqueId, "§e${targetXPlayer.getDisplayName()}§f님을 차단했습니다.")
                }
            } else {
                xChatService.sendError(sender.uniqueId, "대상 플레이어를 찾을 수 없습니다.")
            }
        }

        return true
    }
}