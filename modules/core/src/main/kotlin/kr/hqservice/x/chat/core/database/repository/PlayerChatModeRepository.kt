package kr.hqservice.x.chat.core.database.repository

import kotlinx.coroutines.Dispatchers
import kr.hqservice.framework.database.repository.player.PlayerRepository
import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.core.database.ChatUserData
import kr.hqservice.x.chat.core.database.entity.PlayerChatModeEntity
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

@Component
class PlayerChatModeRepository : PlayerRepository<ChatUserData>() {
    override suspend fun load(player: Player): ChatUserData {
        return newSuspendedTransaction(Dispatchers.IO) {
            val entity = PlayerChatModeEntity.findById(player.uniqueId)
            if (entity != null) {
                ChatUserData(
                    entity.chatModeId,
                    entity.muteEndAt
                )
            } else ChatUserData(null, null)
        }
    }

    override suspend fun save(player: Player, value: ChatUserData) {
        newSuspendedTransaction(Dispatchers.IO) {
            val entity = PlayerChatModeEntity.findById(player.uniqueId)
            if (entity != null) {
                entity.chatModeId = value.chatModeId
                entity.muteEndAt = value.muteEndAt
            } else {
                PlayerChatModeEntity.new(player.uniqueId) {
                    chatModeId = value.chatModeId
                    muteEndAt = value.muteEndAt
                }
            }
        }
    }
}