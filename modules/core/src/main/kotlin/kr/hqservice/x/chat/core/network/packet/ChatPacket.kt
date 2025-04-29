package kr.hqservice.x.chat.core.network.packet

import io.netty.buffer.ByteBuf
import kr.hqservice.framework.netty.packet.Packet
import kr.hqservice.framework.netty.packet.extension.readString
import kr.hqservice.framework.netty.packet.extension.readUUID
import kr.hqservice.framework.netty.packet.extension.writeString
import kr.hqservice.framework.netty.packet.extension.writeUUID
import kr.hqservice.x.chat.api.XChatSender
import kr.hqservice.x.chat.core.XChatSenderImpl
import java.util.UUID

class ChatPacket(
    var mode: String,
    var sender: XChatSender,
    var singleReceiver: UUID?,
    var jsonMessage: String,
    var logging: Boolean
) : Packet() {
    override fun read(buf: ByteBuf) {
        logging = buf.readBoolean()
        mode = buf.readString()
        sender = XChatSenderImpl(
            buf.readUUID(),
            buf.readString(),
            buf.readString()
        )
        singleReceiver = if (buf.readBoolean())
            buf.readUUID()
        else null

        jsonMessage = buf.readString()
            .replace("ª™ª", "$")
            .replace("ª•ª", "\\")
    }

    override fun write(buf: ByteBuf) {
        buf.writeBoolean(logging)
        buf.writeString(mode)
        buf.writeUUID(sender.getUniqueId())
        buf.writeString(sender.getDisplayName())
        buf.writeString(sender.getOriginalName())

        buf.writeBoolean(singleReceiver != null)
        singleReceiver?.let { buf.writeUUID(it) }
        buf.writeString(
            jsonMessage
                .replace("$", "ª™ª")
                .replace("\\", "ª•ª")
        )
    }
}