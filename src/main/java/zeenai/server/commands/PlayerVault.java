package zeenai.server.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.*;

import zeenai.server.Main;

public class PlayerVault implements CommandExecutor, Listener {

    public PlayerVault(){
        Main.GetMainInstance().getServer().getPluginManager().registerEvents(this, Main.GetMainInstance());
    }

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(args.length != 1) return false;
        // Requires the player vault ID. This can be a string or number. so it is treated as a string

        Player p = (Player)sender;

        FileConfiguration fc = GetConfig(p);
        Inventory vault = Bukkit.createInventory(p, 54, "ZNI Player Vault - "+args[0]);
        
        List<ItemStack> invStack = new ArrayList<ItemStack>();
        // Load the 27 Slots from disk
        if(fc.contains(p.getName()+".vault."+args[0]))
            invStack = (List<ItemStack>)fc.get(p.getName()+".vault."+args[0]);
        else
        {
            if(!p.hasPermission("znimod.unlimitedVC") && fc.getConfigurationSection(p.getName()+".vault").getKeys(false).size() >= 5){
                sender.sendMessage(ChatColor.RED+"You have reached the maximum virtual chests available to you. You require: VIP for unlimited");
                return true;
            }
            invStack = Arrays.asList();
        }
        int slot=0;
        
        sender.sendMessage("Opening vault '"+args[0]+"'");
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
        if(title.startsWith("ZNI Player Vault - ")){
            // This is for current player
            // Begin to serialize!
            List<ItemStack> lIS = new ArrayList<>();
            int i=0;
            int end=finalizedInventory.getSize();
            for(i=0;i<end;i++){
                lIS.add((finalizedInventory.getItem(i)));
            }

            String ident = title.substring("ZNI Player Vault - ".length(), title.length());
            FileConfiguration fc = GetConfig(p);
            fc.set(p.getName()+".vault."+ident, lIS);
            p.sendMessage("Writing vault '"+ident+"' to memory!");

            SaveConfig(p);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        
        Main.GetMainInstance().getServer().broadcastMessage(e.getPlayer().getName()+" has joined!");
    }
    
    
    public static FileConfiguration GetConfig(Player play) {
        if(Main.GetMainInstance().CustomConfigs.containsKey("Vault_"+play.getName())){
            return Main.GetMainInstance().CustomConfigs.get("Vault_"+play.getName());
        }else {
            FileConfiguration fc = YamlConfiguration.loadConfiguration(new File(Main.GetMainInstance().getDataFolder()+"/"+play.getName(), "vaults.yml"));
            Main.GetMainInstance().CustomConfigs.put("Vault_"+play.getName(), fc);

            return fc;
        }
    }

    public static void SaveConfig(Player play) {
        try {
            Main.GetMainInstance().CustomConfigs.get("Vault_"+play.getName()).save(new File(Main.GetMainInstance().getDataFolder()+"/"+play.getName(), "vaults.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}