package co.akoot.plugins.plushies.util

import co.akoot.plugins.plushies.Plushies
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object Util {
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

    // i dont know about this one!
    val pl: Plushies = JavaPlugin.getPlugin(Plushies::class.java)
}