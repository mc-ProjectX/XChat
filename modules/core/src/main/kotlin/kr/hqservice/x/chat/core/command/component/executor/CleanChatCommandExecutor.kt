package kr.hqservice.x.chat.core.command.component.executor

import kr.hqservice.framework.global.core.component.Component
import kr.hqservice.framework.netty.api.PacketSender
import kr.hqservice.x.chat.core.command.component.BukkitCommandComponent
import kr.hqservice.x.chat.core.network.packet.ChatClearPacket
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Component
class CleanChatCommandExecutor(
    private val packetSender: PacketSender
) : BukkitCommandComponent {
    override fun getBukkitCommand(): String {
        return "채팅청소"
    }

    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<String>): Boolean {
        val playerLabel = if (p0 is Player) p0.displayName else "관리자"
        val clearChatPacket = ChatClearPacket(playerLabel)
        packetSender.sendPacketAll(clearChatPacket)
        return true
    }
}