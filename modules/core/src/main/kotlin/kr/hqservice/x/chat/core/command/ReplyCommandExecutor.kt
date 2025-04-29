package kr.hqservice.x.chat.core.command

import kr.hqservice.framework.bukkit.core.extension.colorize
import kr.hqservice.framework.global.core.component.Bean
import kr.hqservice.x.chat.api.DefaultChatMode
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.core.registry.WhisperReplyRegistry
import kr.hqservice.x.core.api.service.XCoreService
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

@Bean
class ReplyCommandExecutor(
    private val xCoreService: XCoreService,
    private val xChatService: XChatService,
    private val whisperReplyRegistry: WhisperReplyRegistry
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
            xChatService.sendError(sender.uniqueId, "메세지를 입력해주세요.")
        } else {
            val target = whisperReplyRegistry.getReply(sender.uniqueId)
            if (target == null) {
                xChatService.sendError(sender.uniqueId, "답장을 보낼 대상이 없습니다.")
                return true
            }
            val targetXPlayer = xCoreService.getServer().getPlayers().firstOrNull { it.getUniqueId() == target }
            if (targetXPlayer != null) {
                var message = args.joinToString(" ")
                if (!sender.isOp) message = ChatColor.stripColor(message.colorize())!!
                xChatService.sendChat(
                    targetXPlayer.getUniqueId(),
                    sender.uniqueId,
                    message,
                    xChatService.getDefaultChatMode(DefaultChatMode.WHISPER_RECEIVER),
                    false
                )
                xChatService.sendChat(
                    sender.uniqueId,
                    targetXPlayer.getUniqueId(),
                    message,
                    xChatService.getDefaultChatMode(DefaultChatMode.WHISPER_SENDER),
                    false
                )
            } else xChatService.sendError(sender.uniqueId, "답장을 보낼 대상이 없습니다.")
        }

        return true
    }
}