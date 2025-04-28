package kr.hqservice.x.chat.core.command

import kr.hqservice.framework.global.core.component.Bean
import kr.hqservice.x.chat.api.format.ErrorFormat
import kr.hqservice.x.chat.api.format.WhisperReceiverFormat
import kr.hqservice.x.chat.core.service.ChatSendService
import kr.hqservice.x.chat.core.service.ChatServiceImpl
import kr.hqservice.x.core.api.service.XCoreService
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

@Bean
class ReplyCommandExecutor(
    private val xCoreService: XCoreService,
    private val xChatService: ChatServiceImpl,
    private val xChatSendService: ChatSendService
) : TabExecutor {
    override fun onTabComplete(
        p0: CommandSender,
        p1: Command,
        p2: String,
        p3: Array<String>
    ): List<String> {
        return emptyList()
    }

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>): Boolean {
        if (sender !is Player) return true

        if (args.isEmpty()) {
            xChatService.sendChat(listOf(sender.uniqueId), "메세지를 입력해주세요.", ErrorFormat, logging = false)
        } else {
            val target = xChatSendService.getWhisperSenderId(sender.uniqueId)
            if (target == null) {
                xChatService.sendChat(listOf(sender.uniqueId), "답장을 보낼 대상이 없습니다.", ErrorFormat, logging = false)
                return true
            }
            val targetXPlayer = xCoreService.getServer().getPlayers().firstOrNull { it.getUniqueId() == target }
            if (targetXPlayer != null) {
                xChatService.sendChat(
                    listOf(targetXPlayer.getUniqueId()),
                    args.joinToString(" "),
                    WhisperReceiverFormat,
                    xCoreService.getServer().findPlayer(sender.uniqueId)!!, false
                )
            } else xChatService.sendChat(listOf(sender.uniqueId), "답장을 보낼 대상이 없습니다.", ErrorFormat, logging = false)
        }

        return true
    }
}