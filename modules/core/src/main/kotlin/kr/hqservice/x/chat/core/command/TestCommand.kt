package kr.hqservice.x.chat.core.command

import kr.hqservice.framework.command.Command
import kr.hqservice.framework.command.CommandExecutor
import kr.hqservice.x.chat.api.DefaultChatMode
import kr.hqservice.x.chat.api.service.XChatService
import org.bukkit.entity.Player

@Command(label = "test")
class TestCommand(
    private val chatService: XChatService
) {
    @CommandExecutor(
        label = "info"
    ) fun infoChat(sender: Player, senderName: String, message: String) {
        chatService.sendChat(
            sender.uniqueId,
            chatService.getConsoleSender(senderName),
            message.replace("_", " "),
            chatService.getDefaultChatMode(DefaultChatMode.INFO)
        )
    }

    @CommandExecutor(
        label = "error"
    ) fun errorChat(sender: Player, message: String) {
        chatService.sendChat(
            sender.uniqueId,
            chatService.getConsoleSender(),
            message.replace("_", " "),
            chatService.getDefaultChatMode(DefaultChatMode.ERROR)
        )
    }
}