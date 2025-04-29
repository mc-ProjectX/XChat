package kr.hqservice.x.chat.core.def.mode

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kr.hqservice.framework.bukkit.core.coroutine.bukkitDelay
import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.XChatFormat
import kr.hqservice.x.chat.api.XChatMode
import kr.hqservice.x.chat.core.def.format.DefaultFormatBuilder
import kr.hqservice.x.chat.core.registry.WhisperReplyRegistry
import org.bukkit.Sound

@Component
class WhisperReceiveChatMode(
    private val coroutineScope: CoroutineScope,
    private val whisperReplyRegistry: WhisperReplyRegistry
) : XChatMode {
    private val format = DefaultFormatBuilder()
        .setDisplayNameFormat { "$it 님에게 받음" }
        .setReceiveEvent { sender, player ->
            whisperReplyRegistry.addReply(player.uniqueId, sender)
            coroutineScope.launch {
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, .3f, 1.7f)
                bukkitDelay(1)
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, .3f, 1.85f)
                bukkitDelay(1)
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, .3f, 2.0f)
            }
        }.setColor(0xddff8c).build()

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