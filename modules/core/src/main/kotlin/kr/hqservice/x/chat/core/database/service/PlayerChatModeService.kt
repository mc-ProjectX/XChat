package kr.hqservice.x.chat.core.database.service

import kr.hqservice.framework.global.core.component.Service
import kr.hqservice.x.chat.core.database.ChatUserData
import kr.hqservice.x.chat.core.database.repository.PlayerChatModeRepository
import org.bukkit.Server
import java.util.UUID

@Service
class PlayerChatModeService(
    private val server: Server,
    private val playerChatModeRepository: PlayerChatModeRepository
) {
    fun getPlayerChatModeData(uniqueId: UUID): ChatUserData {
        return playerChatModeRepository[uniqueId] ?: ChatUserData(null, null)
    }

    private fun setPlayerChatModeData(uniqueId: UUID, chatUserData: ChatUserData) {
        if (server.getPlayer(uniqueId) != null)
            playerChatModeRepository[uniqueId] = chatUserData
    }

    fun setMute(uniqueId: UUID, muteEndAt: Long?) {
        val chatUserData = getPlayerChatModeData(uniqueId).copy(muteEndAt = muteEndAt)
        setPlayerChatModeData(uniqueId, chatUserData)
    }

    fun setChatMode(uniqueId: UUID, chatModeId: Int?) {
        val chatUserData = getPlayerChatModeData(uniqueId).copy(chatModeId = chatModeId)
        setPlayerChatModeData(uniqueId, chatUserData)
    }
}