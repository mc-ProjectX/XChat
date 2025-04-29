package kr.hqservice.x.chat.core.def.mode

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kr.hqservice.framework.bukkit.core.coroutine.bukkitDelay
import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.XChatFormat
import kr.hqservice.x.chat.api.XChatModeWithSpy
import kr.hqservice.x.chat.core.XChatWhisperData
import kr.hqservice.x.chat.core.def.format.DefaultFormatBuilder
import kr.hqservice.x.chat.core.registry.WhisperReplyRegistry
import kr.hqservice.x.core.api.service.XCoreService
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Component
class WhisperReceiveChatMode(
    private val xCoreService: XCoreService,
    private val coroutineScope: CoroutineScope,
    private val whisperReplyRegistry: WhisperReplyRegistry
) : XChatModeWithSpy {
    private val format = DefaultFormatBuilder()
        .setAfterPrefix { "${it.getSender().getDisplayName()} 님에게 받음" }
        .setHover {
            val text = net.kyori.adventure.text.Component.text()
            text.append(net.kyori.adventure.text.Component.text(
                "보낸 유저: ${if (it.getSender().getDisplayName() != it.getSender().getOriginalName()) "${it.getSender().getDisplayName()}(${it.getSender().getOriginalName()})" else it.getSender().getOriginalName()}\n" +
                        "수신 받은 시간: ${LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))}\n" +
                        "\n" +
                        "클릭 하여 귓속말을 보낼 수 있습니다."
            ))
            text.style(Style.style(TextColor.color(0xffffff), TextDecoration.ITALIC.withState(false)))
            HoverEvent.showText(text)
        }
        .setClick {
            ClickEvent.suggestCommand("/귓 ${it.getSender().getDisplayName()} ")
        }.setReceiveEvent { sender, player ->
            whisperReplyRegistry.addReply(player.uniqueId, sender)
            coroutineScope.launch {
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, .3f, 1.7f)
                bukkitDelay(1)
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, .3f, 1.85f)
                bukkitDelay(1)
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, .3f, 2.0f)
            }
        }.setColor(0xddff8c).build()

    private val spyFormat = DefaultFormatBuilder()
        .setHover {
            it as XChatWhisperData
            val text = net.kyori.adventure.text.Component.text()
            text.append(net.kyori.adventure.text.Component.text(
                "보낸 유저: ${if (it.getSender().getDisplayName() != it.getSender().getOriginalName()) "${it.getSender().getDisplayName()}(${it.getSender().getOriginalName()})" else it.getSender().getOriginalName()}" +
                " [${xCoreService.getServer().findPlayer(it.getSender().getUniqueId())?.getChannel()?.getChannelName()}]\n" +
                "받은 유저: ${if (it.getReceiver().getDisplayName() != it.getReceiver().getName()) "${it.getReceiver().getDisplayName()}(${it.getReceiver().getName()})" else it.getReceiver().getName()}" +
                " [${xCoreService.getServer().findPlayer(it.getReceiver().getUniqueId())?.getChannel()?.getChannelName()}]\n" +
                "수신 받은 시간: ${LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))}"
            ))
            text.style(Style.style(TextColor.color(0xffffff), TextDecoration.ITALIC.withState(false)))
            HoverEvent.showText(text)
        }.setAfterPrefix {
            it as XChatWhisperData
            "[${it.getSender().getDisplayName()} -> ${it.getReceiver().getDisplayName()}]"
        }.setSeparator(" ").setColor(0xc4c4c4).build()

    override fun getSpyFormat(): XChatFormat {
        return spyFormat
    }

    override fun getKey(): String {
        return "whisper_receive"
    }

    override fun getFormat(): XChatFormat {
        return format
    }

    override fun isLoggingEnabled(): Boolean {
        return false
    }
}