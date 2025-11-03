package co.akoot.plugins.plushies.events

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.events.FoxEventCancellable
import co.akoot.plugins.plushies.util.Recipes.getMaterial
import org.bukkit.inventory.Merchant
import org.bukkit.inventory.MerchantRecipe

class ModifyMerchantEvent(val merchant: Merchant) : FoxEventCancellable() {

    fun addTrades(vararg recipes: MerchantRecipe) {
        val current = merchant.recipes.toMutableList()
        current.addAll(recipes)
        merchant.recipes = current
    }

    fun addTrades(type: String, config: FoxConfig) {
        val current = merchant.recipes.toMutableList()

        for (key in config.getKeys(type)) {
            val sell = config.getString("$type.$key.sell")?.split("/") ?: continue
            val buy = config.getString("$type.$key.buy")?.split("/") ?: continue

            val sellItem = getMaterial(sell[0], sell.getOrNull(1)?.toIntOrNull() ?: 1) ?: continue
            val buyItem = getMaterial(buy[0], buy.getOrNull(1)?.toIntOrNull() ?: 1) ?: continue

            current.add(MerchantRecipe(sellItem, Int.MAX_VALUE).apply { addIngredient(buyItem) })
        }

        merchant.recipes = current
    }
}
