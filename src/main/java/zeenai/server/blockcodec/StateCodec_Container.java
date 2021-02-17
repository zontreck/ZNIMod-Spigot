package zeenai.server.blockcodec;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import zeenai.server.Main;
import zeenai.server.NullConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StateCodec_Container implements BlockStateCodec, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Material[] getApplicableMaterials() {
        return new Material[] { Material.CHEST, Material.TRAPPED_CHEST, Material.HOPPER, Material.CHEST_MINECART, Material.HOPPER_MINECART, Material.SHULKER_BOX,
                Material.DROPPER, Material.DISPENSER,
                Material.BARREL
        };
    }

    private List<Material> FriendlyList;

    public StateCodec_Container(){
        FriendlyList=new ArrayList<Material>();
        for (Material material : getApplicableMaterials()) {
            FriendlyList.add(material);
        }
    }

    class ContainerData implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 189543875L;
        public String Contents;

        public String AsString(){
            StringBuilder sb = new StringBuilder();
            sb.append("Container Data "+Contents);

            return sb.toString();
        }
    }

    @Override
    public String serialize(BlockState state) {
        if (FriendlyList.contains(state.getType())) {
            
            Container cn = (Container)state;
            ContainerData dat = new ContainerData();
            Inventory inv = cn.getInventory();
            YamlConfiguration yml = new YamlConfiguration();
            yml.set("contents", inv.getContents());
            dat.Contents=yml.saveToString();


            ByteArrayOutputStream baos = new ByteArrayOutputStream();


            try
            {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(dat);
                oos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            String encoded = Base64Coder.encodeLines(baos.toByteArray());


            return encoded;
            
        }
        
        {
            if(FriendlyList.contains(state.getType())){
                File TMP=null;
                try {
                    TMP = File.createTempFile("state", "debug_container");
                    NullConfig.GetTempConfig("container_debug").set("state", state);
                    NullConfig.SaveTempConfig(TMP, "container_debug");
                    FileReader FR = new FileReader(TMP);
                    BufferedReader br = new BufferedReader(FR);
                    String contents = "";
                    String cur="";
                    while((cur = br.readLine())!=null){
                        contents+=cur+"\n";
                    }
                    Main.GetMainInstance().getLogger().info("ERROR\n\n: Below is the raw block data for container\n\n"+contents);

                    br.close();
                    FR.close();
                    TMP.delete();
                } catch(Exception e){

                }
            }
        }
        return null;
    }

    @Override
    public void deserialize(BlockState state, String conf) {
        Main.GetMainInstance().getLogger().info("Deserialize container called");
        if(FriendlyList.contains(state.getType())){
            Main.GetMainInstance().getLogger().info("Deserialize container called- is instanceof");
            
            Container bn = (Container)state;
            ContainerData dat = new ContainerData();

            ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(conf));
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);

                dat = (ContainerData)ois.readObject();
    
                ois.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Main.GetMainInstance().getLogger().info("Deserialized!\n\n"+dat.AsString());
            if(conf != null) {
                YamlConfiguration yml = new YamlConfiguration();
                try {
                    yml.loadFromString(dat.Contents);
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                bn.getInventory().setContents((ItemStack[])yml.get("contents"));


            }
        }

    }

    @Override
    public String toString(String conf) {
/*        if(conf != null){
            StringBuilder sb = new StringBuilder();
            for (String line : conf.getStringList("state.lines")) {
                if(sb.length() > 0){
                    sb.append(" ");
                }
                sb.append("[").append(line).append("]");
            }
            return sb.toString();
        }*/
        return null;
    }

    @Override
    public String getID() {
        return "Fence";
    }
    
}