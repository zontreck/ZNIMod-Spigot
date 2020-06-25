package zeenai.server.currency;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import zeenai.server.Main;

public class BuyXPLevel implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        // Begin to perform maths
        double XPCost = 15.00;
        Player p = (Player)sender;
        int BuyLvlTotal = Integer.parseInt(args[0]);

        FileConfiguration fc = PlayerCurrencyBoard.GetConfig(p);
        double BALANCE = Main.GetMainInstance().econ.getBalance(p);

        if(BALANCE<XPCost){
            p.sendMessage("The cost for 1 XP point is 15 Z$. Go mining or sell some blocks!");
            return false;
        }

        int TotalPointsPurchased=0;
        for(int i=0;i<BuyLvlTotal;i++){
            int x=0;
            int ends = p.getExpToLevel();
            // For each xp level to add, subtract from balance, then grant the XP point
            for(x=0;x<ends;x++){
                if(BALANCE>XPCost){
                    TotalPointsPurchased++;
                    BALANCE-=XPCost;
                    p.giveExp(1);
                } else {
                    p.sendMessage("You've run out of Z$. Cost per XP Point is 15 Z$");
                }
            }            
            
        }
        PlayerCurrencyBoard.SaveConfig(p);

        p.sendMessage("Purchased : "+TotalPointsPurchased +" XP Points for a grand total of : "+(TotalPointsPurchased * XPCost)+" Z$");

        return true;
    }
    
}