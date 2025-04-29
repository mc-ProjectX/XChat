package kr.hqservice.x.chat.core.command

import kr.hqservice.framework.bukkit.core.HQBukkitPlugin
import kr.hqservice.framework.bukkit.core.component.module.Module
import kr.hqservice.framework.bukkit.core.component.module.Setup

@Module
class BukkitCommandSetupModule(
    private val plugin: HQBukkitPlugin,
    private val whisperCommandExecutor: WhisperCommandExecutor,
    private val replyCommandExecutor: ReplyCommandExecutor,
    private val operatorChatCommandExecutor: OperatorChatCommandExecutor,
    private val guideChatCommandExecutor: GuideChatCommandExecutor,
) {
    @Setup
    fun setup() {
        plugin.getCommand("귓")
            ?.apply {
                setExecutor(whisperCommandExecutor)
                tabCompleter = whisperCommandExecutor
            }

        plugin.getCommand("답장")
            ?.apply {
                setExecutor(replyCommandExecutor)
                tabCompleter = replyCommandExecutor
            }

        plugin.getCommand("관리자채팅")
            ?.setExecutor(operatorChatCommandExecutor)

        plugin.getCommand("가이드채팅")
            ?.setExecutor(guideChatCommandExecutor)
    }
}