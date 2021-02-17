package zeenai.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;


import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import zeenai.server.antigrief.*;
import zeenai.server.chunkforceloader.*;
import zeenai.server.colormagic.*;
import zeenai.server.commands.*;
import zeenai.server.currency.*;
import zeenai.server.entitycodec.RestoreEntity;
import zeenai.server.generators.ZniGenerator;
import zeenai.server.notepad.DeleteNote;
import zeenai.server.notepad.GetNote;
import zeenai.server.notepad.Notes;
import zeenai.server.notepad.SetNote;
import zeenai.server.schematics.FillAir;
import zeenai.server.schematics.GetPos;
import zeenai.server.schematics.LoadSchematic;
import zeenai.server.schematics.POS1;
import zeenai.server.schematics.POS2;
import zeenai.server.schematics.PositionDebug;
import zeenai.server.schematics.SetOrigin;
import zeenai.server.schematics.Unimport;
import zeenai.server.schematics.WriteSchematic;
import zeenai.server.schematics.writer.Vector3;
import zeenai.server.treechops.RestoreBlock;
import zeenai.server.treechops.TreeFeller;
import zeenai.server.treechops.Undo;
import zeenai.server.treechops.ViewRadius;
import zeenai.server.autocraft.ZAutoCraft;
import zeenai.server.autostock.SetAutoStock;
import zeenai.server.autostock.StockConfig;
import zeenai.server.biomewatchdog.*;

public class Main extends JavaPlugin {
    private SetHome SH = new SetHome();
    private GoHome GH = new GoHome();
    private RemoveHome RH = new RemoveHome();
    private SetSpawn SS = new SetSpawn();
    private Spawn _spawn = new Spawn();
    private PlayerVault _playerVault;
    private OtherPlayersVault _otherPlayersVaults;
    private GoToHeight gth = new GoToHeight();
    private RemoveVault rmv = new RemoveVault();
    private NukeVault nukes = new NukeVault();
    private ListVaults lv = new ListVaults();
    private ListHomes LH = new ListHomes();
    private BackToGrave _back;
    private AlwaysXP axp = new AlwaysXP();
    private XP _xp = new XP();
    private GameModes _gamemodes = new GameModes();
    private PlayerVaultStackPush stackpush = new PlayerVaultStackPush();
    private PlayerVaultStackPop stackpop = new PlayerVaultStackPop();
    private SetWarp _setwarp = new SetWarp();
    private Warp _warp = new Warp();
    private ListWarps _lswarps = new ListWarps();
    private DelWarp _delwarp = new DelWarp();
    private Inventory colorInv;
    private PrefixColor _prefixColor;
    private ColorCmd _colorsel;
    private ColorName _cname;
    private ZNIReload _reloader = new ZNIReload();
    private Trash _trash = new Trash();
    private GetChunkID _GetChunkID = new GetChunkID();
    private ListForceLoaded _ListForceLoaded = new ListForceLoaded();
    private FLChunk _FlChunk = new FLChunk();
    private OpenPlayerInv _OpenPlayerInv = new OpenPlayerInv();
    public Map<String, FileConfiguration> CustomConfigs = new HashMap<>();
    public Map<String, Biome> BiomesMap = new HashMap<>();
    public Map<String, Scoreboard> boards = new HashMap<String, Scoreboard>();

    private FillAir _fillair = new FillAir();


    public List<Location> stateRequests = new ArrayList<Location>();
    public Map<Location, BlockState> states = new HashMap<Location, BlockState>();
    public List<RestoreBlock> forceQueue = new ArrayList<RestoreBlock>();
    public Map<Location, List<ItemStack>> containerRestore = new HashMap<>();


    public class DisplayNameRegistry{
        public Map<String,ChatColor> internal = new HashMap<>();
    }
    public Map<String, DisplayNameRegistry> displayNames = new HashMap<String, DisplayNameRegistry>();



    private static Main inst = null;

    public static Main GetMainInstance() {
        return inst;
    }

    public static void SetMainInstance(Main instance){
        inst=instance;
    }


    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(RestoreBlock.class);
        ConfigurationSerialization.registerClass(RestoreEntity.class);
        ConfigurationSerialization.registerClass(Vector3.class);
        Main.SetMainInstance(this);
        getLogger().info("Hello spigot ZNIMod has arrived!");
        saveDefaultConfig();




        _playerVault = new PlayerVault();
        _otherPlayersVaults = new OtherPlayersVault();
        _back = new BackToGrave();
        setupInventory();
        _prefixColor = new PrefixColor(colorInv);
        _colorsel = new ColorCmd(colorInv);
        _cname = new ColorName(colorInv);

        getCommand("sethome").setExecutor(SH);
        getCommand("home").setExecutor(GH);
        getCommand("removehome").setExecutor(RH);
        getCommand("setspawn").setExecutor(SS);
        getCommand("respawn").setExecutor(_spawn);
        getCommand("playervault").setExecutor(_playerVault);
        getCommand("oplayervault").setExecutor(_otherPlayersVaults);
        getCommand("gotoheight").setExecutor(gth);
        getCommand("rmvault").setExecutor(rmv);
        getCommand("nukevault").setExecutor(nukes);
        getCommand("homes").setExecutor(LH);
        getCommand("vaults").setExecutor(lv);
        getCommand("back").setExecutor(_back);
        getCommand("alwaysxp").setExecutor(axp);
        getCommand("xp").setExecutor(_xp);
        getCommand("gamemode").setExecutor(_gamemodes);
        getCommand("pushinv").setExecutor(stackpush);
        getCommand("popinv").setExecutor(stackpop);
        getCommand("setwarp").setExecutor(_setwarp);
        getCommand("warp").setExecutor(_warp);
        getCommand("warps").setExecutor(_lswarps);
        getCommand("delwarp").setExecutor(_delwarp);
        getCommand("prefixcolor").setExecutor(_prefixColor);
        getCommand("color").setExecutor(_colorsel);
        getCommand("prefix").setExecutor(new Prefix());
        getCommand("namecolor").setExecutor(_cname);
        getCommand("znireload").setExecutor(_reloader);
        getCommand("trash").setExecutor(_trash);
        getCommand("getChunk").setExecutor(_GetChunkID);
        getCommand("listfl").setExecutor(_ListForceLoaded);
        getCommand("flchunk").setExecutor(_FlChunk);
        getCommand("viewinv").setExecutor(_OpenPlayerInv);
        getCommand("cost").setExecutor(new CheckCostOfBlock());
        getCommand("setcost").setExecutor(new SetCostForBlock());
        getCommand("buylevel").setExecutor(new BuyXPLevel());
        getCommand("ignorebiome").setExecutor(new IgnoreBiomes());
        getCommand("watchbiome").setExecutor(new WatchBiomes());
        getCommand("slime").setExecutor(new GetSlimeChunk());
        getCommand("withdraw").setExecutor(new Withdraw());
        getCommand("webtoken").setExecutor(new WebToken());
        getCommand("fly").setExecutor(new Fly());
        getCommand("autochop").setExecutor(new TreeFeller());
        getCommand("undochop").setExecutor(new Undo());
        getCommand("viewradius").setExecutor(new ViewRadius());
        getCommand("checkqueue").setExecutor(new CheckQueue());
        getCommand("showhealstats").setExecutor(new HealStats());
        getCommand("p2").setExecutor(new StartPhase2());
        getCommand("heal").setExecutor(new FinishHeal());
        getCommand("clearradius").setExecutor(new ClearRadius());
        getCommand("restorebackup").setExecutor(new RestoreBackup());
        getCommand("clearbackup").setExecutor(new ClearBackup());
        getCommand("zpos1").setExecutor(new POS1());
        getCommand("zpos2").setExecutor(new POS2());
        getCommand("posdebug").setExecutor(new PositionDebug());
        getCommand("saveschem").setExecutor(new WriteSchematic());
        getCommand("loadschem3").setExecutor(new LoadSchematic());
        getCommand("clearqueue").setExecutor(new ClearQueue());
        getCommand("setorigin").setExecutor(new SetOrigin());
        getCommand("getpos").setExecutor(new GetPos());
        getCommand("unimport").setExecutor(new Unimport());
        getCommand("forceupdate").setExecutor(new ForceBlockUpdate());
        getCommand("setwarpperms").setExecutor(new SetWarpPerms());
        getCommand("note").setExecutor(new GetNote());
        getCommand("notes").setExecutor(new Notes());
        getCommand("takenote").setExecutor(new SetNote());
        getCommand("prefixes").setExecutor(new Prefixes());
        getCommand("delnote").setExecutor(new DeleteNote());
        getCommand("applyprefix").setExecutor(new ApplyPrefix());
        getCommand("repair").setExecutor(new Repair());
        getCommand("setautostock").setExecutor(new SetAutoStock());
        getCommand("fillair").setExecutor(_fillair);
        getCommand("disablenetherheal").setExecutor(new DisableNetherHeal());
        getCommand("enablenetherheal").setExecutor(new EnableNetherHeal());



        _FlChunk.ScanAndLoad(true);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, Healer.GetInstance(), 0L, 1L);
        getServer().getPluginManager().registerEvents(new Colors(colorInv), this);
        getServer().getPluginManager().registerEvents(new BlockIgniteListener(), this);
        // this);
        getServer().getPluginManager().registerEvents(_FlChunk, this);
        getServer().getPluginManager().registerEvents(new PlayerCurrencyBoard(), this);
        getServer().getPluginManager().registerEvents(new TreeFeller(), this);
        getServer().getPluginManager().registerEvents(new CheckForNewBiome(), this);
        getServer().getPluginManager().registerEvents(new ZAutoCraft(), this);
        getServer().getPluginManager().registerEvents(new EnforceGameMode(), this);

        getServer().getPluginManager().registerEvents(new SetAutoStock(), this);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
            @Override
            public void run(){
                // 
                if(Main.GetMainInstance().stateRequests.size()==0)return;
                Iterator<Location> iter = Main.GetMainInstance().stateRequests.iterator();
                while(iter.hasNext()){
                    try {

                        Location _Location = iter.next();
                        Main.GetMainInstance().states.put(_Location, _Location.getBlock().getState());
                        Main.GetMainInstance().stateRequests.remove(_Location);
    
                        iter.remove();
                    }catch(NoSuchElementException e){
                        break;
                    } catch(ConcurrentModificationException e){
                        break;
                    }
                }
            }
        }, 0L, 150L);


        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

            @Override
            public void run() {
                // retrieve the StockConfig
                FileConfiguration fc = StockConfig.GetConfig();
                Map<String, Object> elements = fc.getValues(false);
                for (String eString : elements.keySet()) {
                    // now parse X in each world
                    //getLogger().info("On Sect: "+eString);
                    ConfigurationSection sect = fc.getConfigurationSection(eString);
                    for (String xString : sect.getKeys(false)) {
                        // now parse Y
                        //getLogger().info("On Sect: "+eString+"."+xString);
                        ConfigurationSection sectY = sect.getConfigurationSection(xString);
                        for (String yString : sectY.getKeys(false)) {
                            // now parse Z
                            //getLogger().info("On Sect: "+eString+"."+xString+"."+yString);
                            ConfigurationSection sectZ = sectY.getConfigurationSection(yString);
                            // we now have the ability to retrieve the values for the autostock
                            // foreach Z is a autostock
                            for (String zString : sectZ.getKeys(false)) {
                                // This is a autostock chest!
                                // Read the item and refill the chest completely now
                                //getLogger().info("On Sect: "+eString+"."+xString+"."+yString+"."+zString);

                                Vector3 vec3 = new Vector3(Double.parseDouble(xString), Double.parseDouble(yString), Double.parseDouble(zString));
                                vec3.worldName=eString;
                                Location bkLoc = vec3.GetBukkitLocation(Main.GetMainInstance().getServer().getWorld(eString));
                                Block b = bkLoc.getBlock();

                                ItemStack itmx = fc.getItemStack(eString+"."+xString+"."+yString+"."+zString+".item");
                                ItemStack itm = new ItemStack(itmx);
                                if(b.getState() instanceof Chest){
                                    
                                    Chest con = (Chest)b.getState();
                                    ItemStack[] inv = con.getInventory().getContents();
                                    //getLogger().info("Slot count: "+inv.length);
                                    int count=0;
                                    if(inv==null){
                                        //getLogger().info("INV is null");
                                        break;
                                    }
                                    for (ItemStack itemStack : inv) {
                                        if(itemStack==null)continue;
                                        if(itemStack.getType()==itm.getType()){
                                            //getLogger().info("COMPARE: "+itemStack.getType());
                                            count++;
                                        }else{
                                            if(itemStack.getType()!=Material.AIR){
                                                con.getInventory().remove(itemStack);
                                            }
                                        }
                                    }
                                    for(int i=count;i<con.getInventory().getSize();i++){
                                        itm = new ItemStack(itmx);
                                        con.getInventory().addItem(itm);
                                    }

                                } else {
                                    getLogger().info(("Unknown block! "+vec3.ToString()));
                                }
                                
                            }
                        }
                    }
                }

                if(containerRestore.size()>0){
                    // Iterate over the map, then restore the inventory onto the block, then remove the inventory from restore queue
                    for (Map.Entry<Location, List<ItemStack>> ix: containerRestore.entrySet()
                         ) {
                        // restore!
                        Container cnt = (Container) ix.getKey().getBlock();
                        //cnt.getInventory().setContents(ix.getValue());
                        List<ItemStack> stacks = ix.getValue();
                        for (ItemStack isx: stacks
                             ) {
                            cnt.getInventory().addItem(new ItemStack(isx));
                        }
                        cnt.update();
                    }
                    containerRestore.clear();
                }

            }

        }, 0L, 1200L);


        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
        
            @Override
            public void run() {
                
                if(Main.GetMainInstance().forceQueue.size()==0)return;
                Iterator<RestoreBlock> iter = Main.GetMainInstance().forceQueue.iterator();
                while(iter.hasNext()){
                    RestoreBlock rBlock = iter.next();
                    rBlock.ApplyState();

                    iter.remove();
                }
            }
        }, 0L, 100L);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            @Override
            public void run() {
                for (Player play : getServer().getOnlinePlayers()) {
                    ScoreboardManager sm  =  Bukkit.getScoreboardManager();
                    Scoreboard board=null;
                    if(Main.GetMainInstance().boards.containsKey(play.getName())) board=Main.GetMainInstance().boards.get(play.getName());
                    else board = sm.getNewScoreboard();

                    if(!Main.GetMainInstance().boards.containsKey(play.getName())) Main.GetMainInstance().boards.put(play.getName(), board);

                    PlayerCurrencyBoard.GenBoard(board,play);
                    if(PlayerConfig.GetConfig(play).getBoolean("showHealStats"))
                        Healer.GetInstance().GenStats(board,play);

                    Objective ZNI = board.getObjective("zni");
                    ZNI.setDisplayName("ZNI Stats");
                }

            }

        }, 0L, 50L);



        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            @Override
            public void run() {
                for (String entry : Main.GetMainInstance().getDataFolder().list()) {
                    if(entry.endsWith("nukeAuthorization")){
                        Main.GetMainInstance().getLogger().info("Nuke auth found: "+entry);
                        FileConfiguration FC = null;
                        class UserObject{
                            public String username;
                            public UUID uuid;
                        }
                        UserObject usrObj = null;
                        try (Reader read = new FileReader(new File(Main.GetMainInstance().getDataFolder().getPath()+"/"+entry))){
                            // deserialize the json
                            FC = YamlConfiguration.loadConfiguration(read);
                            usrObj = new UserObject();
                            usrObj.username = FC.getString("username");

                            Main.GetMainInstance().getLogger().info("USROBJ is set");
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        if(usrObj == null){
                            // silent fail
                            File f = new File(Main.GetMainInstance().getDataFolder().getPath()+"/"+entry);
                            f.delete();
                            continue;
                        }else{

                            Bukkit.broadcastMessage(ChatColor.YELLOW+usrObj.username+" has been reset");
                            // Delete the player data from ZNI first.
                            Main.GetMainInstance().getConfig().set(usrObj.username,null);
                            
                            try {
                                deleteDirectoryRecursion(
                                        Paths.get(Main.GetMainInstance().getDataFolder().getPath(), usrObj.username));
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            // Reload ZNI configuration
                            Main.GetMainInstance().saveConfig();
                            Main.GetMainInstance().CustomConfigs.clear();
                            Main.GetMainInstance().BiomesMap.clear();
                            
                            Player pl = Bukkit.getPlayer(usrObj.username);
                            
                            Main.GetMainInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke "+usrObj.username+" everything");
                            pl.getInventory().clear();
                            pl.getEnderChest().clear();
                            pl.setTotalExperience(0);
                            pl.closeInventory();
                            pl.updateInventory();
                            
                            pl.sendMessage("Your account has been reset. If you wish to remove all your player data however, send a email to support@zontreck.dev ! Have a nice day");
                            
                            File f = new File(Main.GetMainInstance().getDataFolder().getPath()+"/"+entry);
                            f.delete();
                        }
                    }
                }

            }

        }, 0L, 1000L);
        
    }
    void deleteDirectoryRecursion(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        Files.delete(path);
    }



    @Override
    public void onDisable(){
        getLogger().info("ZNImod is shutting down");
    }


    //@Override
    //public ChunkGenerator getDefaultWorldGenerator(String worldName, String id){
    //    getLogger().info("ID : "+id);
    //    return new ZniGenerator(id);
    //}

	private void setupInventory() {
        colorInv = Bukkit.createInventory(null, 27, "Chat Color Menu");

		colorInv.setItem(1, createItem(14, "§cRed", "§f§l&c"));
		colorInv.setItem(2, createItem(1, "§6Gold", "§f§l&6"));
		colorInv.setItem(3, createItem(4, "§eYellow", "§f§l&e"));
		colorInv.setItem(4, createItem(13, "§2Green", "§f§l&2"));
		colorInv.setItem(5, createItem(5, "§aLime", "§f§l&a"));
		colorInv.setItem(6, createItem(3, "§bCyan", "§f§l&b"));
		colorInv.setItem(7, createItem(9, "§3Aqua", "§f§l&3"));

		colorInv.setItem(10, createItem(11, "§9Blue", "§f§l&9"));
		colorInv.setItem(11, createItem(6, "§5Purple", "§f§l&5"));
		colorInv.setItem(12, createItem(10, "§dPink", "§f§l&d"));

		colorInv.setItem(14, createItem(0, "§fWhite", "§f§l&f"));
		colorInv.setItem(15, createItem(8, "§7Light-Gray", "§f§l&7"));
        colorInv.setItem(16, createItem(7, "§8Gray", "§f§l&8"));
        
        colorInv.setItem(17, createItem(0, ChatColor.ITALIC+"Italic", ""));
        colorInv.setItem(18, createItem(0, ChatColor.MAGIC+"Magic", ""));
        colorInv.setItem(19, createItem(0, ChatColor.STRIKETHROUGH+"Strikethrough", ""));

        getLogger().info("Finished creating color inventory.");
    }


    private ItemStack createItem(int id, String name, String lore) {
        
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK, 1);
        
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(new String[] { lore }));
		item.setItemMeta(meta);
		return item;
    }
    

    public Biome PlayerLastBiome(String player){
        return BiomesMap.get(player);
    }

    public void SetPlayerLastBiome(String player, Biome b){
        if(BiomesMap.containsKey(player))BiomesMap.remove(player);

        BiomesMap.put(player,b);
    }





    
}
