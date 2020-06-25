package zeenai.server.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import zeenai.server.Main;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;


public class GoHome implements CommandExecutor {
    public List<String> Aliases = Arrays.asList("h");

    @Override
    public boolean onCommand(CommandSender sender,Command command,  String label,
             String[] args)
    {
        if(args.length != 1) return false;
        if(sender instanceof Player){
            // h | home
            // arg0 will always be the home name
            // teleport the player!

            Player play = (Player)sender;
            sender.sendMessage("Teleporting to '"+args[0]+"'...");

            Main m = Main.GetMainInstance();
            Location L = new Location(null, 0, 0, 0);
            FileConfiguration FC = m.getConfig();

            if(!FC.contains(play.getName()+".playerHome."+args[0]+".position")){
                sender.sendMessage("No such home");
                return true;
            }
            L = FC.getLocation(play.getName()+".playerHome."+args[0]+".position");
            play.teleport(L);

            sender.sendMessage("Teleportation Completed!");
        }

        return true;
    }
    
}