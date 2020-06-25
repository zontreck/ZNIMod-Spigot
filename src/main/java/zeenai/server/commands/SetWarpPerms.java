package zeenai.server.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import zeenai.server.commands.Warp.WarpFlags;

public class SetWarpPerms implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2,
            @NotNull String[] arg3)
    {
        if(arg3.length!=2){
            arg0.sendMessage(ChatColor.RED+"Syntax incorrect. Review usage:");
            return false;
        }
        // Do checks, then set permissions on warp
        WarpFlags newPerm = WarpFlags.OWNER_ONLY;

        FileConfiguration fc = ListWarps.GetConfig((Player)arg0);
        if(arg0.hasPermission("znimod.opCommands") || arg0.hasPermission("znimod.adminCommands") || arg0.getName().equals(fc.getString("warp."+arg3[0]+".owner"))){
            // adjust permissions

            switch(arg3[1]){
                case "0":
                {
                    newPerm = WarpFlags.OWNER_ONLY;
                    break;
                }
                case "1":
                {
                    newPerm = WarpFlags.NAME_KNOWN;
                    break;
                }
                case "2":
                {
                    newPerm = WarpFlags.FULL;
                    break;
                }
                case "owner":{
                    newPerm=WarpFlags.OWNER_ONLY;
                    break;
                }
                case "name":{
                    newPerm = WarpFlags.NAME_KNOWN;
                    break;
                }
                case "full":{
                    newPerm = WarpFlags.FULL;
                    break;
                }
                default :{
                    return false;
                }
            }

            fc.set("warp."+arg3[0]+".perms", newPerm.name());

            arg0.sendMessage(ChatColor.GREEN+"Permission set");
            ListWarps.SaveConfig((Player)arg0);
        } else {
            arg0.sendMessage(ChatColor.RED+"You lack permission");
        }

        return true;
    }
    
}