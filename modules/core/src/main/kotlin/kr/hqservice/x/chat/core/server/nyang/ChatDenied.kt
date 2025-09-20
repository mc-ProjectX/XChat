package kr.hqservice.x.chat.core.server.nyang

import java.util.UUID

data class ChatDenied(
    val owner: UUID,
    val targets: Set<UUID>
)