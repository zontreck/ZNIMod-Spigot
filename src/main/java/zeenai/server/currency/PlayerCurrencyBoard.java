package zeenai.server.currency;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.event.*;
import org.bukkit.*;

import zeenai.server.Main;

public class PlayerCurrencyBoard implements Listener {
    public static void GenBoard(Scoreboard board, Player p) {
        //ScoreboardManager mgr = Bukkit.getScoreboardManager();
        //Scoreboard board = mgr.getNewScoreboard();
        Objective _Objective = board.getObjective("zni");
        if(_Objective==null) _Objective = board.registerNewObjective("zni", "zni", "Blocks Changed");

        
        _Objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        FileConfiguration fc = GetConfig(p);
        Score _score = _Objective.getScore("Mined");
        if(!fc.contains("total.mined"))
            _score.setScore(0);
        else{
            _score.setScore(fc.getInt("total.mined"));
        }

        Score _placed = _Objective.getScore("Placed");
        if(!fc.contains("total.placed"))
            _placed.setScore(0);
        else
            _placed.setScore(fc.getInt("total.placed"));
        


        
        Score _moneyCount = _Objective.getScore("Z$");
        _moneyCount.setScore(GetConfig(p).getInt("balance"));
        


        p.setScoreboard(board);

    }

    public static FileConfiguration GetConfig(Player play) {
        if(Main.GetMainInstance().CustomConfigs.containsKey("Currency_"+play.getName())){
            return Main.GetMainInstance().CustomConfigs.get("Currency_"+play.getName());
        }else {
            FileConfiguration fc = YamlConfiguration.loadConfiguration(new File(Main.GetMainInstance().getDataFolder()+"/"+play.getName(), "currency.yml"));
            Main.GetMainInstance().CustomConfigs.put("Currency_"+play.getName(), fc);

            return fc;
        }
    }

    public static void SaveConfig(Player play) {
        try {
            Main.GetMainInstance().CustomConfigs.get("Currency_"+play.getName()).save(new File(Main.GetMainInstance().getDataFolder()+"/"+play.getName(), "currency.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(e.getPlayer().getGameMode() == GameMode.SURVIVAL){

            Player p = e.getPlayer();
            int tot = GetConfig(p).getInt("total.mined");
            tot++;
            GetConfig(p).set("total.mined", tot);

            // 
            int V = Currency.GetReward(e.getBlock().getType());
            FileConfiguration fcx = GetConfig(p);
            fcx.set("balance", fcx.getInt("balance")+(int)V);
            //Main.GetMainInstance().econ.depositPlayer(e.getPlayer(), (int)V);
            



            SaveConfig(p);
        }
        
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        if(e.getPlayer().getGameMode() == GameMode.SURVIVAL){
            Player p =e.getPlayer();
            int tot = GetConfig(p).getInt("total.placed");
            tot++;
            GetConfig(p).set("total.placed",tot);
            SaveConfig(p);
        }
    }

    @EventHandler
    public void onMarketAccess(InventoryClickEvent ev){
        if(ev.getView().getTitle().endsWith("Market]")) 
        {
            Player p = (Player)ev.getWhoClicked();
            if(p.getGameMode() != GameMode.SURVIVAL)return; // only affect survival users!
            double BALANCE = GetConfig(p).getInt("balance");
            
            if(ev.getCurrentItem() != null && ev.getCurrentItem().getType()!= Material.AIR){
                if(ev.getRawSlot() < ev.getInventory().getSize()){
                    // Buy item from chest
                    // Get the mining reward for the item, then add 20% to obtain the buy price
                    if(!ev.isShiftClick()){

                        int MiningReward = Currency.GetReward(ev.getCurrentItem().getType());
                        int BuyPrice = ((MiningReward / 100)*20) + MiningReward;
    
                        if(BALANCE >= BuyPrice){
                            // Perform purchase
                            p.sendMessage("Purchased : "+ev.getCurrentItem().getType().toString()+" / For : "+BuyPrice);
                            //Main.GetMainInstance().econ.withdrawPlayer(p, BuyPrice);
                            GetConfig(p).set("balance", GetConfig(p).getInt("balance")-BuyPrice);

                            ItemStack _item = ev.getCurrentItem();
                            ItemStack _orig = _item.clone();
                            ev.setCancelled(true);
                            _item.setAmount(_item.getAmount()-1);
    
                            if(_item.getAmount() == 0){
                                _item = null;
    
                            }
    
                            ev.getView().getTopInventory().setItem(ev.getSlot(), _item);
                            _orig.setAmount(1);
                            
                            p.getInventory().addItem(_orig);
                            p.updateInventory();
                            
                            SaveConfig(p);
                            
                        } else {
                            p.sendMessage("Purchase failure! You do not have enough Z$!");
                            ev.setCancelled(true);
                            p.closeInventory();
                        }
                    } else {
                        int MiningReward = Currency.GetReward(ev.getCurrentItem().getType());
                        int BuyPrice = ((MiningReward / 100) * 20) + (MiningReward * ev.getCurrentItem().getAmount());

                        if(BALANCE >= BuyPrice){

                            p.sendMessage("Purchased : "+ev.getCurrentItem().getType().toString()+" / For : "+BuyPrice);
                            GetConfig(p).set("balance", GetConfig(p).getInt("balance")-BuyPrice);
                            SaveConfig(p);

                            ItemStack _item = ev.getCurrentItem();

                            ev.setCancelled(true);
                            ev.getView().getTopInventory().setItem(ev.getSlot(), null);

                            p.getInventory().addItem(_item);
                            p.updateInventory();

                        }else{
                            p.sendMessage("Purchase failure! You do not have enough Z$. Amount required: "+BuyPrice);
                            ev.setCancelled(true);
                            p.closeInventory();
                        }
                    }
                } else {
                    // Sell item
                    // Get mining reward then subtract 20% to obtain the sell reward
                    if(!ev.isShiftClick()){

                        int MiningReward = Currency.GetReward(ev.getCurrentItem().getType());
                        int SellPrice = MiningReward-((MiningReward / 100) * 20);
                        // Sell item
                        GetConfig(p).set("balance", GetConfig(p).getInt("balance")+SellPrice);
                        SaveConfig(p);
    
                        p.sendMessage("Sold : "+ev.getCurrentItem().getType().toString()+" / "+SellPrice);
    
                        ItemStack _item = ev.getCurrentItem();
                        ItemStack _orig = _item.clone();
    
                        ev.setCancelled(true);
                        _item.setAmount(_item.getAmount()-1);
                        
                        if(_item.getAmount()==0){
                            _item = null;
                        }
    
                        ev.getView().setItem(ev.getRawSlot(), _item);
    
                        _orig.setAmount(1);
                        ev.getView().getTopInventory().addItem(_orig);
                            
                        
                        p.updateInventory();
    
                    } else {
                        int MiningReward = Currency.GetReward(ev.getCurrentItem().getType());
                        int SellPrice = (MiningReward * ev.getCurrentItem().getAmount()) - ((MiningReward / 100) * 20);
                        
                        GetConfig(p).set("balance", GetConfig(p).getInt("balance")+SellPrice);
                        SaveConfig(p);

                        p.sendMessage("Sold : "+ev.getCurrentItem().getType().toString()+" / "+SellPrice);

                        ItemStack _item = ev.getCurrentItem();

                        ev.setCancelled(true);

                        ev.getView().setItem(ev.getRawSlot(), null);

                        p.updateInventory();

                        ev.getView().getTopInventory().addItem(_item);
                        
                    }
                }
            } else {
                p.sendMessage("Sorry, but you cannot buy or sell air!");
            }
        }
    }

}