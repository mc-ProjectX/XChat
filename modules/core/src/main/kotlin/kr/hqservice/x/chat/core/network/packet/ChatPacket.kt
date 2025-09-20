package kr.hqservice.x.chat.core.network.packet

import io.netty.buffer.ByteBuf
import kr.hqservice.framework.global.core.extension.compress
import kr.hqservice.framework.global.core.extension.decompress
import kr.hqservice.framework.netty.packet.Packet
import kr.hqservice.framework.netty.packet.extension.readString
import kr.hqservice.framework.netty.packet.extension.readUUID
import kr.hqservice.framework.netty.packet.extension.writeString
import kr.hqservice.framework.netty.packet.extension.writeUUID
import kr.hqservice.x.chat.api.XChatSender
import kr.hqservice.x.chat.core.XChatItemSender
import kr.hqservice.x.chat.core.XChatPlayerSender
import java.util.UUID

class ChatPacket(
    var mode: String,
    var sender: XChatSender,
    var singleReceiver: UUID?,
    var jsonMessage: String,
    var logging: Boolean,
    var extraData: ByteArray?
) : Packet() {
    override fun read(buf: ByteBuf) {
        logging = buf.readBoolean()
        mode = buf.readString()

        val senderId = buf.readInt()
        val senderSize = buf.readInt()
        val compressedSerializedSender = ByteArray(senderSize)
        buf.readBytes(compressedSerializedSender)
        val decompressedSender = compressedSerializedSender.decompress()
        sender = when (senderId) {
            0 -> XChatPlayerSender.fromByteArray(decompressedSender)
            1 -> XChatItemSender.fromByteArray(decompressedSender)
            else -> {
                throw IllegalArgumentException("Unsupported sender type in ChatPacket")
            }
        }

        singleReceiver = if (buf.readBoolean())
            buf.readUUID()
        else null

        val bytesLength = buf.readInt()
        val bytes = ByteArray(bytesLength)
        buf.readBytes(bytes)
        jsonMessage = bytes
            .decompress()
            .toString(Charsets.UTF_8)
            .replace("ª™ª", "$")
            .replace("ª•ª", "\\")

        val size = buf.readInt()
        if (size > 0) {
            val compressedData = ByteArray(size)
            buf.readBytes(compressedData)
            extraData = compressedData.decompress()
        } else {
            extraData = null
        }
    }

    override fun write(buf: ByteBuf) {
        buf.writeBoolean(logging)
        buf.writeString(mode)

        val senderId = when (sender) {
            is XChatPlayerSender -> 0
            is XChatItemSender -> 1
            else -> throw IllegalArgumentException("Unsupported sender type: ${sender::class.java.name}")
        }
        buf.writeInt(senderId)
        val serializedSender = sender.toByteArray().compress()
        buf.writeInt(serializedSender.size)
        buf.writeBytes(serializedSender)

        buf.writeBoolean(singleReceiver != null)
        singleReceiver?.let { buf.writeUUID(it) }
        val jsonText = jsonMessage
            .replace("$", "ª™ª")
            .replace("\\", "ª•ª")

        val bytes = jsonText
            .toByteArray(Charsets.UTF_8)
            .compress()
        buf.writeInt(bytes.size)
        buf.writeBytes(bytes)

        if (extraData != null) {
            val compressed = extraData!!.compress()
            buf.writeInt(compressed.size)
            buf.writeBytes(compressed)
        } else {
            buf.writeInt(0)
        }
    }
}