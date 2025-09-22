package kr.hqservice.x.chat.core.hook

import kr.hqservice.framework.bukkit.core.component.module.Module
import kr.hqservice.framework.bukkit.core.component.module.Setup

@Module
class PlaceholderModule(
    val hook: PlaceholderHook
) {
    @Setup
    fun setup() {
        hook.register()
    }
}