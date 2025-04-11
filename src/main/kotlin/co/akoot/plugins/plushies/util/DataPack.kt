package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.Plushies.Companion.conf
import co.akoot.plugins.plushies.Plushies.Companion.customMusicDiscConfig
import java.io.File

object DataPack {

    // TODO: enable pack and create item using ItemFactory. paper fail?

    fun createPack(plugin: FoxPlugin): File {
        val worldFolder = plugin.server.getWorld("world")?.worldFolder
        val dataPackFolder = File(worldFolder, "datapacks")

        val pack = File(dataPackFolder, "plushies")
        pack.mkdirs()

        val packMeta = File(pack, "pack.mcmeta")
        val mcMeta = """
            {
              "pack": {
                "pack_format": ${conf.getInt("pack.dpFormat")?: 61},
                "description": "Plushies"
              }
            }
            """.trimIndent()
        packMeta.writeText(mcMeta)

        val plushPack = File(pack, "data/plushies")
        plushPack.mkdirs()

        return plushPack
    }

    fun createDiscs(plushPack: File) {
        val jukeboxFolder = File(plushPack, "jukebox_songs")
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
}