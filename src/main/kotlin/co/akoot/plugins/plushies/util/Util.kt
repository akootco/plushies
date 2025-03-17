package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.api.FoxPlugin
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object Util {
    fun loadYamlConfig(plugin: FoxPlugin, path: String): FileConfiguration {
        val file = File(plugin.dataFolder, path)
        // Check if the file exists. if not, save it
        if (!file.exists()) {
            try {
                plugin.logger.warning("Creating file: $path")
                file.apply {
                    parentFile.mkdirs()
                    createNewFile()
                }
            } catch (e: Exception) {
                plugin.logger.warning("Error saving file!")
            }
        }
        return YamlConfiguration.loadConfiguration(file)
    }
}