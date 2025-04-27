package kr.hqservice.x.chat.api.event

import kr.hqservice.x.chat.api.ChatMode
import kr.hqservice.x.chat.api.ChatSender
import kr.hqservice.x.core.api.XPlayer
import net.kyori.adventure.text.Component
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class AsyncXChatEvent(
    private val user: ChatSender,
    private val mode: ChatMode,
    private val message: Component,
    private val receivers: List<XPlayer>
) : Event(true), Cancellable {
    private var cancel = false

    override fun setCancelled(p0: Boolean) {
        this.cancel = p0
    }

    override fun isCancelled(): Boolean {
        return cancel
    }

    override fun getHandlers(): HandlerList {
        return getHandlerList()
    }

    companion object {
        private val HANDLER_LIST = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLER_LIST
        }
    }
}