package zeenai.server.antigrief;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import net.md_5.bungee.api.ChatColor;
import zeenai.server.PlayerConfig;

public class HealStats implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        PlayerConfig.GetConfig((Player)sender).set("showHealStats", !PlayerConfig.GetConfig((Player)sender).getBoolean("showHealStats"));
        PlayerConfig.SaveConfig((Player)sender);

        sender.sendMessage("Heal status visibility is set to: "+ChatColor.GREEN+PlayerConfig.GetConfig((Player)sender).getBoolean("showHealStats"));


        Scoreboard board = ((Player)sender).getScoreboard();
        board.clearSlot(DisplaySlot.SIDEBAR);

        return true;
    }
    
}