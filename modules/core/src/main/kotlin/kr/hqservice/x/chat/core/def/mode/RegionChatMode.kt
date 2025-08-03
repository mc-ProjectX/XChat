package kr.hqservice.x.chat.core.def.mode

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.XChatFormat
import kr.hqservice.x.chat.api.XChatSender
import kr.hqservice.x.chat.api.XChatModeWithSpy
import kr.hqservice.x.chat.api.XChatFormatBuilder
import kr.hqservice.x.core.api.XPlayer
import kr.hqservice.x.core.api.service.XCoreService
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Server
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Component
class RegionChatMode(
    private val server: Server,
    private val xCoreService: XCoreService
) : XChatModeWithSpy {
    private val format = XChatFormatBuilder()
        // Server Front
        .setPrefix("䰴")
        .setPrefixColor(0xebd2b2)

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
        }.build()

    private val spyFormat = XChatFormatBuilder()
        .setHover {
            val text = net.kyori.adventure.text.Component.text()
            text.append(net.kyori.adventure.text.Component.text(
                "보낸 유저: ${if (it.getSender().getDisplayName() != it.getSender().getOriginalName()) "${it.getSender().getDisplayName()}(${it.getSender().getOriginalName()})" else it.getSender().getOriginalName()}" +
                " [${xCoreService.getServer().findPlayer(it.getSender().getUniqueId())?.getChannel()?.getChannelName()}]\n" +
                "수신 받은 시간: ${LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))}"
            ))
            text.style(Style.style(TextColor.color(0xffffff), TextDecoration.ITALIC.withState(false)))
            HoverEvent.showText(text)
        }.setColor(0xc4c4c4).build()

    override fun getSpyFormat(): XChatFormat {
        return spyFormat
    }

    override fun getKey(): String {
        return "region"
    }

    override fun getFormat(): XChatFormat {
        return format
    }

    override fun getReceiverFilter(sender: XChatSender, extraData: Any?): (XPlayer) -> Boolean {
        val senderPlayer = server.getPlayer(sender.getUniqueId()) ?: return { _ -> false }
        return { receiver ->
            val receiverPlayer = server.getPlayer(receiver.getUniqueId())
            if (receiverPlayer != null) {
                receiverPlayer.location.world.name == senderPlayer.location.world.name
                        && senderPlayer.location.distance(receiverPlayer.location) < 500.0
            } else false
        }
    }
}