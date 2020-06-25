package zeenai.server;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class NullConfig
{
    
    public static FileConfiguration GetConfig(Player play) {
        if(Main.GetMainInstance().CustomConfigs.containsKey("nullconfig_"+play.getName())){
            return Main.GetMainInstance().CustomConfigs.get("nullconfig_"+play.getName());
        }else {
            Main.GetMainInstance().CustomConfigs.put("nullconfig_"+play.getName(), new YamlConfiguration());
            return Main.GetMainInstance().CustomConfigs.get("nullconfig_"+play.getName());
        }
    }

    
    public static FileConfiguration GetTempConfig(String play) {
        if(Main.GetMainInstance().CustomConfigs.containsKey("nullconfig_"+play)){
            return Main.GetMainInstance().CustomConfigs.get("nullconfig_"+play);
        }else {
            Main.GetMainInstance().CustomConfigs.put("nullconfig_"+play, new YamlConfiguration());
            return Main.GetMainInstance().CustomConfigs.get("nullconfig_"+play);
        }
    }

    public static void SaveTempConfig(File X, String ConfigHandle) {
        try {
            Main.GetMainInstance().CustomConfigs.get("nullconfig_"+ConfigHandle).save(X);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}