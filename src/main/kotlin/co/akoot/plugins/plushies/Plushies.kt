package co.akoot.plugins.plushies

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.Plushies.Configs.pConf
import co.akoot.plugins.plushies.commands.*
import co.akoot.plugins.plushies.listeners.GUI

class Plushies : FoxPlugin("plushies") {

    object Configs {
        lateinit var pConf: FoxConfig
    }

    override fun load() {
        logger.info("welcome back!")
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
    }

    override fun registerEvents() {
        registerEventListener(GUI())
    }

    override fun registerConfigs() {
        registerConfig("lays")
        registerConfig("ai")
        pConf = registerConfig("plushies")
    }
}