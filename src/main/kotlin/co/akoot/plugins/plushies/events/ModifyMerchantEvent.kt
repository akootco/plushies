package co.akoot.plugins.plushies.events

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.events.FoxEventCancellable
import co.akoot.plugins.plushies.util.Recipes.getMaterial
import org.bukkit.entity.Villager
import org.bukkit.inventory.Merchant
import org.bukkit.inventory.MerchantRecipe

class ModifyMerchantEvent(val merchant: Merchant) : FoxEventCancellable() {

    fun addTrades(type: String, config: FoxConfig) {
        val current = merchant.recipes.toMutableList()
        val trades = mutableListOf<String>()

        if (type == "wandering_trader") {
            trades.addAll(config.getStringList(type))
        } else {
            val villager = merchant as? Villager ?: return
            val lvl = villager.villagerLevel
            val recipes = config.getStringList("$type.$lvl")

            trades.addAll(recipes)
        }

        for (trade in trades) {
            val recipe = trade.split(":")
            if (recipe.size != 2) continue

            val buyMat = recipe[0].split("/")
            val sellMat = recipe[1].split("/")

            val buyItem = getMaterial(buyMat[0], buyMat.getOrNull(1)?.toIntOrNull() ?: 1) ?: continue
            val sellItem = getMaterial(sellMat[0], sellMat.getOrNull(1)?.toIntOrNull() ?: 1) ?: continue

            current.add(MerchantRecipe(sellItem, 12).apply {
                villagerExperience = (merchant as? Villager)?.villagerLevel?.times(5) ?: 10
                addIngredient(buyItem)
                setExperienceReward(true)
                setIgnoreDiscounts(false)
            })
        }

        merchant.recipes = current
    }
}
