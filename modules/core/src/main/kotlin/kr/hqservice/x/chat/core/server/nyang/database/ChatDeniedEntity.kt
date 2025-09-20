package kr.hqservice.x.chat.core.server.nyang.database

import kr.hqservice.framework.database.component.Table
import kr.hqservice.x.chat.core.database.entity.PlayerChatModeEntity
import kr.hqservice.x.chat.core.database.entity.PlayerChatModeTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

class ChatDeniedEntity(
    id: EntityID<Int>
) : IntEntity(id) {
    companion object : IntEntityClass<ChatDeniedEntity>(ChatDeniedTable)

    var owner by PlayerChatModeEntity referencedOn ChatDeniedTable.owner
    var target by PlayerChatModeEntity referencedOn ChatDeniedTable.targetId
}

@Table
object ChatDeniedTable : IntIdTable("nyang_chat_denied") {
    var owner = reference("owner_id", PlayerChatModeTable, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    var targetId = reference("target_id", PlayerChatModeTable, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
}