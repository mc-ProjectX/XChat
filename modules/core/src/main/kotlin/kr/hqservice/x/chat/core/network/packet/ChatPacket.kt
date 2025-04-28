package kr.hqservice.x.chat.core.network.packet

import io.netty.buffer.ByteBuf
import kr.hqservice.framework.netty.packet.Packet
import kr.hqservice.framework.netty.packet.extension.readString
import kr.hqservice.framework.netty.packet.extension.readUUID
import kr.hqservice.framework.netty.packet.extension.writeString
import kr.hqservice.framework.netty.packet.extension.writeUUID
import java.util.UUID

class ChatPacket(
    var sender: UUID,
    var messageJson: String,
    var receivers: List<UUID>,
    var logging: Boolean,
    var whisper: Boolean,
    var componentId: Int
) : Packet() {
    override fun read(buf: ByteBuf) {
        logging = buf.readBoolean()
        whisper = buf.readBoolean()
        componentId = buf.readInt()

        sender = buf.readUUID()
        messageJson = buf.readString()
        val size = buf.readInt()
        receivers = mutableListOf<UUID>().apply {
            repeat(size) {
                add(buf.readUUID())
            }
        }
    }

    override fun write(buf: ByteBuf) {
        buf.writeBoolean(logging)
        buf.writeBoolean(whisper)
        buf.writeInt(componentId)

        buf.writeUUID(sender)
        buf.writeString(messageJson)
        buf.writeInt(receivers.size)
        receivers.forEach {
            buf.writeUUID(it)
        }
    }
}