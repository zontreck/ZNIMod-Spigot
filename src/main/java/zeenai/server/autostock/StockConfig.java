package zeenai.server.autostock;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import zeenai.server.Main;

public class StockConfig
{
    
    public static FileConfiguration GetConfig(Player play) {
        if(Main.GetMainInstance().CustomConfigs.containsKey("config_stocks")){
            return Main.GetMainInstance().CustomConfigs.get("config_stocks");
        }else {
            FileConfiguration fc = YamlConfiguration.loadConfiguration(new File(Main.GetMainInstance().getDataFolder()+"/", "stocks.yml"));
            Main.GetMainInstance().CustomConfigs.put("config_stocks", fc);

            return fc;
        }
    }

    public static void SaveConfig(Player play) {
        try {
            Main.GetMainInstance().CustomConfigs.get("config_stocks").save(new File(Main.GetMainInstance().getDataFolder()+"/", "stocks.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}