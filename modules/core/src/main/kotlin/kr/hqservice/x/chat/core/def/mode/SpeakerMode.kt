package kr.hqservice.x.chat.core.def.mode

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.XChatFormat
import kr.hqservice.x.chat.api.XChatMode
import kr.hqservice.x.chat.api.XChatFormatBuilder
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.ChatColor.stripColor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Component
class SpeakerMode : XChatMode {
    private val format = XChatFormatBuilder()
        .setAfterPrefix { " 工 ${it.getSender().getDisplayName()}" }
        .setHover {
            val text = net.kyori.adventure.text.Component.text()
            text.append(net.kyori.adventure.text.Component.text(
                "보낸 유저: ${it.getSender().getOriginalName()}\n" +
                        "수신 받은 시간: ${LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))}\n" +
                        "\n" +
                        "클릭 하여 귓속말을 보낼 수 있습니다."
            ))
            text.style(Style.style(TextColor.color(0xffffff), TextDecoration.ITALIC.withState(false)))
            HoverEvent.showText(text)
        }
        .setClick {
            ClickEvent.suggestCommand("/귓 ${it.getSender().getDisplayName().run(ChatColor::stripColor)!!} ")
        }.setColor(0xffffff)
        .build()

    override fun getKey(): String {
        return "speaker"
    }

    override fun getFormat(): XChatFormat {
        return format
    }

    override fun isLoggingEnabled(): Boolean {
        return true
    }
}