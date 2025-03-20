package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.plushies.Plushies
import co.akoot.plugins.plushies.Plushies.Companion.key
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object Util {
    val pl: Plushies = JavaPlugin.getPlugin(Plushies::class.java)

    fun loadYamlConfig(path: String): FileConfiguration {
        val file = File(pl.dataFolder, path)
        // Check if the file exists. if not, save it
        if (!file.exists()) {
            try {
                pl.logger.warning("Creating file: $path")
                file.apply {
                    parentFile.mkdirs()
                    createNewFile()
                }
            } catch (e: Exception) {
                pl.logger.warning("Error saving file!")
            }
        }
        return YamlConfiguration.loadConfiguration(file)
    }

    fun setAttributes(item: ItemStack, player: Player) {
        val attributes = item.itemMeta.getPDC<String>(key("attributes")) ?: return
        val attributeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE)

        // name/value;name/value;name/value
        attributes.split(";").forEach {
            val (name, value) = it.split("/")

            // make sure the attribute exists
            val attribute = attributeRegistry[NamespacedKey.minecraft(name.lowercase())] ?: return
            val att = player.getAttribute(attribute) ?: return // return if the player doesn't have the attribute

            // set the value
            att.baseValue += value.toDouble()
        }
    }

    // i dont know about this one!
}