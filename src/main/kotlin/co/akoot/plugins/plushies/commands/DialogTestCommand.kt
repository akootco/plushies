package co.akoot.plugins.plushies.commands
import co.akoot.plugins.bluefox.api.FoxCommand
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.api.Kolor
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.plushies.Plushies.Companion.conf
import co.akoot.plugins.plushies.util.Items.customItems
import co.akoot.plugins.plushies.util.builders.DialogBuilder
import co.akoot.plugins.plushies.util.builders.ItemBuilder
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack

class DialogTestCommand(plugin: FoxPlugin) : FoxCommand(plugin, "dlt", "dialog builder test") {

    override fun onTabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
        val p = playerCheck(sender)?: return false
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
}