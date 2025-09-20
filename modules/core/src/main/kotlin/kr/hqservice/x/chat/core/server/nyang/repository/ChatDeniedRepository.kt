package kr.hqservice.x.chat.core.server.nyang.repository

import kotlinx.coroutines.Dispatchers
import kr.hqservice.framework.database.repository.player.PlayerRepository
import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.core.database.entity.PlayerChatModeEntity
import kr.hqservice.x.chat.core.server.nyang.ChatDenied
import kr.hqservice.x.chat.core.server.nyang.database.ChatDeniedEntity
import kr.hqservice.x.chat.core.server.nyang.database.ChatDeniedTable
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

@Component
class ChatDeniedRepository : PlayerRepository<ChatDenied>() {
    override suspend fun load(player: Player): ChatDenied {
        return newSuspendedTransaction(Dispatchers.IO) {
            val ids = ChatDeniedEntity
                .find { ChatDeniedTable.owner eq player.uniqueId }
                .map { it.target.id.value }
                .toSet()

            ChatDenied(player.uniqueId, ids)
        }
    }

    suspend fun isDenied(player: UUID, target: UUID): Boolean {
        return newSuspendedTransaction(Dispatchers.IO) {
            ChatDeniedEntity.find {
                (ChatDeniedTable.owner eq player) and (ChatDeniedTable.targetId eq target)
            }.any()
        }
    }

    suspend fun addChatDenied(player: UUID, target: UUID) {
        newSuspendedTransaction(Dispatchers.IO) {
            ChatDeniedEntity.new {
                this.owner = PlayerChatModeEntity[player]
                this.target = PlayerChatModeEntity[target]
            }
        }
    }

    suspend fun removeChatDenied(player: UUID, target: UUID) {
        newSuspendedTransaction(Dispatchers.IO) {
            ChatDeniedEntity.find {
                (ChatDeniedTable.owner eq player) and (ChatDeniedTable.targetId eq target)
            }.forEach { it.delete() }
        }
    }

    override suspend fun save(player: Player, value: ChatDenied) {}
}