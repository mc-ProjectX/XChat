package kr.hqservice.x.chat.core

import kr.hqservice.x.chat.api.XChatSender
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.util.*

class XChatItemSender(
    private val uniqueId: UUID,
    private val prefix: String,
    private val displayName: String,
    private val originalName: String,
    private val itemStack: ItemStack
) : XChatSender {
    companion object {
        fun fromByteArray(data: ByteArray): XChatItemSender {
            return ByteArrayInputStream(data).use { input ->
                BukkitObjectInputStream(input).use { stream ->
                    val uniqueId = stream.readObject() as UUID
                    val prefix = stream.readObject() as String
                    val displayName = stream.readObject() as String
                    val originalName = stream.readObject() as String
                    val itemStack = stream.readObject() as ItemStack
                    XChatItemSender(uniqueId, prefix, displayName, originalName, itemStack)
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
            BukkitObjectOutputStream(it).use { output ->
                output.writeObject(uniqueId)
                output.writeObject(prefix)
                output.writeObject(displayName)
                output.writeObject(originalName)
                output.writeObject(itemStack)
            }
            it.toByteArray()
        }
    }

    fun getItemStack(): ItemStack {
        return itemStack
    }
}