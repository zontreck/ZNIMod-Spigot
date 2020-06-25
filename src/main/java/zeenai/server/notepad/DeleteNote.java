package zeenai.server.notepad;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeleteNote implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args)
    {
        if(sender.hasPermission("znimod.notes")){
            if(args.length!=1){
                sender.sendMessage(ChatColor.RED+"You must include the note title to remove it");
                return false;
            }
            NotepadConfig.GetConfig((Player)sender).set(args[0],null);
            sender.sendMessage(ChatColor.GREEN+"Note '"+args[0]+"' has been deleted");
            return true;
        }else{
            sender.sendMessage(ChatColor.RED+"You lack permission to use notes");
        }

        return false;
    }
    
}