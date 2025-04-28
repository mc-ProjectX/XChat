package kr.hqservice.x.chat.core.network.handler

import kr.hqservice.framework.bukkit.core.component.module.Module
import kr.hqservice.framework.bukkit.core.component.module.Setup
import kr.hqservice.framework.netty.api.NettyServer
import kr.hqservice.x.chat.core.network.packet.ChatPacket
import kr.hqservice.x.chat.core.service.ChatSendService
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer

@Module
class ChatPacketHandler(
    private val nettyServer: NettyServer,
    private val chatSendService: ChatSendService
) {
    @Setup
    fun setup() {
        nettyServer.registerOuterPacket(ChatPacket::class)
        nettyServer.registerInnerPacket(ChatPacket::class) { packet , _ ->
            kotlin.runCatching {
                chatSendService.sendChat(packet.sender, packet.receivers, JSONComponentSerializer.json().deserialize(packet.messageJson), packet.logging, packet.whisper, packet.componentId)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}