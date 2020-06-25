package zeenai.server.notepad;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import zeenai.server.Main;

public class NotepadConfig
{
    
    public static FileConfiguration GetConfig(Player play) {
        if(Main.GetMainInstance().CustomConfigs.containsKey("notepad_"+play.getName())){
            return Main.GetMainInstance().CustomConfigs.get("notepad_"+play.getName());
        }else {
            FileConfiguration fc = YamlConfiguration.loadConfiguration(new File(Main.GetMainInstance().getDataFolder()+"/"+play.getName(), "notepad.yml"));
            Main.GetMainInstance().CustomConfigs.put("notepad_"+play.getName(), fc);

            return fc;
        }
    }

    public static void SaveConfig(Player play) {
        try {
            Main.GetMainInstance().CustomConfigs.get("notepad_"+play.getName()).save(new File(Main.GetMainInstance().getDataFolder()+"/"+play.getName(), "notepad.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}