package co.akoot.plugins.plushies.commands

import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.util.Items.hitSound
import co.akoot.plugins.plushies.util.Items.swingSound
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.SwingAnimation
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.*
import org.bukkit.command.CommandSender

class ItemEditCommand(plugin: FoxPlugin) : FoxCommand(plugin, "edititem") {

    private val songs: Registry<JukeboxSong> = RegistryAccess.registryAccess().getRegistry(RegistryKey.JUKEBOX_SONG)
    private val sounds: Registry<Sound> = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT)
    private val animation = SwingAnimation.Animation.entries.map { it.name.lowercase() }

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {

        if (args.size == 1) return arrayListOf(
            "name",
            "lore",
            "cmd",
            "texture",
            "dye",
            "model",
            "disc",
            "hitsound",
            "swingsound",
            "swinganimation"
        )

        return when (args[0]) {
            "dye", "lore" -> arrayListOf("-c")
            "disc" -> songs.map { it.key.key }.toMutableList()
            "swinganimation" -> animation.toMutableList()
            else -> mutableListOf()
        }
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        if (args.isEmpty()) {
            sendError(p, "Well, what do you wanna edit?.")
            return true
        }

        val item = p.inventory.itemInMainHand

        if (item.type == Material.AIR) {
            sendError(p, "You must hold something.")
            return false
        }

        val arg1 = args.getOrNull(1)

        when (args[0]) {
            "name" -> {
                if (arg1 == null) {
                    sendError(p, "Name cannot be empty.")
                    return false
                }
                ItemBuilder.builder(item)
                    .itemName(Text(args.drop(1).joinToString(" ")).component)
                    .build()

                return true
            }

            "lore" -> {
                if (arg1 == null) { return false }

                if (args[1] == "-c") {
                    ItemBuilder.builder(item)
                        .unsetData(DataComponentTypes.LORE)
                        .build()

                    return Result.success("Lore cleared.").getAndSend(p)
                }

                val lore = args.drop(1).joinToString(" ").split("\\n").map { Text(it).component }

                ItemBuilder.builder(item)
                    .lore(lore)
                    .build()

                return true
            }

            "hitsound" -> {
                if (arg1 == null) { return sendError(p, "what sound should this make?") }
                item.hitSound = args[1]
                return true
            }

            "swingsound" -> {
                if (arg1 == null) { return sendError(p, "what sound should this make?") }
                item.swingSound = args[1]
                return true
            }

            // set or clear custom model data
            "cmd" -> {
                if (arg1 == null) {
                    sendError(p, "cmd is missing.")
                    return false
                }
                when (val cmd = args[1]) {
                    "0", "null", "none" -> {
                        ItemBuilder.builder(item)
                            .unsetData(DataComponentTypes.CUSTOM_MODEL_DATA)
                            .build()
                    }

                    else -> {
                        ItemBuilder.builder(item)
                            .customModelData(cmd)
                            .build()
                    }
                }
                return true
            }

            // set or unset dye color
            "dye" -> {
                if (arg1 == null) {
                    return sendError(p, "Dye argument is missing.")
                }

                when (arg1) {
                    "-c" -> {
                        ItemBuilder.builder(item)
                            .unsetData(DataComponentTypes.DYED_COLOR)
                            .build()
                    }

                    else -> {
                        if (arg1.matches("^[A-Fa-f0-9]{6}$".toRegex())) {
                            ItemBuilder.builder(item)
                                .dye(Color.fromRGB(Integer.parseInt(arg1, 16)))
                                .build()
                        } else {
                            sendError(p, "Invalid format. Use RRGGBB.")
                        }
                    }
                }
                return true
            }

            // set head texture
            "texture" -> {
                val textureArg = args.getOrNull(1) ?: return sendError(p, "Texture argument is missing.")

                if (item.type != Material.PLAYER_HEAD) {
                    sendError(p, "You can only set textures on player heads.")
                    return false
                }

                ItemBuilder.builder(item)
                    .headTexture(textureArg)
                    .build()
                return true
            }

            // set item model
            "model" -> {
                val model = args.getOrNull(1) ?: return sendError(p, "Model argument is missing.")
                ItemBuilder.builder(item)
                    .itemModel(model)
                    .build()
                return true
            }

            // set item to play as disc
            "disc" -> {
                when (val disc = args[1]) {
                    "-c" -> {
                        ItemBuilder.builder(item)
                            .unsetData(DataComponentTypes.JUKEBOX_PLAYABLE)
                            .build()
                    }
                    else -> {
                        val song = songs[NamespacedKey("plushies", disc)] ?: songs[NamespacedKey.minecraft(disc)]
                        ?: return sendError(p, "$disc is not a valid music disc.")

                        ItemBuilder.builder(item)
                            .jukeboxSong(song)
                            .build()

                        return true
                    }
                }
                return true
            }

            "swinganimation" -> {
                if (arg1 == null) {
                    sendError(p, "swing animation is missing.")
                    return false
                }
                ItemBuilder.builder(item)
                    .apply {
                        val animation = args.getOrNull(1)
                            ?.uppercase()
                            ?.let { SwingAnimation.Animation.values().find { anim -> anim.name == it } }

                        animation?.let { swingAnimation(it) }
                    }
                    .build()
                return true
            }

            else -> {
                sendError(p, "You cannot modify '${args[0]}'!")
                return false
            }
        }
    }
}

