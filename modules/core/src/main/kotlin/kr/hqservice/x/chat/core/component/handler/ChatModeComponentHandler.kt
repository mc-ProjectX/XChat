package kr.hqservice.x.chat.core.component.handler

import kr.hqservice.framework.global.core.component.handler.ComponentHandler
import kr.hqservice.framework.global.core.component.handler.HQComponentHandler
import kr.hqservice.x.chat.api.ChatMode
import kr.hqservice.x.chat.core.component.registry.ChatModeComponentRegistry
import java.util.logging.Logger

@ComponentHandler
class ChatModeComponentHandler(
    private val logger: Logger,
    private val chatModeComponentRegistry: ChatModeComponentRegistry
) : HQComponentHandler<ChatMode> {
    override fun setup(element: ChatMode) {
        if (element.getId() >= 0) {
            if (chatModeComponentRegistry.findChatMode(element.getId()) != null) {
                logger.warning("ChatMode with ID ${element.getId()}(${element.getName()}) is already registered. It will be replaced.")
            }

            chatModeComponentRegistry.registerChatMode(element)
            logger.info("ChatMode with ID ${element.getId()}(${element.getName()}) has been registered.")
        } else {
            logger.warning("ChatMode with ID ${element.getId()}(${element.getName()}) is not valid. It will not be registered.")
        }
    }
}