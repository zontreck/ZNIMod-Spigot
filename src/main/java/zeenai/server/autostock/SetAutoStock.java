package zeenai.server.autostock;

import java.util.List;

import com.sk89q.jchronic.utils.Time;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SetAutoStock implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args)
    {
        Player play = (Player)sender;
        if(!play.hasPermission("znimod.opCommands"))return false;
        if(args.length == 4){
            // arg 0 = number of stock box
            // arg 1 = Material name of what to stock automatically
            // arg 2 = Amount to automatically fill, -1 for maximum in all available slots of whatever is sourcing
            // arg 3 = Time between refills in minutes (2 minutes minimum)
            String box = args[0];
            FileConfiguration fc = StockConfig.GetConfig(play);
            if(StockConfig.GetConfig(play).contains(box)){
                
                // Get the information about this box

                // Box properties
                // .LastRefill - Defaults to null
                // .Item - Maps to ItemStack(arg1,(arg2<=maxcount) ? maxcount : arg2)
                // .TimeBetween - Maps to arg3
                long lastRefill = fc.getLong(box+".LastRefill");
                ItemStack item = fc.getItemStack(box+".Item");
                int TimeBetween = fc.getInt(box+".TimeBetween");


                sender.sendMessage("Updating stock ("+box+") from "+item.getType().name()+" to "+args[1]);
                item = new ItemStack(Material.valueOf(args[1]), Integer.parseInt(args[2]));
                lastRefill=0;
                TimeBetween = Integer.parseInt(args[3]);

                fc.set(box+".LastRefill", lastRefill);
                fc.set(box+".Item", item);
                fc.set(box+".TimeBetween", TimeBetween);
            } else {
                fc.set(box+".LastRefill", Long.valueOf(0));
                fc.set(box+".Item", new ItemStack(Material.valueOf(args[1]), Integer.parseInt(args[2])));
                fc.set(box+".TimeBetween", Integer.parseInt(args[3]));
            }

            StockConfig.SaveConfig(play);
        }
        return true; // not yet implemented
    }
    
}