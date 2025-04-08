package co.akoot.plugins.plushies.listeners.handlers

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.removePDC
import co.akoot.plugins.bluefox.extensions.setPDC
import co.akoot.plugins.bluefox.util.runLater
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.util.Util.getBlockPDC
import io.papermc.paper.registry.keys.SoundEventKeys.MUSIC_DISC_11
import net.kyori.adventure.sound.Sound
import org.bukkit.SoundCategory
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

fun playMusic(item: ItemStack, block: Block) {
    val music = item.itemMeta.getPDC<String>(key("music_disc")) ?: return

    // play song
    block.location.add(0.5, 0.5, 0.5).world
        ?.playSound(block.location, music, SoundCategory.RECORDS, 1f, 1f)

    // cancel normal disc song
    runLater(2) {
        block.location.world.stopSound(Sound.sound(MUSIC_DISC_11, Sound.Source.RECORD, 1f, 1f))
    }

    block.chunk.setPDC<String>(getBlockPDC(block.location), music)
}

fun stopMusic(block: Block) {
    val musicDisc = block.chunk.getPDC<String>(getBlockPDC(block.location)) ?: return

    block.location.world.stopSound(Sound.sound(key(musicDisc.split(":")[1]),
        Sound.Source.RECORD, 1f, 1f))

    block.chunk.removePDC(getBlockPDC(block.location))
}