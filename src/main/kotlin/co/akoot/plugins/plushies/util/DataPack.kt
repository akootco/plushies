package co.akoot.plugins.plushies.util

import co.akoot.plugins.plushies.Plushies.Companion.conf
import co.akoot.plugins.plushies.Plushies.Companion.customMusicDiscConfig
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.Util.pl
import java.io.File

object DataPack {

    // TODO: enable pack

    val dataPack: File
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

    fun createDiscitems() {
        customMusicDiscConfig.getKeys().forEach { song ->
            try {
                val item =
                    "music_disc_11[jukebox_playable={song:'plushies:${song}'},custom_model_data={floats:[${
                        customMusicDiscConfig.getInt(
                            "$song.customModelData"
                        )
                    }]}]"

                customItems[song] = pl.server.itemFactory.createItemStack(item)
                pl.logger.info("Created disc: $song")

            } catch (e: IllegalArgumentException) {
                pl.logger.warning("Failed to create disc for $song")
                return@forEach
            }
        }
    }
}