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
import kr.hqservice.x.core.api.service.XCoreService
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.*

@Component
class WhisperCommandExecutor(
    private val coroutineScope: CoroutineScope,
    private val xCoreService: XCoreService,
    private val xChatService: XChatService
) : BukkitCommandComponent {
    override fun getBukkitCommand(): String {
        return "귓"
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
        if (args.isEmpty()) {
            if (sender is Player) {
                xChatService.sendError(sender.uniqueId, "대상 플레이어를 입력해주세요.")
            } else sender.sendMessage("§c대상 플레이어를 입력해주세요.")
        } else if (args.size == 1) {
            if (sender is Player) {
                xChatService.sendError(sender.uniqueId, "메세지를 입력해주세요.")
            } else sender.sendMessage("§c메세지를 입력해주세요.")
        } else {
            val target = args[0]
            val targetXPlayer = xCoreService.getServer().getPlayers().firstOrNull { it.getName().equals(target, true) || it.getDisplayName().equals(target, true) }
            if (targetXPlayer != null) {
                var message = Arrays.copyOfRange(args, 1, args.size).joinToString(" ")
                if (!sender.isOp) message = ChatColor.stripColor(message.colorize())!!
                if (sender is Player) {
                    if (targetXPlayer.getUniqueId() == sender.uniqueId) {
                        xChatService.sendError(sender.uniqueId, "자기 자신에게 귓속말을 보낼 수 없습니다.")
                        return true
                    }

                    coroutineScope.launch(Dispatchers.BukkitAsync) {
                        if (sender.hasPermission("project_x.group.guide")) {
                            val regex = Regex("(\\[/[^\\]]*])|([^\\[]+|\\[(?!/)|])")
                            val newComponent = net.kyori.adventure.text.Component.text()
                            regex.findAll(message).forEach { match ->
                                val component = when {
                                    match.groups[1] != null -> net.kyori.adventure.text.Component
                                        .text(match.groups[1]!!.value)
                                        .style(Style.style(TextColor.color(0x51db66), TextDecoration.UNDERLINED.withState(true)))
                                        .hoverEvent(HoverEvent.showText(net.kyori.adventure.text.Component.text("클릭 시, 명령어를 입력됩니다.")))
                                        .clickEvent(ClickEvent.suggestCommand(match.groups[1]!!.value.removePrefix("[").removeSuffix("]")))
                                    match.groups[2] != null -> net.kyori.adventure.text.Component.text(match.groups[2]!!.value)
                                    else -> null
                                }
                                component?.let(newComponent::append)
                            }

                            xChatService.sendChat(
                                sender.uniqueId,
                                targetXPlayer.getUniqueId(),
                                newComponent.build(),
                                xChatService.getDefaultChatMode(DefaultChatMode.WHISPER_SENDER),
                                false
                            )
                            xChatService.sendChat(
                                targetXPlayer.getUniqueId(),
                                sender.uniqueId,
                                newComponent.build(),
                                xChatService.getDefaultChatMode(DefaultChatMode.WHISPER_RECEIVER),
                                false
                            )
                        } else {
                            xChatService.sendChat(
                                sender.uniqueId,
                                targetXPlayer.getUniqueId(),
                                message,
                                xChatService.getDefaultChatMode(DefaultChatMode.WHISPER_SENDER),
                                false
                            )
                            xChatService.sendChat(
                                targetXPlayer.getUniqueId(),
                                sender.uniqueId,
                                message,
                                xChatService.getDefaultChatMode(DefaultChatMode.WHISPER_RECEIVER),
                                false
                            )
                        }
                    }
                } else {
                    xChatService.sendChat(
                        targetXPlayer.getUniqueId(),
                        xChatService.getConsoleSender("관리자"),
                        message,
                        xChatService.getDefaultChatMode(DefaultChatMode.WHISPER_RECEIVER),
                        false
                    )
                }
            } else {
                if (sender is Player) xChatService.sendError(sender.uniqueId, "대상 플레이어를 찾을 수 없습니다.")
                else sender.sendMessage("§c대상 플레이어를 찾을 수 없습니다.")
            }
        }

        return true
    }
}