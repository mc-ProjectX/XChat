package kr.hqservice.x.chat.core.server.nyang.hook

import kr.hqservice.framework.global.core.component.Bean
import kr.hqservice.x.chat.core.service.PlayerChatModeService
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

@Bean
class PlaceHolderHook(
    private val xChatModeService: PlayerChatModeService
) : PlaceholderExpansion() {

    override fun getIdentifier(): String {
        return "xchat"
    }

    override fun getAuthor(): String {
        return "ggpl"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun onRequest(player: OfflinePlayer, params: String): String? {
        when (params) {
            "mode" -> {
                return xChatModeService.getCurrentChatMode(player.uniqueId).getKey().lowercase()
            }
        }
        return null
    }
}