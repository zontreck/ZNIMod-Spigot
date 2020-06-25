package zeenai.server.currency;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import zeenai.server.Main;

public class Currency
{
    

    public static FileConfiguration GetConfig() {
        if(Main.GetMainInstance().CustomConfigs.containsKey("Currency")){
            return Main.GetMainInstance().CustomConfigs.get("Currency");
        }else {
            File f = new File(Main.GetMainInstance().getDataFolder(), "currency.yml");
            FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
            if(!f.exists()){
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.GetMainInstance(), new Runnable(){

                    @Override
                    public void run() {

                        ConfigurationSection sect = fc.createSection("rewards");
                        sect.set("default", 1);
                        
                        for(Material mat : Material.values()){
                            sect.set(mat.toString(), 2);
                        }
                        Bukkit.broadcastMessage("Default Block Rewards previously undefined.\n\n[Initialization Completed]");
                        SaveConfig();
                    }
                    
                }, 1000L);
                
            }
            Main.GetMainInstance().CustomConfigs.put("Currency", fc);
            

            return fc;
        }
    }

    public static void SaveConfig() {
        try {
            Main.GetMainInstance().CustomConfigs.get("Currency").save(new File(Main.GetMainInstance().getDataFolder(), "currency.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int GetReward(Material mat){
        return GetConfig().getInt("rewards."+mat.toString());
    }

    public static void SetReward(Material mat, int cost){
        GetConfig().set("rewards."+mat.toString(), cost);
        SaveConfig();
    }
}