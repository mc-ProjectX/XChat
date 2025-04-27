package kr.hqservice.x.chat.core.network.packet

import io.netty.buffer.ByteBuf
import kr.hqservice.framework.netty.packet.Packet
import kr.hqservice.framework.netty.packet.extension.readString
import kr.hqservice.framework.netty.packet.extension.readUUID
import kr.hqservice.framework.netty.packet.extension.writeString
import kr.hqservice.framework.netty.packet.extension.writeUUID
import java.util.UUID

class ChatPacket(
    var messageJson: String,
    var receivers: Set<UUID>
) : Packet() {
    override fun read(buf: ByteBuf) {
        messageJson = buf.readString()
        val size = buf.readInt()
        receivers = mutableSetOf<UUID>().apply {
            repeat(size) {
                add(buf.readUUID())
            }
        }
    }

    override fun write(buf: ByteBuf) {
        buf.writeString(messageJson)
        buf.writeInt(receivers.size)
        receivers.forEach {
            buf.writeUUID(it)
        }
    }
}