package kr.hqservice.x.chat.core.handler

import kr.hqservice.framework.bukkit.core.listener.Listener
import kr.hqservice.framework.bukkit.core.listener.Subscribe
import kr.hqservice.x.chat.core.registry.WhisperReplyRegistry
import org.bukkit.event.player.PlayerQuitEvent

@Listener
class BukkitConnectionHandler(
    private val whisperReplyRegistry: WhisperReplyRegistry
) {
    @Subscribe
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        whisperReplyRegistry.removeReply(player.uniqueId)
    }
}