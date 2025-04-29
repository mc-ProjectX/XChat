package kr.hqservice.x.chat.core.network.handler

import kr.hqservice.framework.bukkit.core.component.module.Module
import kr.hqservice.framework.bukkit.core.component.module.Setup
import kr.hqservice.framework.netty.api.NettyServer
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.core.XChatDataImpl
import kr.hqservice.x.chat.core.network.packet.ChatPacket
import kr.hqservice.x.core.api.service.XCoreService
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.Server
import java.util.logging.Logger

@Module
class ChatPacketHandler(
    private val logger: Logger,
    private val server: Server,
    private val nettyServer: NettyServer,
    private val xCoreService: XCoreService,
    private val xChatService: XChatService
) {
    @Setup
    fun setup() {
        nettyServer.registerOuterPacket(ChatPacket::class)
        nettyServer.registerInnerPacket(ChatPacket::class) { packet , _ ->
            val mode = xChatService.findChatMode(packet.mode) ?: return@registerInnerPacket
            val filter = mode.getReceiverFilter(packet.sender)
            val singleReceiver = packet.singleReceiver
            val receivers = if (singleReceiver != null) {
                server
                    .getPlayer(singleReceiver)
                    ?.run { xCoreService.getServer().findPlayer(uniqueId) }
                    ?.let { listOf(it) }?.filter(filter) ?: emptyList()
            } else {
                server
                    .onlinePlayers
                    .mapNotNull { xCoreService.getServer().findPlayer(it.uniqueId) }
                    .filter(filter)
            }

            if (receivers.isNotEmpty()) {
                val chatData = XChatDataImpl(packet.sender, JSONComponentSerializer.json().deserialize(packet.jsonMessage))
                val formattedMessage = mode.getFormat().format(chatData)
                receivers.forEach { receiver ->
                    server.getPlayer(receiver.getUniqueId())?.apply {
                        sendMessage(formattedMessage)
                        mode.getFormat().received(packet.sender, this)
                    }
                }
                if (packet.logging) {
                    logger.info(ChatColor.stripColor(LegacyComponentSerializer.legacySection().serialize(formattedMessage)) + " (receivers: ${receivers.size})")
                }
            }
        }
    }
}