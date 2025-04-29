package kr.hqservice.x.chat.core.service

import kr.hqservice.framework.global.core.component.Service
import kr.hqservice.x.chat.api.DefaultChatMode
import kr.hqservice.x.chat.api.XChatMode
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.api.service.XChatUserService
import kr.hqservice.x.chat.core.database.ChatUserData
import kr.hqservice.x.chat.core.database.repository.PlayerChatModeRepository
import org.bukkit.Server
import java.util.UUID

@Service
class PlayerChatModeService(
    private val server: Server,
    private val chatService: XChatService,
    private val playerChatModeRepository: PlayerChatModeRepository
) : XChatUserService {
    private fun getPlayerChatModeData(uniqueId: UUID): ChatUserData {
        return playerChatModeRepository[uniqueId] ?: ChatUserData(null, null)
    }

    override fun getCurrentChatMode(userId: UUID): XChatMode {
        return getPlayerChatModeData(userId).chatModeKey
            ?.let { chatService.findChatMode(it) } ?: chatService.getDefaultChatMode(DefaultChatMode.DEFAULT)
    }

    override fun getMuteEndAt(userId: UUID): Long? {
        return getPlayerChatModeData(userId).muteEndAt
    }

    override fun setMute(userId: UUID, muteEndAt: Long?) {
        val chatUserData = getPlayerChatModeData(userId).copy(muteEndAt = muteEndAt)
        setPlayerChatModeData(userId, chatUserData)
    }

    override fun setChatMode(userId: UUID, xChatMode: XChatMode?) {
        val chatUserData = getPlayerChatModeData(userId).copy(chatModeKey = xChatMode?.getKey())
        setPlayerChatModeData(userId, chatUserData)
    }

    private fun setPlayerChatModeData(uniqueId: UUID, chatUserData: ChatUserData) {
        if (server.getPlayer(uniqueId) != null)
            playerChatModeRepository[uniqueId] = chatUserData
    }
}