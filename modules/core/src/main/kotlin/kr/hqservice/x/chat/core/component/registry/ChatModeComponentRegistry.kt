package kr.hqservice.x.chat.core.component.registry

import kr.hqservice.framework.global.core.component.Bean
import kr.hqservice.x.chat.api.XChatMode

@Bean
class ChatModeComponentRegistry {
    private val chatModeComponents = mutableMapOf<String, XChatMode>()

    fun registerChatMode(chatMode: XChatMode) {
        chatModeComponents[chatMode.getKey()] = chatMode
    }

    fun findChatMode(id: String): XChatMode? {
        return chatModeComponents[id]
    }

    fun getAllChatModes(): Collection<XChatMode> {
        return chatModeComponents.values
    }
}