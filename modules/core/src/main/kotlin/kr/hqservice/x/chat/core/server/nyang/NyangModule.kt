package kr.hqservice.x.chat.core.server.nyang

import kr.hqservice.framework.bukkit.core.component.module.Module
import kr.hqservice.framework.bukkit.core.component.module.Setup
import kr.hqservice.x.chat.core.server.nyang.hook.PlaceHolderHook

@Module
class NyangModule(
    val hook: PlaceHolderHook
) {
    @Setup
    fun setup() {
        hook.register()
    }
}