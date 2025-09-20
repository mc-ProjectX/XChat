package kr.hqservice.x.chat.core.hook

import kr.hqservice.framework.global.core.component.Bean
import net.milkbowl.vault.economy.Economy
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import kotlin.jvm.java

@Bean
class EconomyHook(
    private val plugin: Plugin
) {
    private val economy = plugin.server.servicesManager.getRegistration(Economy::class.java)!!.provider

    fun getBalance(player: Player): Long {
        return economy.getBalance(player).toLong()
    }

    fun hasMoney(target: Player, money: Long): Boolean {
        return economy.has(target, money.toDouble())
    }

    fun withdrawMoney(target: Player, money: Long) {
        economy.withdrawPlayer(target, money.toDouble())
    }

    fun depositMoney(target: Player, money: Long) {
        economy.depositPlayer(target, money.toDouble())
    }
}