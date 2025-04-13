package co.akoot.plugins.plushies.util

import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.plushies.Plushies.Companion.conf
import co.akoot.plugins.plushies.Plushies.Companion.customMusicDiscConfig
import co.akoot.plugins.plushies.util.Items.customItems
import java.io.File

class DataPack(private val plugin: FoxPlugin) {

    // TODO: enable pack

    private fun createPack(): File {
        val worldFolder = plugin.server.getWorld("world")?.worldFolder
        val dataPackFolder = File(worldFolder, "datapacks")
        val dataPack = File(dataPackFolder, "plushies")

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

    fun createDiscs() {
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

            try {
                val item =
                    "music_disc_11[jukebox_playable={song:'plushies:$song'},custom_model_data={floats:[${customMusicDiscConfig.getInt("$song.customModelData")}]}]"

                customItems[song] = plugin.server.itemFactory.createItemStack(item)
            } catch (e: IllegalArgumentException) {
                plugin.logger.warning("Failed to create disc for $song")
                return@forEach
            }
        }
    }
}