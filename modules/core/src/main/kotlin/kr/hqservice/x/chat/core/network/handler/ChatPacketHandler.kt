package kr.hqservice.x.chat.core.network.handler

import kr.hqservice.framework.bukkit.core.component.module.Module
import kr.hqservice.framework.bukkit.core.component.module.Setup
import kr.hqservice.framework.netty.api.NettyServer
import kr.hqservice.x.chat.api.XChatModeWithSpy
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.api.service.XChatUserService
import kr.hqservice.x.chat.core.XChatDataImpl
import kr.hqservice.x.chat.core.XChatWhisperData
import kr.hqservice.x.chat.core.def.mode.WhisperReceiveChatMode
import kr.hqservice.x.chat.core.network.packet.ChatPacket
import kr.hqservice.x.core.api.service.XCoreService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import java.util.logging.Logger

@Module
class ChatPacketHandler(
    private val plugin: Plugin,
    private val logger: Logger,
    private val server: Server,
    private val nettyServer: NettyServer,
    private val xCoreService: XCoreService,
    private val xChatService: XChatService,
    private val xChatUserService: XChatUserService
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

            val message = try {
                JSONComponentSerializer.json().deserialize(packet.jsonMessage)
            } catch (e: Exception) {
                LegacyComponentSerializer.legacySection().deserialize(packet.jsonMessage)
            }
            val chatData =
                if (mode is WhisperReceiveChatMode) XChatWhisperData(packet.sender, receivers.firstOrNull() ?: xCoreService.getServer().findPlayer(singleReceiver ?: return@registerInnerPacket) ?: return@registerInnerPacket, message)
                else XChatDataImpl(packet.sender, message)

            if (receivers.isNotEmpty()) {
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

            if (mode is XChatModeWithSpy) {
                server.scheduler.runTaskAsynchronously(plugin, Runnable {
                    val spyPlayers = server.onlinePlayers
                        .filter {
                            it.hasPermission("project_x.chat.spy")
                                    && xChatUserService.isSpyMode(it.uniqueId)
                                    && receivers.none { receiver -> receiver.getUniqueId() == it.uniqueId }
                                    && chatData.getSender().getUniqueId() != it.uniqueId
                        }

                    if (spyPlayers.isNotEmpty()) {
                        val spyFormattedMessage = Component.text("[SPY] ")
                            .style(Style.style(TextColor.color(0x9a73de), TextDecoration.ITALIC.withState(false)))
                            .append(mode.getSpyFormat().format(chatData))

                        spyPlayers.forEach { it.sendMessage(spyFormattedMessage) }
                    }
                })
            }
        }
    }
}