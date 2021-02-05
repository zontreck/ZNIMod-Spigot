package zeenai.server.antigrief;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EnableNetherHeal implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        // Toggle the AntiGriefing nether healer
        FileConfiguration fc = AntiGrief.GetConfig();
        fc.set("antigrief.healNetherExplode", true);
        Player p = (Player)sender;
        fc.set("antigrief.netherName", p.getLocation().getWorld().getName());
        if(sender.hasPermission("znimod.opCommands")) {
            AntiGrief.SaveConfig();
            sender.sendMessage("Disabling..");
        }
        else {
            sender.sendMessage("Op Only Command");
            return false;
        }
        return true;
    }
}
