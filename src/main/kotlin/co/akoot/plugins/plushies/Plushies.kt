package co.akoot.plugins.plushies

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.commands.*
import co.akoot.plugins.plushies.commands.bluemap.*
import co.akoot.plugins.plushies.geyser.GeyserRegistrar
import co.akoot.plugins.plushies.geyser.GeyserUtil.downloadBedrockPack
import co.akoot.plugins.plushies.listeners.*
import co.akoot.plugins.plushies.util.DataPack.createDiscFiles
import co.akoot.plugins.plushies.util.DataPack.createDiscItems
import co.akoot.plugins.plushies.util.Items.loadItems
import co.akoot.plugins.plushies.util.Recipes.registerPlushieRecipes
import co.akoot.plugins.plushies.util.ResourcePack.getJavaPack
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin
import org.geysermc.geyser.api.GeyserApi
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomSkullsEvent

class Plushies : FoxPlugin("plushies") {

    companion object {
        lateinit var plushieConf: FoxConfig
        lateinit var conf: FoxConfig
        lateinit var headConf: FoxConfig
        lateinit var aiConf: FoxConfig
        lateinit var laysConf: FoxConfig
        lateinit var recipeConf: FoxConfig
        lateinit var cookRecipeConf: FoxConfig
        lateinit var smithRecipeConf: FoxConfig
        lateinit var customItemConfig: FoxConfig
        lateinit var customMusicDiscConfig: FoxConfig
        lateinit var customBlockConfig: FoxConfig
        lateinit var customDialogConfig: FoxConfig

        fun key(key: String): NamespacedKey {
            return NamespacedKey("plushies", key)
        }

        private fun checkPlugin(name: String): Plugin? = Bukkit.getPluginManager().getPlugin(name)
        fun pluginEnabled(name: String): Boolean = checkPlugin(name)?.isEnabled == true
    }

    override fun load() {
        logger.info("welcome back!")
        getJavaPack()

        loadItems()
        createDiscItems() // attempt to create music discs
        registerPlushieRecipes()

        if (pluginEnabled("Geyser-Spigot")) {
            val geyser = GeyserRegistrar()
            val bus = GeyserApi.api().eventBus()
            // get bedrock pack
            downloadBedrockPack()
            // register geyser events
            bus.subscribe(geyser, GeyserDefineCustomItemsEvent::class.java, geyser::registerItems)
            bus.subscribe(geyser, GeyserDefineCustomSkullsEvent::class.java, geyser::registerHeads)
        }
    }

    override fun unload() {
        // create music disc datapack here
        createDiscFiles()
        logger.info(":i_sleep:")
    }

    override fun registerCommands() {
        registerCommand(MaceCommand(this))
        registerCommand(LaysCommand(this))
        registerCommand(AICommand(this))
        registerCommand(ThrowableCommand(this))
        registerCommand(PlushieCommand(this))
        //test
        registerCommand(Plushie2Command(this))
        registerCommand(DialogTestCommand(this))
        //crest
        registerCommand(BookCommand(this))
        registerCommand(PresetCommand(this))
        registerCommand(EnchantCommand(this))
        registerCommand(ItemEditCommand(this))
        registerCommand(ResourcePackCommand(this))
        registerCommand(GolfCommand(this))
        registerCommand(BookArchiveCommand(this))
        registerCommand(CustomItemCommand(this))
        registerCommand(ToggleArmorCommand(this))
        registerCommand(ReloadRecipesCommand(this))
        registerCommand(IHatePlushiesCommand(this))
        registerCommand(IHateJumpBoostCommand(this))
        registerCommand(EditBookCommand(this))
        registerCommand(ChunkPDCCommand(this))
        registerCommand(LocatorCommand(this))
        registerCommand(ATMCommand(this))
        // bluemap commands
        registerCommand(ShowCommand(this))
        registerCommand(HideCommand(this))
    }

    override fun registerEvents() {
        registerEventListener(GUI())
        registerEventListener(EntityEvents(this))
        registerEventListener(PlayerEvents(this))
        registerEventListener(Events())
        registerEventListener(HDB())
        registerEventListener(BlockEvents())
    }

    override fun registerConfigs() {
        aiConf = registerConfig("ai", "data/ai.conf")
        laysConf = registerConfig("lays", "data/lays.conf")
        plushieConf = registerConfig("plushies")
        conf = registerConfig("main")
        headConf = registerConfig("heads", "data/heads.conf")
        recipeConf = registerConfig("craftingRecipes", "recipes/recipes.conf")
        cookRecipeConf = registerConfig("cookRecipes", "recipes/cook_recipes.conf")
        smithRecipeConf = registerConfig("smithRecipes", "recipes/smithing.conf")
        customItemConfig = registerConfig("customItems", "data/items.conf")
        customBlockConfig = registerConfig("customBlocks", "data/blocks.conf")
        customMusicDiscConfig = registerConfig("customMusicDiscs", "data/music_discs.conf")
        customDialogConfig = registerConfig("customDialogs", "data/dialogs.conf")
    }
}