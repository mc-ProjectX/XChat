package kr.hqservice.x.chat.core.component.registry

import kr.hqservice.framework.global.core.component.Bean
import kr.hqservice.x.chat.api.ChatMode
import kr.hqservice.x.chat.core.component.DefaultChatModeComponent

@Bean
class ChatModeComponentRegistry {
    private val chatModeComponents = mutableMapOf<Int, ChatMode>()
    private val defaultChatMode = DefaultChatModeComponent

    fun registerChatMode(chatMode: ChatMode) {
        chatModeComponents[chatMode.getId()] = chatMode
    }

    fun findChatMode(id: Int): ChatMode? {
        return chatModeComponents[id]
    }

    fun getDefaultChatMode(): ChatMode {
        return defaultChatMode
    }

    fun getAllChatModes(): Collection<ChatMode> {
        return chatModeComponents.values
    }
}