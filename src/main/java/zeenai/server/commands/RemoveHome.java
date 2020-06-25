package zeenai.server.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import zeenai.server.Main;

public class RemoveHome implements CommandExecutor {
    public List<String> Aliases = Arrays.asList("rmhome", "rmh", "rh");


    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(args.length != 1)return false;

        if(sender instanceof Player){
            Player play = (Player)sender;
            // Delete home
            sender.sendMessage("Attempting to delete that home");

            FileConfiguration fc = Main.GetMainInstance().getConfig();

            if(fc.contains(play.getName()+".playerHome."+args[0]+".position")){
                // delete entries
                fc.set(play.getName()+".playerHome."+args[0]+".position",null);
                fc.set(play.getName()+".playerHome."+args[0],null);
                Main.GetMainInstance().saveConfig();
            }


        }

        return true;
    }
    
}