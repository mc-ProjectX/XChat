package kr.hqservice.x.chat.core.service

import kr.hqservice.framework.global.core.component.Service
import kr.hqservice.framework.netty.api.PacketSender
import kr.hqservice.x.chat.api.ChatData
import kr.hqservice.x.chat.api.ChatFormat
import kr.hqservice.x.chat.api.format.*
import kr.hqservice.x.chat.core.component.registry.ChatModeComponentRegistry
import kr.hqservice.x.chat.core.network.packet.ChatPacket
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.ChatColor
import org.bukkit.Server
import java.util.*
import java.util.logging.Logger

@Service
class ChatSendService(
    private val server: Server,
    private val logger: Logger,
    private val packetSender: PacketSender,
    private val chatModeComponentRegistry: ChatModeComponentRegistry
) {
    private val systemFormats = mapOf(
        DefaultChatFormat.getModeId() to DefaultChatFormat,
        ErrorFormat.getModeId() to ErrorFormat,
        InfoFormat.getModeId() to InfoFormat,
        WhisperSenderFormat.getModeId() to WhisperSenderFormat,
        WhisperReceiverFormat.getModeId() to WhisperReceiverFormat
    )

    private val whisperSenderIds = mutableMapOf<UUID, UUID>()
    private val systemId = UUID.randomUUID()

    fun getWhisperSenderId(playerId: UUID): UUID? {
        return whisperSenderIds[playerId]
    }

    fun sendChat(receiverIds: List<UUID>, chatData: ChatData, format: ChatFormat, logging: Boolean) {
        val formattedChat = format.format(chatData)
        if (format is WhisperReceiverFormat) {
            val senderFormattedChat = WhisperSenderFormat.format(chatData)
            if (logging) logger.info(ChatColor.stripColor(LegacyComponentSerializer.legacySection().serialize(senderFormattedChat)))
            server.getPlayer(chatData.getSender().getUniqueId())?.sendMessage(senderFormattedChat)
        }

        sendChat(chatData.getSender().getUniqueId(), receiverIds, formattedChat, logging, format)
    }

    fun sendChat(sender: UUID, receiverIds: List<UUID>, formattedChat: Component, logging: Boolean, format: ChatFormat) {
        val packet = ChatPacket(sender, JSONComponentSerializer.json().serialize(formattedChat), receiverIds, logging, format is WhisperReceiverFormat, format.getModeId())
        packetSender.sendPacketAll(packet)
    }

    fun sendChat(sender: UUID, receiverIds: List<UUID>, formattedChat: Component, logging: Boolean, whisper: Boolean, modeId: Int) {
        if (receiverIds.isEmpty()) server.broadcast(formattedChat)
        else {
            val receiverPlayers = receiverIds.mapNotNull(server::getPlayer)
            if (receiverPlayers.isNotEmpty()) {
                if (logging) logger.info(ChatColor.stripColor(LegacyComponentSerializer.legacySection().serialize(formattedChat)))
                receiverPlayers.forEach { player ->
                    if (player.isOnline) {
                        if (whisper && receiverIds.first() == player.uniqueId)
                            whisperSenderIds[player.uniqueId] = sender
                        player.sendMessage(formattedChat)
                        if (modeId < 0) systemFormats[modeId]?.received(player)
                        else chatModeComponentRegistry.findChatMode(modeId)?.getFormat()?.received(player)
                    }
                }
            }
        }
    }
}