package zeenai.server.schematics;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import zeenai.server.Main;
import zeenai.server.NullConfig;
import zeenai.server.antigrief.Healer;
import zeenai.server.schematics.loader.compatibility.SchematicLoader;
import zeenai.server.schematics.loader.compatibility.v1_15_2.SchematicLoader_v1_15_2;
import zeenai.server.treechops.RestoreBlock;

public class Unimport implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command arg1, @NotNull String arg2,
            @NotNull String[] args)
    {
        
        String schemName = "";
        
        for (String string : args) {
            Main.GetMainInstance().getLogger().info("Argument: "+string);
            if(string.compareToIgnoreCase("-a")==0){
                Main.GetMainInstance().getLogger().info("Set all to air!");
                NullConfig.GetTempConfig(sender.getName()).set("SetToAir",true);
                continue;
            } else if(string.compareToIgnoreCase("-e")==0){
                Main.GetMainInstance().getLogger().info("Excluding misc added blocks!");
                NullConfig.GetTempConfig(sender.getName()).set("SetToAir",false);
                continue;
            }
            schemName+=string;
        }
        if(!NullConfig.GetTempConfig(sender.getName()).contains("SetToAir"))NullConfig.GetTempConfig(sender.getName()).set("SetToAir",true);
        

        
        try{

            File f = new File(Main.GetMainInstance().getDataFolder()+"/schematics", schemName+".0.schem3");
            if(f.exists()){
                sender.sendMessage("Loading schematic: "+schemName);
            }else{
                sender.sendMessage("Schematic does not exist on server : "+schemName);
                return false;
            }
            f= new File(Main.GetMainInstance().getDataFolder()+"/schematics", schemName);
            
            String version="";
            try{
                version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            }catch(Exception e){
                e.printStackTrace();
                sender.sendMessage("Error getting server API Version! "+version);
                return false;
            }
            SchematicLoader SL = null;
            switch(version){
                case "v1_15_R1":{
                    SL = new SchematicLoader_v1_15_2();
                    break;
                }
                default: {
                    sender.sendMessage("This version appears to be unsupported! ("+version+")");
                    return false;
                }
            }
            SL.SetUndo(true);
            SL.SetPlayer(sender);
            SL.LoadSchematic(f);
            sender.sendMessage("Preparing to modify "+SL.GetBlockCount()+" blocks");
            
            List<RestoreBlock> RB = SL.GetBlocks();
            // Get current block list and set the backup list!
            

        } catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }
    
}