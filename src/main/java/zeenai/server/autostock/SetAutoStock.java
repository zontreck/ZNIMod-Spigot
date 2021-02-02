package zeenai.server.autostock;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import zeenai.server.NullConfig;
import zeenai.server.schematics.writer.Vector3;

public class SetAutoStock implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args)
    {
        Player play = (Player)sender;
        if(!play.hasPermission("znimod.opCommands"))return false;
        if(args.length == 0){
            play.sendMessage("Please insert the item stack you wish to be duplicated into the inventory that appears. The item will be deleted permanently from the game, and transfered to the automatic supply memory.");
            FileConfiguration fc = NullConfig.GetConfig(play);
            fc.set("autostock.inProgress",true);
            fc.set("autostock.stage",1);
            Inventory inv = Bukkit.createInventory(play, 9, "AutoStock Setup");
            play.openInventory(inv);
            // Player will be presented with a setup window, they will have to drag in the item stack. It will then be saved in TempConfig
        }
        return true; // not yet implemented
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent ice){
        ItemStack its = ice.getCurrentItem();
        Player p = (Player)ice.getWhoClicked();
        FileConfiguration fc = NullConfig.GetConfig(p);
        if(fc.getBoolean("autostock.inProgress")){
            int stage=fc.getInt("autostock.stage");
            if(stage==1){
                stage++;
                fc.set("autostock.stage",stage);
                fc.set("autostock.item", its);
                ice.setCancelled(true);
                ice.getWhoClicked().closeInventory();
                ice.getWhoClicked().sendMessage(ChatColor.RED+"Open the chest you want to associate now : Item("+its.getType()+")");
            }
        }
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent pie){
        if(pie.hasBlock()){
            FileConfiguration fc=NullConfig.GetConfig(pie.getPlayer());
            if(fc.getBoolean("autostock.inProgress")){
                if(fc.getInt("autostock.stage")==2){
                    // Associate the chest now if the block is a chest
                    if(pie.getClickedBlock().getType()==Material.CHEST){
                        Vector3 pos = new Vector3(pie.getClickedBlock().getLocation());
                        pos.LosePrecision();
                        pos.worldName = pie.getClickedBlock().getWorld().getName();

                        fc.set("autostock.inProgress",null);
                        fc.set("autostock.stage",null);
                        ItemStack itm = fc.getItemStack("autostock.item");
                        fc.set("autostock.item",null);

                        fc = StockConfig.GetConfig();
                        fc.set(pos.worldName+"."+(int)pos.x+"."+(int)pos.y+"."+(int)pos.z+".item", itm.getType().name());
                        fc.set(pos.worldName+"."+(int)pos.x+"."+(int)pos.y+"."+(int)pos.z+".amount", itm.getAmount());
                        StockConfig.SaveConfig();

                        pie.setCancelled(true);
                        pie.getPlayer().sendMessage("Chest registered with autostock");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBreakChest(BlockBreakEvent bbe){
        FileConfiguration sc = StockConfig.GetConfig();
        Vector3 pos = new Vector3(bbe.getBlock().getLocation());
        pos.LosePrecision();
        pos.worldName=bbe.getBlock().getLocation().getWorld().getName();
        if(sc.contains(pos.worldName+"."+(int)pos.x+"."+(int)pos.y+"."+(int)pos.z)){
            // disallow if not op
            Player p = bbe.getPlayer();
            if(p.hasPermission("znimod.opCommands")){
                // Remove from configuration
                
                sc.set(pos.worldName+"."+(int)pos.x+"."+(int)pos.y+"."+(int)pos.z, null);
                StockConfig.SaveConfig();
            }else{
                bbe.setCancelled(true);
                bbe.getPlayer().sendMessage(ChatColor.RED+"Action is denied because you do not have the permission: "+ChatColor.DARK_AQUA+"znimod.opCommands");
            }
        }
    }
    
}