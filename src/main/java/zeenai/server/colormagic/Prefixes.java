package zeenai.server.colormagic;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import zeenai.server.Main;

public class Prefixes implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args)
    {
        Player p = (Player)sender;
        for (Entry<String,ChatColor> displayNamePrefix : Main.GetMainInstance().displayNames.get(sender.getName()).internal.entrySet()) {
            //sender.sendMessage("Prefix: "+ChatColor.RED+"["+displayNamePrefix.getValue()+displayNamePrefix.getKey()+ChatColor.RED+"]");
            p.spigot().sendMessage(
                    new ComponentBuilder(
                    "Prefix: ")
                .append("[").color(net.md_5.bungee.api.ChatColor.RED)
                .append(displayNamePrefix.getKey()).color(net.md_5.bungee.api.ChatColor.valueOf(displayNamePrefix.getValue().name()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/applyprefix "+displayNamePrefix.getKey()))
                .append("]").color(net.md_5.bungee.api.ChatColor.RED)
                .create());
                
                
        }

        sender.sendMessage("Done listing prefixes available. Do not include brackets when setting prefix");
        return true;
    }
    
}