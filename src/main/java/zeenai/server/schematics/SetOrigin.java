package zeenai.server.schematics;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import zeenai.server.NullConfig;
import zeenai.server.schematics.writer.Vector3;

public class SetOrigin implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2,
            @NotNull String[] arg3) 
    {
        if(!(arg0 instanceof Player)){
            Vector3 pos = new Vector3(Double.parseDouble(arg3[0]), Double.parseDouble(arg3[1]), Double.parseDouble(arg3[2]));

            NullConfig.GetTempConfig(arg0.getName()).set("Origin",pos);
            NullConfig.GetTempConfig(arg0.getName()).set("World", arg3[3]);

            arg0.sendMessage("Execution Success");
            return true;
        } else if(arg0 instanceof Player){

            Player p = (Player)arg0;

            Vector3 PlayerPosition = new Vector3(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
    
            // set origin to the vector specified
            NullConfig.GetTempConfig(arg0.getName()).set("Origin", PlayerPosition);
            arg0.sendMessage("Position is set to "+PlayerPosition.ToString());
            NullConfig.GetTempConfig(arg0.getName()).set("World", p.getLocation().getWorld().getName());

        }

        return true;
    }
    
}