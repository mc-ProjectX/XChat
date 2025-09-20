package kr.hqservice.x.chat.core

import kr.hqservice.x.chat.api.XChatSender
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

class XChatPlayerSender(
    private val uniqueId: UUID,
    private val prefix: String,
    private val displayName: String,
    private val originalName: String,
    private val guidePermission: Boolean = false
) : XChatSender {
    companion object {
        fun fromByteArray(data: ByteArray): XChatPlayerSender {
            return ByteArrayInputStream(data).use { input ->
                ObjectInputStream(input).use { stream ->
                    val uniqueId = stream.readObject() as UUID
                    val prefix = stream.readObject() as String
                    val displayName = stream.readObject() as String
                    val originalName = stream.readObject() as String
                    val guidePermission = stream.readBoolean()
                    XChatPlayerSender(uniqueId, prefix, displayName, originalName, guidePermission)
                }
            }
        }
    }

    override fun getUniqueId(): UUID {
        return uniqueId
    }

    override fun getPrefix(): String {
        return prefix
    }

    override fun getDisplayName(): String {
        return displayName
    }

    override fun getOriginalName(): String {
        return originalName
    }

    override fun toByteArray(): ByteArray {
        return ByteArrayOutputStream().use {
            ObjectOutputStream(it).use { output ->
                output.writeObject(uniqueId)
                output.writeObject(prefix)
                output.writeObject(displayName)
                output.writeObject(originalName)
                output.writeBoolean(guidePermission)
            }
            it.toByteArray()
        }
    }

    fun hasGuidePermission(): Boolean = guidePermission
}