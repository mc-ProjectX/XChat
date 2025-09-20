package kr.hqservice.x.chat.core.handler

import io.papermc.paper.event.player.AsyncChatEvent
import kr.hqservice.framework.bukkit.core.extension.colorize
import kr.hqservice.framework.bukkit.core.listener.HandleOrder
import kr.hqservice.framework.bukkit.core.listener.Listener
import kr.hqservice.framework.bukkit.core.listener.Subscribe
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.api.service.XChatUserService
import kr.hqservice.x.chat.core.XChatPlayerSender
import kr.hqservice.x.core.api.service.XCoreService
import kr.hqservice.x.core.prefix.database.repository.PrefixUserDataRepository
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.md_5.bungee.api.ChatColor

@Listener
class BukkitChatHandler(
    private val chatService: XChatService,
    private val userService: XChatUserService,
    private val xCoreService: XCoreService,

    private val prefixUserDataRepository: PrefixUserDataRepository
) {
    @Subscribe(handleOrder = HandleOrder.LAST)
    fun onChat(event: AsyncChatEvent) {
        if (event.isCancelled) return

        event.isCancelled = true
        val cache = prefixUserDataRepository[event.player.uniqueId]
        val data = cache?.currentPrefix?.let { id -> cache.prefixList.firstOrNull { it.databaseId == id }?.display?.let { result -> "$result " } } ?: ""
        val xUser = xCoreService.getServer().findPlayer(event.player.uniqueId)?.getColoredDisplayName() ?: event.player.displayName
        val user = XChatPlayerSender(event.player.uniqueId, data, xUser, event.player.name, event.player.hasPermission("project_x.group.guide"))
        var message = PlainTextComponentSerializer.plainText().serialize(event.message()).colorize()
        if (!event.player.isOp) message = ChatColor.stripColor(message)!!
        if (event.player.hasPermission("project_x.group.guide")) {
            val regex = Regex("(\\[/[^\\]]*])|([^\\[]+|\\[(?!/)|])")
            val newComponent = Component.text()
            regex.findAll(message).forEach { match ->
                val component = when {
                    match.groups[1] != null -> Component
                        .text(match.groups[1]!!.value)
                        .style(Style.style(TextColor.color(0x51db66)))
                        .hoverEvent(HoverEvent.showText(Component.text("클릭 시, 명령어를 입력됩니다.")))
                        .clickEvent(ClickEvent.suggestCommand(match.groups[1]!!.value.removePrefix("[").removeSuffix("]")))
                    match.groups[2] != null -> Component.text(match.groups[2]!!.value)
                    else -> null
                }
                component?.let(newComponent::append)
            }
            if (!chatService.sendChat(null, user, newComponent.build(), userService.getCurrentChatMode(user.getUniqueId()))) {
                chatService.sendError(user.getUniqueId(), userService.getCurrentChatMode(user.getUniqueId()).permissionDeniedMessage())
            }
        } else {

            if (!chatService.sendChat(null, user, message, userService.getCurrentChatMode(user.getUniqueId()))) {
                chatService.sendError(user.getUniqueId(), userService.getCurrentChatMode(user.getUniqueId()).permissionDeniedMessage())
            }
        }
    }
}