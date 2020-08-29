package zeenai.server.antigrief;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import com.google.common.collect.Queues;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import zeenai.server.Main;
import zeenai.server.treechops.RestoreBlock;

public class Healer implements Runnable
{
    private static Healer _inst;
    public static Healer GetInstance(){
        if(_inst==null)_inst=new Healer();
        return _inst;
    }
    public Healer(){}

    public Map<Location, RestoreBlock> Queues = new HashMap<Location, RestoreBlock>();
    public Map<Location, RestoreBlock> backupMap = new HashMap<Location, RestoreBlock>(); // a non-automatically cleared list
    public int Pass = 0;
    public long LastRestoreTime;

    public int LastBlock;

    public boolean TNT=false;

    public void RemoveRestoreQueueAt(Location loc){
        for (RestoreBlock restoreBlock : Queues.values()) {
            if(restoreBlock.loc==loc){
                Queues.remove(restoreBlock);
                return;
            }
        }
    }

    public void GenStats(Scoreboard board, Player p) {
        //ScoreboardManager mgr = Bukkit.getScoreboardManager();
        //Scoreboard board = mgr.getNewScoreboard();
        Objective _Objective = board.getObjective("zni");
        if(_Objective==null) _Objective = board.registerNewObjective("zni", "zni", "Block Healing");

        _Objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score _score = _Objective.getScore("Queued");
        _score.setScore(Queues.size());

        Score _placed = _Objective.getScore("Current");
        _placed.setScore(LastBlock);
        

        p.setScoreboard(board);

    }

    public void Heal(){
        for (int i = 0; i < 2000; i++) {
            // Restore 2000 blocks in a single batch
            if(Pass!=2) run();
        }
    }
    @Override
    public void run() {
        FileConfiguration fc = AntiGrief.GetConfig();
        int Seconds=0;
        if(!fc.contains("antigrief.millisecBetween")){
            fc.set("antigrief.millisecBetween",5000);
            Seconds=5000;
            AntiGrief.SaveConfig();
        } else {
            Seconds=fc.getInt("antigrief.millisecBetween");
        }

        if(!fc.contains("antigrief.preventBlockFall")){
            fc.set("antigrief.preventBlockFall", false);
            AntiGrief.SaveConfig();
        } 
        if(!fc.contains("antigrief.preventNonPlayerBlockChange")) 
        {
            fc.set("antigrief.preventNonPlayerBlockChange", true);
            AntiGrief.SaveConfig();
        }
        if(!fc.contains("antigrief.healExplode"))
        {
            fc.set("antigrief.healExplode", true);
            AntiGrief.SaveConfig();
        }
        if(!fc.contains("antigrief.healFire")) 
        {
            fc.set("antigrief.healFire", true);
            AntiGrief.SaveConfig();
        }
        

        // Heal this block
        // If this block has changed once the 2nd pass starts, then repair it again and reschedule 2nd pass.
        // 2nd pass should run in a for loop, however first run will occur inside a timer

        // If pass=0, set pass to one IF queue has items
        //Main.GetMainInstance().getLogger().info("Queued items : " + Queues.size());
        
        if(Queues.size()>0){
            
            List<RestoreBlock> tmp = new ArrayList<RestoreBlock>(Queues.values());
            if(Pass == 0){
                Pass=1;
            }

            if(Pass==1){

                if(Instant.now().toEpochMilli()+Seconds>LastRestoreTime){
                    LastRestoreTime = Instant.now().toEpochMilli()+ Seconds;
                    // Heal a block
                    if(LastBlock>=Queues.size()){
                        // Increment pass, reset last block
                        LastBlock=0;
                        Pass=2;
                        return;
                    }
                    RestoreBlock rb = tmp.get(LastBlock);
                    while(rb.mat.compareTo(Material.AIR)==0 && rb.loc.getBlock().getType().compareTo(Material.AIR)==0){
                        LastBlock++;
                        if(LastBlock>=Queues.size()){
                            // Increment pass, reset last block
                            LastBlock=0;
                            Pass=2;
                            return;
                        }
                        rb=tmp.get(LastBlock);
                    }

                    if(rb.mat.compareTo(Material.TNT)==0 && TNT){
                        // skip this block!
                        LastBlock++;
                        return;
                    }

                    //Main.GetMainInstance().getLogger().info("Restoring block..");
                    // Restore the block
                    Random rnd = new Random();
                    Block b = rb.loc.getBlock();

                    if(rb.mat.compareTo(b.getType())==0){
                        LastBlock++;
                        return;
                    }
                    Chunk c = b.getChunk();
                    if(!c.isLoaded()){
                        while(!c.isLoaded()){
                            c.load();
                        }
                    }
                    b.setBiome(rb.biome);
                    b.setType(rb.mat, false);
                    if(!Main.GetMainInstance().forceQueue.contains(rb))Main.GetMainInstance().forceQueue.add(rb);
                    

                    b.getWorld().playSound(b.getLocation(), Sound.ENTITY_ITEM_PICKUP,1F,rnd.nextFloat() * 2);

                    LastBlock++;

                    if(LastBlock>=Queues.size()){
                        // Increment pass, reset last block
                        LastBlock=0;
                        Pass=2;
                    }
                }
            } else if(Pass==2){
                Main.GetMainInstance().getLogger().info("Pass 2 starting!");
                // This will run over the blocks in the list and compare. If something does not match, then pass 1 will be scheduled again but only for the blocks that do not match
                for (int i = 0; i < Queues.size(); i++) {
                    RestoreBlock rb = tmp.get(i);
                    Block current = rb.loc.getBlock();
                    Chunk c = current.getChunk();
                    if(!c.isLoaded()){
                        while(!c.isLoaded()){
                            c.load();
                        }
                    }
                    if(current.getType().compareTo(rb.mat)==0){
                        // The type matches!
                        
                        // For good measure, apply the blockstate, then remove this block from queue
                        rb.ApplyState();
                        Random rnd = new Random();
                        current.getWorld().playSound(current.getLocation(), Sound.BLOCK_ANVIL_USE, 1F, rnd.nextFloat() * 2);

                        Queues.remove(rb.loc);
                    }else {
                        if(TNT && rb.mat.compareTo(Material.TNT)==0){
                            Queues.remove(rb.loc);
                            continue;
                        }else{
                            // Not same, don't remove from the queue list, but put us back in Pass 1
                            Pass=1;
                            LastRestoreTime = 0L;
                        }
                    }
                }

                if(Queues.size()==0){
                    // Queue emptied!
                    Pass=0;
                    LastBlock=0;
                    TNT=false;
                    LastRestoreTime=0L;
                    Queues.clear();
                    tmp.clear();
                }
            }
        }else return;

    }
}