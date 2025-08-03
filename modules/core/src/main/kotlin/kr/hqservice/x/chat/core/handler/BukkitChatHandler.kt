package kr.hqservice.x.chat.core.handler

import io.papermc.paper.event.player.AsyncChatEvent
import kr.hqservice.framework.bukkit.core.extension.colorize
import kr.hqservice.framework.bukkit.core.listener.HandleOrder
import kr.hqservice.framework.bukkit.core.listener.Listener
import kr.hqservice.framework.bukkit.core.listener.Subscribe
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.api.service.XChatUserService
import kr.hqservice.x.chat.core.XChatSenderImpl
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.md_5.bungee.api.ChatColor

@Listener
class BukkitChatHandler(
    private val chatService: XChatService,
    private val userService: XChatUserService
) {
    @Subscribe(handleOrder = HandleOrder.LAST)
    fun onChat(event: AsyncChatEvent) {
        event.isCancelled = true

        val user = XChatSenderImpl(event.player.uniqueId, event.player.displayName, event.player.name)
        var message = PlainTextComponentSerializer.plainText().serialize(event.message())
        if (!event.player.isOp) message = ChatColor.stripColor(message.colorize())!!

        if (!chatService.sendChat(null, user, message, userService.getCurrentChatMode(user.getUniqueId()))) {
            chatService.sendError(user.getUniqueId(), userService.getCurrentChatMode(user.getUniqueId()).permissionDeniedMessage())
        }
    }
}