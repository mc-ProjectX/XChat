package kr.hqservice.x.chat.core.command.component.executor

import kr.hqservice.framework.bukkit.core.extension.toByteArray
import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.DefaultChatMode
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.core.XChatItemSender
import kr.hqservice.x.chat.core.XChatPlayerSender
import kr.hqservice.x.chat.core.command.component.BukkitCommandComponent
import kr.hqservice.x.chat.core.hook.EconomyHook
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder

@Component
class ItemSpeakerCommandExecutor(
    private val economyHook: EconomyHook,
    private val xChatService: XChatService
) : BukkitCommandComponent {
    private val cooldown = mutableMapOf<String, Long>()

    override fun getBukkitCommand(): String {
        return "자랑"
    }

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>): Boolean {
        if (sender is Player) {
            if (!sender.isOp) {
                val currentTime = System.currentTimeMillis()
                if (cooldown.containsKey(sender.name) && currentTime - cooldown[sender.name]!! < 300000) {
                    xChatService.sendError(sender.uniqueId, "5분 마다 사용할 수 있습니다.")
                    return true
                }
                cooldown[sender.name] = currentTime
            }
            val item = sender.inventory.itemInMainHand

            if (economyHook.hasMoney(sender, 5000L) || sender.hasPermission("nyang.speaker.permission")) {
                if (item.type.isAir) {
                    xChatService.sendError(sender.uniqueId, "아이템을 손에 들고 사용해주세요.")
                    return true
                }

                if (!sender.hasPermission("nyang.speaker.permission")) economyHook.withdrawMoney(sender, 5000L)

                val user = XChatItemSender(sender.uniqueId, "", sender.displayName, sender.name, item)
                xChatService.sendChat(
                    null,
                    user,
                    sender.inventory.itemInMainHand,
                    xChatService.getDefaultChatMode(DefaultChatMode.ITEM_SPEAKER),
                )
            } else {
                xChatService.sendError(sender.uniqueId, "5,000 냥코인이 필요합니다!")
            }
        }
        return true
    }
}