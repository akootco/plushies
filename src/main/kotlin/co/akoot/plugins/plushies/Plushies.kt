package co.akoot.plugins.plushies

import co.akoot.plugins.bluefox.api.FoxPlugin

class Plushies : FoxPlugin("plushies") {

    override fun load() {
        logger.info("welcome back!")
    }

    override fun unload() {
        logger.info(":i_sleep:")
    }
}