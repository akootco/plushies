package co.akoot.plugins.plushies

import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.commands.*

class Plushies : FoxPlugin("plushies") {

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
    }

    override fun registerConfigs() {
        // this is the only way i was able to get the file to load correctly
        registerConfig("lays")
        registerConfig("ai")
    }
}