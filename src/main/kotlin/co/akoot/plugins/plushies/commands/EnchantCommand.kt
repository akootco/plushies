package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.invoke
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandSender

class EnchantCommand(plugin: FoxPlugin) : FoxCommand(plugin, "enchant") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val enchantmentKeys = mutableListOf<String>()

        for (enchantment in RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)) {
            enchantmentKeys.add(enchantment.key.key)
        }

        if (args.size == 1) {
            return enchantmentKeys
        }

        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        if (args.isEmpty()) {
            return Result.fail(Kolor.ERROR.accent("/$name") + Kolor.TEXT(" <enchantment>") + Kolor.NUMBER(" <level>")).getAndSend(p)
        }

        val item = p.inventory.itemInMainHand

        if (item.type == Material.AIR) {
            return sendError(p, "You must hold something.")
        }

        val enchant = RegistryAccess.registryAccess()
            .getRegistry(RegistryKey.ENCHANTMENT)
            .get(NamespacedKey.minecraft(args[0]))

        if (enchant == null) {
            return sendError(p, "${args[0]} is not a valid enchantment.")
        }
        val lvl = args.getOrNull(1)?.toIntOrNull() ?: 1

        if (lvl !in 1..255) {
            return sendError(p, "level must be between 1 and 255.")
        }

        ItemBuilder.builder(item)
            .addEnchant(enchant, lvl)
            .build()

        return true
    }
}

