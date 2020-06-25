package zeenai.server.notepad;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Notes implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2,
            @NotNull String[] arg3)
    {
        if(arg0.hasPermission("znimod.notes")){
            // List all note entries
            Set<String> keys = NotepadConfig.GetConfig((Player)arg0).getKeys(false);
            for (String string : keys) {
                arg0.sendMessage("Entry: "+ChatColor.AQUA+string);
            }

            return true;
        } else {
            arg0.sendMessage(ChatColor.RED+"You lack permission");
            return false;
        }
    }
    
}