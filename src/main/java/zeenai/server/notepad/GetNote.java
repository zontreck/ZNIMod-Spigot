package zeenai.server.notepad;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GetNote implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2,
            @NotNull String[] arg3) 
    {
        Player p = (Player)arg0;
        if(p.hasPermission("znimod.notes")){
            FileConfiguration fc = NotepadConfig.GetConfig(p);
            if(arg3.length!=1){
                p.sendMessage("Wrong length of arguments");
                return false;
            } else {
                // Retrieve note data
                if(fc.contains(arg3[0])){
                    p.sendMessage(ChatColor.LIGHT_PURPLE+arg3[0]+ChatColor.AQUA+": "+fc.getString(arg3[0]));
                }else{
                    p.sendMessage(ChatColor.RED+arg3[0]+ChatColor.BOLD+": Does not exist");
                }

                return true;
            }
        }else {
            p.sendMessage("You lack permission");
            return true;
        }
    }
    
}