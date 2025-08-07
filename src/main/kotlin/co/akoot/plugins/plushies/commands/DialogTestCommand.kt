package co.akoot.plugins.plushies.commands
import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.conf
import co.akoot.plugins.plushies.Plushies.Companion.customDialogConfig
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.Recipes.getMaterial
import co.akoot.plugins.plushies.util.builders.DialogBuilder
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import io.papermc.paper.dialog.Dialog
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack

class DialogTestCommand(plugin: FoxPlugin) : FoxCommand(plugin, "dlt", "dialog builder test") {



    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return if (args.size == 1) customDialogConfig.getKeys().toMutableList() else mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender) ?: return false

        if (args.isEmpty()) {
            p.showDialog(DialogBuilder()
                .title(Kolor.WARNING("CRESTWEST"))
                .text(Text("I chugged a cotton candy alani and I am ").plus(Text("fent").obfuscated()).plus(Text(" folding the opposite direction rn, holy im so done for come 5 hours")))
                .icon(ItemBuilder.builder(ItemStack(Material.PLAYER_HEAD)).playerHead(p).build(), Text("this u?"))
                .runcmd(Kolor.MONTH("/lays"), Kolor.ERROR("THIS IS A TEST"), "lays")
                .icon(customItems["party_hat"], Kolor.QUOTE("Happy Birthday ${p.name}!"))
                .btn(Kolor.ERROR("resource pack"), clickEvent = ClickEvent.openUrl(conf.getString("pack.link") ?: " "))
                .icon(customItems["1"], Kolor.QUOTE("good plugin"))
                .icon(ItemStack(Material.STONE, 24), Text("THIS IS 24 STONE"))
                .icon(customItems["super_burrito"], Text("THIS IS BURRITO (HOPEFULLY)"))
                .build())
            return true
        }

        p.showDialog(createDialog(customDialogConfig, args[0])?: return sendError(p,"Dialog does not exist!"))
        return true
    }

    private fun createDialog(config: FoxConfig, path: String): Dialog? {
        if (!customDialogConfig.getKeys().toMutableList().contains(path)) return null
        return DialogBuilder().apply {
            config.getString("$path.title")?.let {(Text(it))}

            for (text in config.getStringList("$path.text")) {
                text(Text(text)) //lol
            }

            for (icon in config.getStringList("$path.icons")) {
                val parts = icon.split("/")
                val desc = if (parts.size > 1) Text(parts[1]) else null
                icon(getMaterial(parts[0]), desc)
            }

            for (button in config.getStringList("$path.links")) {
                val parts = button.split("|")
                btn(Text(parts[0]), Text(parts[1]), clickEvent = ClickEvent.openUrl(parts[1]))
            }

            for (button in config.getStringList("$path.commands")) {
                val parts = button.split("|")
                btn(Text(parts[0]), clickEvent = ClickEvent.runCommand(parts[1]))
            }
        }.build()
    }
}