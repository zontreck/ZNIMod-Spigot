package zeenai.server.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.*;

import zeenai.server.Main;

public class OtherPlayersVault implements CommandExecutor, Listener {

    public OtherPlayersVault(){
        Main.GetMainInstance().getServer().getPluginManager().registerEvents(this, Main.GetMainInstance());
    }

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(args.length != 2) return false;
        // Requires the player vault ID. This can be a string or number. so it is treated as a string

        Player p = (Player)sender;
        if(!p.hasPermission("znimod.adminCommands")){
            sender.sendMessage("You lack permissions");
            return false;
        }
        FileConfiguration fc = PlayerVault.GetConfig(p);
        Inventory vault = Bukkit.createInventory(p, 54, "ZNI Vault: "+args[0]+" - "+args[1]);
        
        List<ItemStack> invStack = null;
        // Load the 27 Slots from disk
        if(fc.contains(p.getName()+".vault."+args[0]))
            invStack = (List<ItemStack>)fc.get(args[0]+".vault."+args[1]);
        else
        {
            invStack = Arrays.asList();
        }
        int slot=0;
        
        sender.sendMessage("Opening vault '"+args[1]+"' from player '"+args[0]+"'");
        for(ItemStack is : invStack){
            if(is!=null)
                vault.setItem(slot, is);

            slot ++;
        }

        p.openInventory(vault);
        
        return true;
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e){
        Player p = (Player)e.getPlayer();
        Inventory finalizedInventory = e.getInventory();

        String title = e.getView().getTitle();
        if(title.startsWith("ZNI Vault: ")){

            String[] splitArgs = title.split("/(: )|( - )/g");
            // This is for current player
            // Begin to serialize!
            List<ItemStack> lIS = new ArrayList<>();
            int i=0;
            int end=finalizedInventory.getSize();
            for(i=0;i<end;i++){
                lIS.add((finalizedInventory.getItem(i)));
            }

            String ident = splitArgs[2];
            FileConfiguration fc = PlayerVault.GetConfig(p);
            fc.set(splitArgs[1]+".vault."+ident, lIS);
            p.sendMessage("Writing vault '"+ident+"' to memory of player '"+splitArgs[1]+"'!");

            PlayerVault.SaveConfig(p);
        }
    }
}