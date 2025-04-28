package kr.hqservice.x.chat.core.handler

import io.papermc.paper.event.player.AsyncChatEvent
import kr.hqservice.framework.bukkit.core.listener.HandleOrder
import kr.hqservice.framework.bukkit.core.listener.Listener
import kr.hqservice.framework.bukkit.core.listener.Subscribe
import kr.hqservice.x.chat.core.ChatDataImpl
import kr.hqservice.x.chat.core.service.ChatSendService
import kr.hqservice.x.chat.core.service.ChatServiceImpl
import kr.hqservice.x.core.api.XPlayer
import kr.hqservice.x.core.api.service.XCoreService

@Listener
class BukkitChatHandler(
    private val xCoreService: XCoreService,
    private val chatService: ChatServiceImpl,
    private val chatSendService: ChatSendService
) {
    @Subscribe(handleOrder = HandleOrder.LAST)
    fun onChat(event: AsyncChatEvent) {
        event.isCancelled = true

        val user = event.player.run(chatService::wrapChatUser)
        val receivers = xCoreService
            .getServer()
            .getPlayers()
            .filter(user.getCurrentChatMode().getReceiverFilter()::test)

        val chatData = ChatDataImpl(user, event.message(), receivers)
        val formattedChat = user.getCurrentChatMode().getFormat().format(chatData)

        chatSendService.sendChat(event.player.uniqueId, receivers.map(XPlayer::getUniqueId), formattedChat, logging = true, user.getCurrentChatMode().getFormat())
    }
}