package co.akoot.plugins.plushies.listeners

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.hasMeta
import co.akoot.plugins.bluefox.extensions.removeMeta
import co.akoot.plugins.bluefox.extensions.removePDC
import co.akoot.plugins.bluefox.extensions.setMeta
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.Plushies.Companion.tradeSource
import co.akoot.plugins.plushies.events.ModifyMerchantEvent
import co.akoot.plugins.plushies.util.id
import org.bukkit.entity.Villager
import org.bukkit.entity.WanderingTrader
import org.bukkit.entity.memory.MemoryKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.entity.VillagerCareerChangeEvent

class MerchantEvents : Listener {

    var Villager.customProfession: String?
        get() = getPDC<String>(key("villager.profession"))
        set(value) {
            if (value == null)  {
                removePDC(key("villager.profession"))
                customName(null)
            }
            else {
                customName(Text("Darnell").component)
                setPDC(key("villager.profession"), value)
            }
        }

    @EventHandler
    fun onModifyMerchant(event: ModifyMerchantEvent) {
        val type = when (val merchant = event.merchant) {
            is Villager -> merchant.customProfession ?: return
            is WanderingTrader -> "wandering_trader"
            else -> return
        }

        tradeSource.forEach { conf -> event.addTrades(type, conf) }
    }

    @EventHandler
    fun VillagerCareerChangeEvent.onVillagerCareerChange() {
        if (reason == VillagerCareerChangeEvent.ChangeReason.LOSING_JOB) {
            entity.customProfession = null
            return
        }

        val jobSite = entity.getMemory(MemoryKey.JOB_SITE)?.block?.location?.id ?: return
        val id = jobSite.substringBefore('|')

        if (entity.profession == Villager.Profession.NONE && tradeSource.any { it.getKeys().contains(id) }) {
            entity.customProfession = id
            return
        }

        isCancelled = entity.customProfession != jobSite
    }


    @EventHandler
    fun VillagerAcquireTradeEvent.onVillagerLevelUp() {
        val villager = entity as? Villager ?: return

        villager.customProfession?.let {
            isCancelled = true

            val key = "crillager"
            if (villager.hasMeta(key)) return

            villager.setMeta(key, true)
            runLater(2) { villager.removeMeta(key) }

            ModifyMerchantEvent(villager).fire() ?: return
        }
    }
}