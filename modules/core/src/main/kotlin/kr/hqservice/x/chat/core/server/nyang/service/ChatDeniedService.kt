package kr.hqservice.x.chat.core.server.nyang.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kr.hqservice.framework.global.core.component.Service
import kr.hqservice.x.chat.core.server.nyang.ChatDenied
import kr.hqservice.x.chat.core.server.nyang.repository.ChatDeniedRepository
import java.util.UUID

@Service
class ChatDeniedService(
    private val coroutineScope: CoroutineScope,
    private val chatDeniedRepository: ChatDeniedRepository
) {
    suspend fun isDenied(sender: UUID, target: UUID): Boolean {
        return chatDeniedRepository.isDenied(target, sender)
    }

    fun getCache(id: UUID): ChatDenied? {
        return chatDeniedRepository[id]
    }

    fun addChatDenied(player: UUID, target: UUID) {
        coroutineScope.launch { chatDeniedRepository.addChatDenied(player, target) }
        chatDeniedRepository[player] = chatDeniedRepository[player]?.let { it.copy(it.owner, it.targets + target) } ?: return
    }

    fun removeChatDenied(player: UUID, target: UUID) {
        coroutineScope.launch { chatDeniedRepository.removeChatDenied(player, target) }
        chatDeniedRepository[player] = chatDeniedRepository[player]?.let { it.copy(it.owner, it.targets - target) } ?: return
    }
}