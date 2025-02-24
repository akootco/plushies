package co.akoot.plugins.plushies

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.commands.*
import co.akoot.plugins.plushies.listeners.GUI
import co.akoot.plugins.plushies.listeners.EntityEvents
import co.akoot.plugins.plushies.listeners.Events
import co.akoot.plugins.plushies.listeners.PlayerEvents
import co.akoot.plugins.plushies.util.Recipes.registerRecipes

class Plushies : FoxPlugin("plushies") {

    companion object {
        lateinit var plushieConf: FoxConfig
        lateinit var conf: FoxConfig
        lateinit var headConf: FoxConfig
        lateinit var aiConf: FoxConfig
        lateinit var laysConf: FoxConfig
        lateinit var recipeConf: FoxConfig
        lateinit var cookRecipeConf: FoxConfig
    }

    override fun load() {
        logger.info("welcome back!")
        registerRecipes()
    }

    override fun unload() {
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
    }
}