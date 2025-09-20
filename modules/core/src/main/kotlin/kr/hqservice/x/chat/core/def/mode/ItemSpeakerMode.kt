package kr.hqservice.x.chat.core.def.mode

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.x.chat.api.XChatData
import kr.hqservice.x.chat.api.XChatFormat
import kr.hqservice.x.chat.api.XChatMode
import kr.hqservice.x.chat.core.XChatItemSender

@Component
class ItemSpeakerMode : XChatMode {
    object SpeakerFormat : XChatFormat {
        override fun format(xChatData: XChatData): net.kyori.adventure.text.Component {
            val sender = xChatData.getSender() as XChatItemSender
            val itemStack = sender.getItemStack()
            return xChatData
                .getMessage()
                .hoverEvent(itemStack.asHoverEvent())
        }
    }

    override fun getKey(): String {
        return "item_speaker"
    }

    override fun getFormat(): XChatFormat {
        return SpeakerFormat
    }

    override fun isLoggingEnabled(): Boolean {
        return true
    }
}