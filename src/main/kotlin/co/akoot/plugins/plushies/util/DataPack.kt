package co.akoot.plugins.plushies.util

import co.akoot.plugins.plushies.Plushies.Companion.conf
import co.akoot.plugins.plushies.Plushies.Companion.customMusicDiscConfig
import co.akoot.plugins.plushies.Plushies.Companion.key
import co.akoot.plugins.plushies.util.ItemCreator.createItem
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.Util.pl
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey.JUKEBOX_SONG
import java.io.File

object DataPack {

    private val dataPack: File
        get() {
            val worldFolder = pl.server.getWorld("world")?.worldFolder
            val dataPackFolder = File(worldFolder, "datapacks")
            return File(dataPackFolder, "plushies")
        }

    private fun createPack(): File {
        dataPack.deleteRecursively() // delete data pack
        dataPack.mkdirs() // create new

        val packMeta = File(dataPack, "pack.mcmeta")
        val mcMeta = """
            {
              "pack": {
                "pack_format": ${conf.getInt("pack.dpFormat") ?: 61},
                "description": "Plushies"
              }
            }
            """.trimIndent()
        packMeta.writeText(mcMeta)

        val plushPack = File(dataPack, "data/plushies")
        plushPack.mkdirs()

        return plushPack
    }

    fun createDiscFiles() {
        val jukeboxFolder = File(createPack(), "jukebox_song")
        jukeboxFolder.mkdirs()
        customMusicDiscConfig.getKeys().forEach { song ->
            val songInfo = """
            {
              "description": "${customMusicDiscConfig.getString("$song.description") ?: song}",
              "comparator_output": 1,
              "length_in_seconds": ${customMusicDiscConfig.getInt("$song.seconds") ?: return@forEach},
              "sound_event": {
                "sound_id": "plushies:$song"
              }
            }
            """.trimIndent()

            File(jukeboxFolder, "$song.json").writeText(songInfo)
        }
    }

    fun createDiscItems() {
        customMusicDiscConfig.getKeys().forEach { song ->
            // check if song exist in the registry
            val songID = RegistryAccess.registryAccess()
                .getRegistry(JUKEBOX_SONG)
                .get(key(song)) ?: run {
                pl.logger.warning("$song does not exist, skipping...")
                return@forEach
            }
            // create disc, or don't
            // i won't mind not one bit!
            val item = createItem(customMusicDiscConfig, song, key("item")) ?: return@forEach
            customItems[song] = ItemBuilder.builder(item)
                .jukeboxSong(songID)
                .build()
        }
    }
}