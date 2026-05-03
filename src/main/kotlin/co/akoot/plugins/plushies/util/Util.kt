package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.BlueFox
import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.hasPDC
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.toBukkitColor
import co.akoot.plugins.plushies.Plushies
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.util.Items.isDyeable
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import com.destroystokyo.paper.MaterialTags
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.kyori.adventure.text.Component
import org.bukkit.DyeColor
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object Util {
    val pl: Plushies = JavaPlugin.getPlugin(Plushies::class.java)

    fun plushMsg(name: String): Text {
        return Kolor.TEXT("Please cherish this ") +
                Kolor.ACCENT(name) +
                Kolor.TEXT(" plushie forever")
    }

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

    fun getBlockPDC(location: Location, plugin: String = "plushies"): NamespacedKey {
        val key = "${location.world.name.lowercase()}.${location.blockX}.${location.blockY}.${location.blockZ}"
        return NamespacedKey(plugin, key)
    }

    fun resolvePlaceholders(string: String): Component {
        val words = string.split(" ")
        val result = Text()

        for ((index, word) in words.withIndex()) {
            when {
                word.startsWith("p:") -> result.playerHead(word.drop(2))
                word.startsWith("i:") -> result.sprite(word.drop(2))
                else -> result += Text(word)
            }

            if(index < words.size - 1) {
                result += Text.space
            }
        }

        return result.component
    }

    val World.isDefault: Boolean
        get() = name in BlueFox.instance.settings.getStringList("wallet.worlds")

    fun Player.inValidWorld(context: FoxCommand? = null): Boolean {
        return if (!world.isDefault) {
            if (context != null) sendMessage(Text("/${context.id} is not allowed in this world!", Kolor.ERROR).component)
            false
        } else true
    }

    fun isDyeRecipe(matrix: Array<ItemStack?>): Boolean {
        val items = matrix.filterNotNull()

        if (items.size != 2) return false

        val hasDyeable = items.any { it.isDyeable }
        val hasDye = items.any { MaterialTags.DYES.isTagged(it.type) }

        return hasDyeable && hasDye
    }

    fun dyeItem(matrix: Array<ItemStack?>): ItemStack? {
        val items = matrix.filterNotNull()

        val dyeable = items.firstOrNull { it.isDyeable } ?: return null
        val dye = items.firstOrNull { MaterialTags.DYES.isTagged(it.type) } ?: return null

        val dyeColor = DyeColor.valueOf(dye.type.name.removeSuffix("_DYE")).color
        val baseColor = dyeable.getData(DataComponentTypes.DYED_COLOR)?.color()

        val finalColor = when {
            dye.itemMeta?.hasPDC(NamespacedKey("choco", "rgb_dye")) == true ->
                dye.itemMeta?.displayName()?.color()?.toBukkitColor()

            baseColor != null -> baseColor.mixColors(dyeColor)

            else -> dyeColor
        }

        return ItemBuilder.builder(dyeable.clone().asOne())
            .dye(finalColor ?: return null)
            .build()
    }
}