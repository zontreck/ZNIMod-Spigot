package zeenai.server.antigrief;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import zeenai.server.Main;

public class AntiGrief
{
    
    public static FileConfiguration GetConfig() {
        if(Main.GetMainInstance().CustomConfigs.containsKey("config_antigrief")){
            return Main.GetMainInstance().CustomConfigs.get("config_antigrief");
        }else {
            FileConfiguration fc = YamlConfiguration.loadConfiguration(new File(Main.GetMainInstance().getDataFolder(), "antigrief.yml"));
            Main.GetMainInstance().CustomConfigs.put("config_antigrief", fc);

            return fc;
        }
    }

    public static void SaveConfig() {
        try {
            Main.GetMainInstance().CustomConfigs.get("config_antigrief").save(new File(Main.GetMainInstance().getDataFolder(), "antigrief.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}