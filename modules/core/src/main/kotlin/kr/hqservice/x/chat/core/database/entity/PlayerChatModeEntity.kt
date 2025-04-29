package kr.hqservice.x.chat.core.database.entity

import kr.hqservice.framework.database.component.Table
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

class PlayerChatModeEntity(
    id: EntityID<UUID>
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PlayerChatModeEntity>(PlayerChatModeTable)

    var chatModeKey by PlayerChatModeTable.chatModeId
    var muteEndAt by PlayerChatModeTable.muteEndAt
}

@Table
object PlayerChatModeTable : UUIDTable("x_user_chat") {
    val chatModeId = char("mode_id", 64).nullable()
    val muteEndAt = long("mute_end_at").nullable()
}