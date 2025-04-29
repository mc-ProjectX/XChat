package kr.hqservice.x.chat.core.network.packet

import io.netty.buffer.ByteBuf
import kr.hqservice.framework.netty.packet.Packet
import kr.hqservice.framework.netty.packet.extension.readString
import kr.hqservice.framework.netty.packet.extension.writeString

class ChatClearPacket(
    var clearOwner: String
) : Packet() {
    override fun read(buf: ByteBuf) {
        clearOwner = buf.readString()
    }

    override fun write(buf: ByteBuf) {
        buf.writeString(clearOwner)
    }
}