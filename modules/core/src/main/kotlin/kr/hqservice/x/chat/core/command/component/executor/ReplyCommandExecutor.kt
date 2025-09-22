package kr.hqservice.x.chat.core.command.component.executor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.hqservice.framework.bukkit.core.coroutine.extension.BukkitAsync
import kr.hqservice.framework.bukkit.core.extension.colorize
import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.DefaultChatMode
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.core.command.component.BukkitCommandComponent
import kr.hqservice.x.chat.core.registry.WhisperReplyRegistry
import kr.hqservice.x.core.api.service.XCoreService
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Component
class ReplyCommandExecutor(
    private val coroutineScope: CoroutineScope,
    private val xCoreService: XCoreService,
    private val xChatService: XChatService,
    private val whisperReplyRegistry: WhisperReplyRegistry
) : BukkitCommandComponent {
    override fun getBukkitCommand(): String {
        return "답장"
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

                coroutineScope.launch(Dispatchers.BukkitAsync) {
                    if (sender.hasPermission("project_x.group.guide")) {
                        val regex = Regex("(\\[/[^\\]]*])|([^\\[]+|\\[(?!/)|])")
                        val newComponent = net.kyori.adventure.text.Component.text()
                        regex.findAll(message).forEach { match ->
                            val component = when {
                                match.groups[1] != null -> net.kyori.adventure.text.Component
                                    .text(match.groups[1]!!.value)
                                    .style(Style.style(TextColor.color(0x51db66)))
                                    .hoverEvent(HoverEvent.showText(net.kyori.adventure.text.Component.text("클릭 시, 명령어를 입력됩니다.")))
                                    .clickEvent(ClickEvent.suggestCommand(match.groups[1]!!.value.removePrefix("[").removeSuffix("]")))
                                match.groups[2] != null -> net.kyori.adventure.text.Component.text(match.groups[2]!!.value)
                                else -> null
                            }
                            component?.let(newComponent::append)
                        }

                        xChatService.sendChat(
                            targetXPlayer.getUniqueId(),
                            sender.uniqueId,
                            newComponent.build(),
                            xChatService.getDefaultChatMode(DefaultChatMode.WHISPER_RECEIVER),
                            false
                        )
                        xChatService.sendChat(
                            sender.uniqueId,
                            targetXPlayer.getUniqueId(),
                            newComponent.build(),
                            xChatService.getDefaultChatMode(DefaultChatMode.WHISPER_SENDER),
                            false
                        )
                    } else {
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
                    }
                }
            } else xChatService.sendError(sender.uniqueId, "답장을 보낼 대상이 없습니다.")
        }

        return true
    }
}