package zeenai.server.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import zeenai.server.Main;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHome implements CommandExecutor {
    public List<String> Aliases = Arrays.asList("sh");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label,
            String[] args)
    {
        if(args.length != 1) return false;
        // Set the home [0] to present location of command sender
        // Save this info into a simple config file under the ZNIMod config folder
        if(sender instanceof Player){

            Player play = (Player)sender;
            Main m = Main.GetMainInstance();
            
            m.getConfig().set(play.getName()+".playerHome."+args[0]+".position", play.getLocation());

            m.saveConfig();

            sender.sendMessage("Home '"+args[0]+"' has been saved!");
        }


        return true;
    }
    
}