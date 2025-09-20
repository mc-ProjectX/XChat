package kr.hqservice.x.chat.core.command.component.executor

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.DefaultChatMode
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.core.XChatPlayerSender
import kr.hqservice.x.chat.core.command.component.BukkitCommandComponent
import kr.hqservice.x.chat.core.hook.EconomyHook
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Component
class SpeakerCommandExecutor(
    private val economyHook: EconomyHook,
    private val xChatService: XChatService
) : BukkitCommandComponent {
    private val cooldown = mutableMapOf<String, Long>()

    override fun getBukkitCommand(): String {
        return "확성기"
    }

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            if (sender is Player) {
                xChatService.sendError(sender.uniqueId, "할 말을 입력해주세요.")
            } else sender.sendMessage("§c할 말을 입력해주세요.")
        } else if (sender is Player) {
            if (!sender.isOp) {
                val currentTime = System.currentTimeMillis()
                if (cooldown.containsKey(sender.name) && currentTime - cooldown[sender.name]!! < 300000) {
                    xChatService.sendError(sender.uniqueId, "5분 마다 사용할 수 있습니다.")
                    return true
                }
                cooldown[sender.name] = currentTime
            }

            val user = XChatPlayerSender(sender.uniqueId, "", sender.displayName, sender.name)

            if (economyHook.hasMoney(sender, 1000L) || sender.hasPermission("nyang.speaker.permission")) {
                if (!sender.hasPermission("nyang.speaker.permission")) economyHook.withdrawMoney(sender, 1000L)
                xChatService.sendChat(
                    null,
                    user,
                    args.joinToString(" "),
                    xChatService.getDefaultChatMode(DefaultChatMode.SPEAKER),
                )
            } else {
                xChatService.sendError(sender.uniqueId, "1,000 냥코인이 필요합니다!")
            }
        }
        return true
    }
}