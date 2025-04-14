package co.akoot.plugins.plushies

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.commands.*
import co.akoot.plugins.plushies.commands.bluemap.*
import co.akoot.plugins.plushies.listeners.GUI
import co.akoot.plugins.plushies.listeners.EntityEvents
import co.akoot.plugins.plushies.listeners.Events
import co.akoot.plugins.plushies.listeners.PlayerEvents
import co.akoot.plugins.plushies.util.Items.loadItems
import co.akoot.plugins.plushies.util.Recipes.registerRecipes
import co.akoot.plugins.plushies.util.DataPack.createDiscs
import co.akoot.plugins.plushies.util.DataPack.dataPack
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin

class Plushies : FoxPlugin("plushies") {

    companion object {
        lateinit var plushieConf: FoxConfig
        lateinit var conf: FoxConfig
        lateinit var headConf: FoxConfig
        lateinit var aiConf: FoxConfig
        lateinit var laysConf: FoxConfig
        lateinit var recipeConf: FoxConfig
        lateinit var cookRecipeConf: FoxConfig
        lateinit var customItemConfig: FoxConfig
        lateinit var customMusicDiscConfig: FoxConfig

        fun key(key: String): NamespacedKey {
            return NamespacedKey("plushies", key)
        }

        private fun checkPlugin(name: String): Plugin? = Bukkit.getPluginManager().getPlugin(name)
        fun pluginEnabled(name: String): Boolean = checkPlugin(name)?.isEnabled == true
    }

    override fun load() {
        logger.info("welcome back!")

        registerRecipes()
        loadItems(customItemConfig)
        createDiscs() // create and enable music disc datapack
    }

    override fun unload() {
        dataPack.deleteRecursively() // delete data pack
        logger.info(":i_sleep:")
    }

    override fun registerCommands() {
        registerCommand(MaceCommand(this))
        registerCommand(EditBookCommand(this))
        registerCommand(LaysCommand(this))
        registerCommand(AICommand(this))
        registerCommand(ThrowableCommand(this))
        registerCommand(PlushieCommand(this))
        registerCommand(BookCommand(this))
        registerCommand(PresetCommand(this))
        registerCommand(EnchantCommand(this))
        registerCommand(ItemEditCommand(this))
        registerCommand(ResourcePackCommand(this))
        registerCommand(GolfCommand(this))
        registerCommand(BookArchiveCommand(this))
        registerCommand(CustomItemCommand(this))
        registerCommand(ToggleArmorCommand(this))

        // bluemap commands
        registerCommand(ShowCommand(this))
        registerCommand(HideCommand(this))
    }

    override fun registerEvents() {
        registerEventListener(GUI())
        registerEventListener(EntityEvents(this))
        registerEventListener(PlayerEvents(this))
        registerEventListener(Events())
    }

    override fun registerConfigs() {
        aiConf = registerConfig("ai", "data/ai.conf")
        laysConf = registerConfig("lays", "data/lays.conf")
        plushieConf = registerConfig("plushies")
        conf = registerConfig("main")
        headConf = registerConfig("heads", "data/heads.conf")
        recipeConf = registerConfig("craftingRecipes", "recipes/recipes.conf")
        cookRecipeConf = registerConfig("cookRecipes", "recipes/cook_recipes.conf")
        customItemConfig = registerConfig("customItems", "data/items.conf")
        customMusicDiscConfig = registerConfig("customMusicDiscs", "data/music_discs.conf")
    }
}