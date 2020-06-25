package zeenai.server.autocraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import zeenai.server.Main;

public class ZAutoCraft implements Listener {
    private Logger log;
    @EventHandler
    public void beginSetup(PlayerInteractEvent ev) {
        if (ev.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (ev.isBlockInHand()) {
                // Player is placing block
                ItemStack filler = new ItemStack(Material.WRITTEN_BOOK, 1);
                ItemMeta IM = filler.getItemMeta();
                IM.setDisplayName("ILLEGAL POSITION");
                filler.setItemMeta(IM);

                if (ev.getItem().getItemMeta().getDisplayName().compareToIgnoreCase("[ZAutoCraft]") == 0) {
                    ev.getPlayer().sendMessage(ChatColor.GREEN + "Starting ZAutoCraft setup wizard for this block!");
                    Inventory iv = Bukkit.createInventory(null, 54, "[ZAutoCraft Setup]");

                    int[] pos = new int[] { 0, 1, 2, 9, 10, 11, 13, 18, 19, 20, 53 };
                    int end = 54;
                    List<Integer> asLists = new ArrayList<>(pos.length);
                    for (Integer inte : pos) {
                        asLists.add(inte);
                    }

                    for (int i = 0; i < end; i++) {
                        if (asLists.contains(i) == false)
                            iv.setItem(i, filler);
                    }

                    ItemStack confirmBtn = new ItemStack(Material.GREEN_WOOL, 1);
                    ItemMeta confBtn = confirmBtn.getItemMeta();
                    confBtn.setDisplayName("Click to confirm");
                    confirmBtn.setItemMeta(confBtn);

                    iv.setItem(53, confirmBtn);

                    ev.getPlayer().openInventory(iv);

                    GetConfig().set("pending." + ev.getPlayer().getName() + ".autocraft", true); // <-- This will be the
                                                                                                 // magic that lets
                                                                                                 // BlockPlaceEvent know
                                                                                                 // that autocraft is
                                                                                                 // waiting on a hopper
                                                                                                 // to be placed down
                    SaveConfig();
                }
            }
        }
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent ev) {
        if (ev.getBlock().getType() == Material.HOPPER) {
            // Hopper placed. Check for pending placements
            FileConfiguration fc = GetConfig();
            if (fc.contains("pending." + ev.getPlayer().getName() + ".autocraft")) {
                // Add the location
                fc.set("pending." + ev.getPlayer().getName() + ".location", ev.getBlock().getLocation());
                SaveConfig();
            }
        }
    }

    @EventHandler
    public void onSetupConclude(InventoryCloseEvent ev) {
        if (ev.getView().getTitle().compareToIgnoreCase("[ZAutoCraft Setup]") == 0) {
            // Get the craft result
            ev.getPlayer().sendMessage("Setup window has been closed");

            Player p = (Player) ev.getPlayer();

            FileConfiguration fc = GetConfig();
            if (fc.contains("pending." + p.getName() + ".autocraft")) {
                // Construct the permanent auto crafter data block
                Location L = fc.getLocation("pending." + p.getName() + ".location");
                String storePrefix = L.getWorld().getName() + "." + L.getX() + "." + L.getY() + "." + L.getZ();
                if (fc.contains("pending." + p.getName() + ".recipeShape")
                        && fc.contains("pending." + p.getName() + ".reward")) {

                    ItemStack[] RecipeData = (ItemStack[]) fc.get("pending." + p.getName() + ".recipeShape");
                    ItemStack Reward = (ItemStack) fc.get("pending." + p.getName() + ".reward");
                    fc.set(storePrefix + ".location", L);
                    fc.set(storePrefix + ".recipe", RecipeData);
                    fc.set(storePrefix + ".reward", Reward);

                    fc.set("pending." + p.getName(), null);
                    SaveConfig();

                    if (fc.contains("autocraft.symbols") == false) {

                        ItemStack zAutoCraftSymbols = new ItemStack(Material.WRITTEN_BOOK, 1);
                        BookMeta zAuto = (BookMeta) zAutoCraftSymbols.getItemMeta();
                        zAuto.setTitle("zAutoCraft");
                        zAuto.setAuthor("Umbrella Corporation");
                        zAuto.setPages(
                                "This book should not be viewed by any human. It is used as a symbol to indicate which inventory is the AutoCrafter's");
                        zAuto.addEnchant(Enchantment.CHANNELING, 619, true);
                        zAutoCraftSymbols.setItemMeta(zAuto);

                        fc.set("autocraft.symbols", zAutoCraftSymbols);
                        SaveConfig();
                    }
                    ItemStack CrafterSymbol = (ItemStack) fc.get("autocraft.symbols");
                    Block crafter = L.getWorld().getBlockAt(L);

                    BlockState bs = crafter.getState();

                    ItemStack LocationBook = new ItemStack(Material.WRITTEN_BOOK, 1);
                    BookMeta locbook = (BookMeta) LocationBook.getItemMeta();
                    locbook.setPages("" + L.getX(), "" + L.getY(), "" + L.getZ(), L.getWorld().getName());
                    locbook.setTitle("Crafter Location");
                    locbook.setAuthor("Franz Hopper");
                    LocationBook.setItemMeta(locbook);

                    if (bs instanceof Container) {
                        Container cont = (Container) bs;
                        Inventory cfg = cont.getInventory();
                        cfg.addItem(CrafterSymbol);
                        cfg.addItem(LocationBook);
                    }

                    ev.getPlayer().sendMessage("["+ChatColor.DARK_RED+"zAutoCraft"+ChatColor.WHITE+"] "+ChatColor.DARK_PURPLE+"ALERT: "+ChatColor.GOLD+"zAutoCraft is currently in alpha testing. Craft functionality is not implemented yet!");
                } else {

                    fc.set("pending." + p.getName(), null);
                    fc.set(storePrefix, null);
                    SaveConfig();

                    // Destroy the block to prevent errors
                    Block B = L.getWorld().getBlockAt(L);
                    B.breakNaturally();
                }

            }
        } else {
            FileConfiguration fcx = GetConfig();
            if (fcx.contains("pending." + ev.getPlayer().getName())) {
                fcx.set("pending." + ev.getPlayer().getName(), null);
                SaveConfig();
            }
        }
    }

    @EventHandler
    public void onBreakCrafter(BlockBreakEvent ev) {
        // Remove from configuration if matches
        FileConfiguration fc = GetConfig();
        Location L = ev.getBlock().getLocation();
        String storePrefix = L.getWorld().getName() + "." + L.getX() + "." + L.getY() + "." + L.getZ();
        if (fc.contains(storePrefix)) {
            fc.set(storePrefix, null); // <-- To set up the auto crafter no materials were truly used, so we can safely
                                       // destroy the stored data
            ev.getPlayer().sendMessage(ChatColor.GREEN + "AutoCraft Hopper destroyed successfully");
            SaveConfig();
        }
    }
    private int steps=0;
    private void debugf(boolean enter, String method, String...parameters){
        if(!enter)steps--;
        if(steps<0)steps=0;

        // Build string
        
        String strBuild="";
        for (int i = 0; i < steps; i++) {
            strBuild+="\t| ";
        }
        if(enter)strBuild+="ENTER ";
        else strBuild+="LEAVE ";
        strBuild+=method+" [";
        for (String string : parameters) {
            strBuild+= string+", ";
        }
        strBuild+="]";

        log.info(strBuild);

        if(enter)steps++;
    }

    private void debug(String logMessage){
        String strBuild="";
        for (int i = 0; i < steps; i++) {
            strBuild+="\t| ";
        }
        log.info(strBuild+logMessage);
    }


    @EventHandler
    public void doSetupView(InventoryClickEvent e){
        if(e.getView().getTitle().compareToIgnoreCase("[ZAutoCraft Setup]")==0){
            //e.getWhoClicked().sendMessage("Click position: "+e.getRawSlot());

            if(e.getRawSlot() < e.getView().getTopInventory().getSize()){
                // Top inventory clicked

                // Check slots

                ItemStack _ResultStack = e.getView().getTopInventory().getItem(13);
                if(_ResultStack != null){
                    
                    e.getWhoClicked().sendMessage("OK: End Result -> "+_ResultStack.getType().name()+"; "+_ResultStack.getAmount());
                }

            }
            if(e.getClickedInventory().getItem(e.getSlot())!=null){

                if(e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().compareToIgnoreCase("ILLEGAL POSITION")==0){
                    e.setCancelled(true);
                } else if(e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().compareToIgnoreCase("Click to confirm")==0){

                    e.setCancelled(true);

                    // Begin to process the crafting recipe and stuff
                    ItemStack[] RecipeShape = new ItemStack[9];

                    Inventory topInv = e.getView().getTopInventory();
                    
                    RecipeShape[0] = topInv.getContents()[0];
                    RecipeShape[1] = topInv.getContents()[1];
                    RecipeShape[2] = topInv.getContents()[2];

                    
                    RecipeShape[3] = topInv.getContents()[9];
                    RecipeShape[4] = topInv.getContents()[10];
                    RecipeShape[5] = topInv.getContents()[11];

                    
                    RecipeShape[6] = topInv.getContents()[18];
                    RecipeShape[7] = topInv.getContents()[19];
                    RecipeShape[8] = topInv.getContents()[20];

                    FileConfiguration fc = GetConfig();

                    fc.set("pending."+e.getWhoClicked().getName()+".recipeShape", RecipeShape);
                    

                    ItemStack RewardItem = topInv.getContents()[13];
                    fc.set("pending."+e.getWhoClicked().getName()+".reward", RewardItem);

                    SaveConfig();

                    // Return the ingredients used to set up this crafting hopper
                    int[] Boxs = new int[] {0,1,2,9,10,11,18,19,20,13};
                    for(Integer inx : Boxs){
                        if(e.getView().getTopInventory().getItem(inx)!=null)
                            e.getView().getBottomInventory().addItem(e.getView().getTopInventory().getItem(inx));
                    }
                    
                    e.getWhoClicked().closeInventory();

                    Player p = (Player)e.getWhoClicked();
                    p.updateInventory();
                }
            }

            
        }
    }
    

    public static FileConfiguration GetConfig() {
        if(Main.GetMainInstance().CustomConfigs.containsKey("AutoCraft")){
            return Main.GetMainInstance().CustomConfigs.get("AutoCraft");
        }else {
            FileConfiguration fc = YamlConfiguration.loadConfiguration(new File(Main.GetMainInstance().getDataFolder(), "autocraft.yml"));
            Main.GetMainInstance().CustomConfigs.put("AutoCraft", fc);

            return fc;
        }
    }

    public static void SaveConfig() {
        try {
            Main.GetMainInstance().CustomConfigs.get("AutoCraft").save(new File(Main.GetMainInstance().getDataFolder(), "autocraft.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}