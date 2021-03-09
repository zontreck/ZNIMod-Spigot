package zeenai.server.schematics;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import zeenai.server.Main;
import zeenai.server.NullConfig;
import zeenai.server.schematics.writer.SchematicWriter;
import zeenai.server.schematics.writer.compatibility.v1_15_r1.SchematicWriter_v1_15_r1;
import zeenai.server.schematics.writer.compatibility.v1_16_r3.SchematicWriter_v1_16_r3;
import zeenai.server.schematics.writer.compatibility.v1_16_r4.SchematicWriter_v1_16_r4;

public class WriteSchematic implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2,
            @NotNull String[] arg3)
    {
        Player p = (Player)arg0;
        String schemName = "";
        for (String string : arg3) {
            if(string=="-a"){
                NullConfig.GetConfig(p).set("IncludeAir",true);
                continue;
            } else if(string == "-e"){
                NullConfig.GetConfig(p).set("IncludeAir",false);
                continue;
            }
            schemName+=string;
        }

        if(NullConfig.GetConfig(p).get("IncludeAir")==null)NullConfig.GetConfig(p).set("IncludeAir",true);

        NullConfig.GetConfig(p).set("schem.name", schemName);
        NullConfig.GetConfig(p).set("schem.player", p);

        Bukkit.getScheduler().runTaskAsynchronously(Main.GetMainInstance(), new Runnable(){
        
            @Override
            public void run() {
                        
                Player p = (Player)arg0;
                
                // Begin to write schematic!
                String version="";
                try{
                    version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

                }catch(Exception e){
                    p.sendMessage(ChatColor.RED+"Error: Could not get version! "+version);
                    return;
                }

                SchematicWriter sw = null;
                switch(version){
                    case "v1_15_R1":{
                        sw = new SchematicWriter_v1_15_r1();
                        break;
                    }
                    case "v1_16_R3":{
                        sw = new SchematicWriter_v1_16_r4();
                        break;
                    }
                    case "v1_16_R4":{
                        sw=new SchematicWriter_v1_16_r4();
                        break;
                    }
                    default:{
                        p.sendMessage(ChatColor.RED+"Error: Could not find a supported version! "+version);
                        return;
                    }
                }
                
                p.sendMessage(ChatColor.DARK_GREEN+"Preparing to write schematic");
                sw.SetCurrentPlayer(p);
                sw.SetSchematicName(arg3[0]);
                File x = new File(Main.GetMainInstance().getDataFolder()+"/schematics", arg3[0]);


                sw.WriteToSchematic3(x);
                p.sendMessage(ChatColor.AQUA+"Schematic has been written to /schematics/"+arg3[0]+" (Schem3)");

            }
        });

        return true;
    }
    
}