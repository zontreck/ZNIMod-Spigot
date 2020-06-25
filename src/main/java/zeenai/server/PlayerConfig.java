package zeenai.server;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerConfig
{
    
    public static FileConfiguration GetConfig(Player play) {
        if(Main.GetMainInstance().CustomConfigs.containsKey("config_"+play.getName())){
            return Main.GetMainInstance().CustomConfigs.get("config_"+play.getName());
        }else {
            FileConfiguration fc = YamlConfiguration.loadConfiguration(new File(Main.GetMainInstance().getDataFolder()+"/"+play.getName(), "config.yml"));
            Main.GetMainInstance().CustomConfigs.put("config_"+play.getName(), fc);

            return fc;
        }
    }

    public static void SaveConfig(Player play) {
        try {
            Main.GetMainInstance().CustomConfigs.get("config_"+play.getName()).save(new File(Main.GetMainInstance().getDataFolder()+"/"+play.getName(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}