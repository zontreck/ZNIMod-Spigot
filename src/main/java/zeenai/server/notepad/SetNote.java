package zeenai.server.notepad;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetNote implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2,
            @NotNull String[] arg3) 
    {
        Player p = (Player)arg0;
        if(p.hasPermission("znimod.notes")){
            // Check that the argument length is over 2
            if(arg3.length>=2){
                // Get note text
                String text = "";
                String arc = arg3[0];
                for (int i = 1; i < arg3.length; i++) {
                    text+=arg3[i]+" ";
                }
                if(text.endsWith(" "))text=text.substring(0, text.length()-1);

                NotepadConfig.GetConfig(p).set(arc,text);
                NotepadConfig.SaveConfig(p);
                
                p.sendMessage(ChatColor.GREEN+"Your note has been saved");

                return true;
            } else {
                // Throw arg count error
                p.sendMessage(ChatColor.RED+"Error: You did not provide enough arguments. See usage");
                return false;
            }
        } else {
            // User has no permission
            p.sendMessage(ChatColor.RED+"You lack permission");
            return true;
        }
    }
    
}