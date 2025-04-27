package kr.hqservice.x.chat.core.command

import kr.hqservice.framework.command.Command
import kr.hqservice.framework.command.CommandExecutor
import kr.hqservice.x.chat.api.format.ErrorFormat
import kr.hqservice.x.chat.api.format.InfoFormat
import kr.hqservice.x.chat.api.service.ChatService
import org.bukkit.entity.Player

@Command(label = "test")
class TestCommand(
    private val chatService: ChatService
) {
    @CommandExecutor(
        label = "info"
    ) fun infoChat(sender: Player, senderName: String, message: String) {
        chatService.sendChat(listOf(sender.uniqueId), message.replace("_", " "), InfoFormat, senderName, logging = false)
    }

    @CommandExecutor(
        label = "error"
    ) fun errorChat(sender: Player, message: String) {
        chatService.sendChat(listOf(sender.uniqueId), message.replace("_", " "), ErrorFormat, "", logging = false)
    }
}