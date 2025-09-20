package kr.hqservice.x.chat.core.service

import kr.hqservice.framework.bukkit.core.extension.colorize
import kr.hqservice.framework.global.core.component.Service
import kr.hqservice.framework.netty.api.PacketSender
import kr.hqservice.framework.nms.extension.getDisplayName
import kr.hqservice.x.chat.api.DefaultChatMode
import kr.hqservice.x.chat.api.XChatMode
import kr.hqservice.x.chat.api.XChatSender
import kr.hqservice.x.chat.api.service.XChatService
import kr.hqservice.x.chat.core.XChatPlayerSender
import kr.hqservice.x.chat.core.component.registry.ChatModeComponentRegistry
import kr.hqservice.x.chat.core.network.packet.ChatPacket
import kr.hqservice.x.core.api.service.XCoreService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.inventory.ItemStack
import java.util.UUID

@Service
class XChatServiceImpl(
    private val packetSender: PacketSender,
    private val xCoreService: XCoreService,
    private val chatModeComponentRegistry: ChatModeComponentRegistry
) : XChatService {
    private val consoleId = UUID.randomUUID()
    private val consoleSender = XChatPlayerSender(consoleId, "", "[SYSTEM]", "console")

    override fun getConsoleSender(label: String): XChatSender {
        return if (label.isNotEmpty()) XChatPlayerSender(consoleId, "", label, "console") else consoleSender
    }

    override fun findChatMode(modeId: String): XChatMode? {
        return chatModeComponentRegistry.findChatMode(modeId)
    }

    override fun getChatModes(): List<XChatMode> {
        return chatModeComponentRegistry.getAllChatModes().toList()
    }

    override fun getDefaultChatMode(modeType: DefaultChatMode): XChatMode {
        return when (modeType) {
            DefaultChatMode.DEFAULT -> findChatMode("default")
            DefaultChatMode.ERROR -> findChatMode("error")
            DefaultChatMode.INFO -> findChatMode("system")
            DefaultChatMode.WHISPER_SENDER -> findChatMode("whisper_send")
            DefaultChatMode.WHISPER_RECEIVER -> findChatMode("whisper_receive")
            DefaultChatMode.SPEAKER -> findChatMode("speaker")
            DefaultChatMode.ITEM_SPEAKER -> findChatMode("item_speaker")
        }!!
    }

    override fun sendError(targetId: UUID, message: String) {
        val chatMode = findChatMode("error")!!
        sendChat(targetId, getConsoleSender(), message, chatMode, false)
    }

    override fun sendInfo(targetId: UUID, message: String) {
        val chatMode = findChatMode("system")!!
        sendChat(targetId, getConsoleSender(), message, chatMode, false)
    }

    override fun sendChat(targetId: UUID?, senderId: UUID, chat: String, xChatMode: XChatMode, logging: Boolean): Boolean {
        val xPlayer = xCoreService.getServer().findPlayer(senderId) ?: return false
        val chatSender = XChatPlayerSender(xPlayer.getUniqueId(), "", xPlayer.getDisplayName(), xPlayer.getName())
        return sendChat(targetId, chatSender, chat, xChatMode, logging)
    }

    override fun sendChat(targetId: UUID?, senderId: UUID, chat: Component, xChatMode: XChatMode, logging: Boolean): Boolean {
        val xPlayer = xCoreService.getServer().findPlayer(senderId) ?: return false
        val chatSender = XChatPlayerSender(xPlayer.getUniqueId(), "", xPlayer.getDisplayName(), xPlayer.getName())
        return sendChat(targetId, chatSender, chat, xChatMode, logging)
    }

    override fun sendChat(targetId: UUID?, sender: XChatSender, chat: String, xChatMode: XChatMode, logging: Boolean): Boolean {
        return sendChat(targetId, sender, LegacyComponentSerializer.legacySection().deserialize(chat.colorize()), xChatMode, logging)
    }

    override fun sendChat(
        targetId: UUID?,
        sender: XChatSender,
        item: ItemStack,
        xChatMode: XChatMode,
        logging: Boolean
    ): Boolean {
        val result = Component.text()
        val nameComponent = Component.text(item.getDisplayName() + " ")
            .style(Style.style(net.kyori.adventure.text.format.TextColor.color(0xffffff)))
        val amountComponent = Component.text("(x${item.amount})")
            .style(Style.style(net.kyori.adventure.text.format.TextColor.color(0xebebeb)))
        return sendChat(targetId, sender, result
            .append(Component.text(" 工 ${sender.getDisplayName()} 님의 아이템 "))
            .append(nameComponent)
            .append(amountComponent)
            //.hoverEvent(HoverEvent.showItem(showItem))
            .style(Style.style(TextDecoration.ITALIC.withState(false)))
            .build()
            , xChatMode, logging)
    }

    override fun sendChat(targetId: UUID?, sender: XChatSender, chat: Component, xChatMode: XChatMode, logging: Boolean): Boolean {
        if (!xChatMode.hasSendPermission(sender)) return false

        val jsonText = JSONComponentSerializer.json().serialize(chat)
        val extra = xChatMode.extraData(sender.getUniqueId())
        val byteArray = extra?.let { xChatMode.writeExtraData(it) }

        val packet = ChatPacket(
            xChatMode.getKey(),
            sender,
            targetId,
            jsonText,
            if (!logging) xChatMode.isLoggingEnabled() else true,
            byteArray
        )

        packetSender.sendPacketAll(packet)
        return true
    }
}