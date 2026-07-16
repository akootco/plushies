package co.akoot.plugins.plushies.events

import co.akoot.plugins.bluefox.api.events.FoxEvent
import org.bukkit.block.Block

class RemoveCustomBlockEvent(val block: Block) : FoxEvent()