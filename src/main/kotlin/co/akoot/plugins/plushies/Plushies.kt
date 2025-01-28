package co.akoot.plugins.plushies

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.Plushies.Configs.conf
import co.akoot.plugins.plushies.Plushies.Configs.headConf
import co.akoot.plugins.plushies.Plushies.Configs.pConf
import co.akoot.plugins.plushies.commands.*
import co.akoot.plugins.plushies.listeners.GUI
import co.akoot.plugins.plushies.listeners.EntityEvents
import co.akoot.plugins.plushies.listeners.PlayerEvents
import co.akoot.plugins.plushies.util.Recipes.addRecipes

class Plushies : FoxPlugin("plushies") {

    object Configs {
        lateinit var pConf: FoxConfig
        lateinit var conf: FoxConfig
        lateinit var headConf: FoxConfig
    }

    override fun load() {
        logger.info("welcome back!")
        addRecipes()
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
        registerCommand(HatCommand(this))
        registerCommand(PlushieCommand(this))
        registerCommand(BookCommand(this))
        registerCommand(PresetCommand(this))
        registerCommand(EnchantCommand(this))
        registerCommand(ItemEditCommand(this))
        registerCommand(ResourcePackCommand(this))
    }

    override fun registerEvents() {
        registerEventListener(GUI())
        registerEventListener(EntityEvents())
        registerEventListener(PlayerEvents(this))
    }

    override fun registerConfigs() {
        registerConfig("lays")
        registerConfig("ai")
        pConf = registerConfig("plushies")
        conf = registerConfig("main")
        headConf = registerConfig("heads")
    }
}