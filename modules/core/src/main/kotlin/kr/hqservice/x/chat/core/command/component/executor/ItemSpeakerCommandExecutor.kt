package kr.hqservice.x.chat.core.command.component.executor

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.DefaultChatMode
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.core.XChatItemSender
import kr.hqservice.x.chat.core.command.component.BukkitCommandComponent
import kr.hqservice.x.chat.core.hook.EconomyHook
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Component
class ItemSpeakerCommandExecutor(
    private val economyHook: EconomyHook,
    private val xChatService: XChatService
) : BukkitCommandComponent {
    override fun getBukkitCommand(): String {
        return "자랑"
    }

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>): Boolean {
        if (sender is Player) {
            val item = sender.inventory.itemInMainHand
            if (item.type.isAir) {
                xChatService.sendError(sender.uniqueId, "아이템을 손에 들고 사용해주세요.")
                return true
            }

            val user = XChatItemSender(sender.uniqueId, "", sender.displayName, sender.name, item)
            xChatService.sendChat(
                null,
                user,
                sender.inventory.itemInMainHand,
                xChatService.getDefaultChatMode(DefaultChatMode.ITEM_SPEAKER),
            )
        }
        return true
    }
}