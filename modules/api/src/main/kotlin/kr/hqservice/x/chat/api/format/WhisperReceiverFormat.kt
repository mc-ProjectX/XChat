package kr.hqservice.x.chat.api.format

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.hqservice.framework.bukkit.core.coroutine.bukkitDelay
import kr.hqservice.framework.bukkit.core.coroutine.extension.BukkitAsync
import kr.hqservice.x.chat.api.ChatData
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.entity.Player

data object WhisperReceiverFormat : SystemFormat {
    private val WHISPER_MESSAGE_STYLE = Style.style(
        TextColor.color(0xddff8c),
        TextDecoration.ITALIC.withState(false)
    )

    override fun format(chatData: ChatData): Component {
        val base = Component.text()
        base.append(Component.text("${chatData.getSender().getDisplayName()} 님에게 받음: "))
        base.append(chatData.getMessage())
        base.style(WHISPER_MESSAGE_STYLE)
        return base.build()
    }

    override fun received(player: Player) {
        CoroutineScope(Dispatchers.BukkitAsync).launch {
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, .3f, 1.7f)
            bukkitDelay(1)
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, .3f, 1.85f)
            bukkitDelay(1)
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, .3f, 2.0f)
        }
    }

    override fun getSystemId(): Int {
        return -4
    }
}