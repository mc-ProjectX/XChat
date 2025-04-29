package kr.hqservice.x.chat.core.component.handler

import kr.hqservice.framework.global.core.component.handler.ComponentHandler
import kr.hqservice.framework.global.core.component.handler.HQComponentHandler
import kr.hqservice.x.chat.api.XChatMode
import kr.hqservice.x.chat.core.component.registry.ChatModeComponentRegistry
import java.util.logging.Logger

@ComponentHandler
class ChatModeComponentHandler(
    private val logger: Logger,
    private val chatModeComponentRegistry: ChatModeComponentRegistry
) : HQComponentHandler<XChatMode> {
    override fun setup(element: XChatMode) {
        if (element.getKey().isNotEmpty()) {
            if (chatModeComponentRegistry.findChatMode(element.getKey()) != null) {
                logger.warning("ChatMode with ID ${element.getKey()}(${element.getKey()}) is already registered. It will be replaced.")
            }

            chatModeComponentRegistry.registerChatMode(element)
            logger.info("ChatMode with ID ${element.getKey()}(${element.getKey()}) has been registered.")
        } else {
            logger.warning("ChatMode with ID ${element.getKey()}(${element.getKey()}) is not valid. It will not be registered.")
        }
    }
}