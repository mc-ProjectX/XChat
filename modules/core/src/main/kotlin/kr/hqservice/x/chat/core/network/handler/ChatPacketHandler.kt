package kr.hqservice.x.chat.core.network.handler

import kr.hqservice.framework.bukkit.core.component.module.Module
import kr.hqservice.framework.bukkit.core.component.module.Setup
import kr.hqservice.framework.netty.api.NettyServer
import kr.hqservice.x.chat.core.network.packet.ChatPacket
import kr.hqservice.x.chat.core.service.ChatServiceImpl
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer

@Module
class ChatPacketHandler(
    private val nettyServer: NettyServer,
    private val chatServiceImpl: ChatServiceImpl
) {
    @Setup
    fun setup() {
        nettyServer.registerOuterPacket(ChatPacket::class)
        nettyServer.registerInnerPacket(ChatPacket::class) { packet , _ ->
            kotlin.runCatching {
                chatServiceImpl.sendChat(packet.receivers, JSONComponentSerializer.json().deserialize(packet.messageJson), true)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}