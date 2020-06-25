package zeenai.server.commands;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import zeenai.server.Main;

public class ListWarps implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args) {
        
        // List vaults for the player specified

        // Op not needed for this
        sender.sendMessage("=> Listing Warps");
        FileConfiguration fc = GetConfig((Player)sender);
        Set<String> WarpNames = fc.getConfigurationSection("warp").getKeys(false);
        for(String vName : WarpNames){
            if(sender.hasPermission("znimod.opCommands") || sender.hasPermission("znimod.adminCommands") || sender.getName().equals(GetConfig((Player)sender).getString("warp."+vName+".owner"))
                || GetConfig((Player)sender).getString("warp."+vName+".perms") == Warp.WarpFlags.FULL.name()
            ){

                sender.sendMessage(vName + "\n-> Owner: "+GetConfig((Player)sender).getString("warp."+vName+".owner")+"\n-> Permissions: "+GetConfig((Player)sender).getString("warp."+vName+".perms"));
            }
        }
        sender.sendMessage("=> Done listing warps");

        return true;
    }

    
    public static FileConfiguration GetConfig(Player play) {
        if(Main.GetMainInstance().CustomConfigs.containsKey("Warps")){
            return Main.GetMainInstance().CustomConfigs.get("Warps");
        }else {
            FileConfiguration fc = YamlConfiguration.loadConfiguration(new File(Main.GetMainInstance().getDataFolder(), "warps.yml"));
            Main.GetMainInstance().CustomConfigs.put("Warps", fc);

            return fc;
        }
    }

    public static void SaveConfig(Player play) {
        try {
            Main.GetMainInstance().CustomConfigs.get("Warps").save(new File(Main.GetMainInstance().getDataFolder(), "warps.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}