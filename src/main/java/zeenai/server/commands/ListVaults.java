package zeenai.server.commands;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import zeenai.server.Main;

public class ListVaults implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args) {
        
        String target = "";
        if(args.length != 1){
            // param is optional
            // 
            Player p = (Player)sender;
            target = p.getName();
        }else {
            if(!sender.hasPermission("znimod.adminCommands")){
                sender.sendMessage("To list another player's vaults you must have permission!");
                return true;
            }
            target = args[0];
        }
        // List vaults for the player specified

        // Op not needed for this
        sender.sendMessage("=> Listing Vaults");
        FileConfiguration fc = PlayerVault.GetConfig((Player)sender);
        Set<String> VaultNames = fc.getConfigurationSection(target+".vault").getKeys(false);
        for(String vName : VaultNames){
            sender.sendMessage(vName);
        }
        sender.sendMessage("=> Done listing vaults for "+target);

        return true;
    }
    
}