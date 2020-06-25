package zeenai.server.blockcodec;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Axis;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Orientable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import zeenai.server.Main;
import zeenai.server.NullConfig;

public class StateCodec_Orientable implements BlockStateCodec, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Material[] getApplicableMaterials() {
        return new Material[] { 
            Material.ACACIA_LOG,
            Material.ACACIA_WOOD,
            Material.BIRCH_LOG,
            Material.BIRCH_WOOD,
            Material.BONE_BLOCK,
            Material.DARK_OAK_LOG,
            Material.DARK_OAK_WOOD,
            Material.HAY_BLOCK,
            Material.JUNGLE_LOG,
            Material.JUNGLE_WOOD,
            Material.NETHER_PORTAL,
            Material.OAK_LOG,
            Material.OAK_WOOD,
            Material.PURPUR_PILLAR,
            Material.QUARTZ_PILLAR,
            Material.SPRUCE_LOG,
            Material.SPRUCE_WOOD,
            Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_ACACIA_WOOD,
            Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_BIRCH_WOOD,
            Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_DARK_OAK_WOOD,
            Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_JUNGLE_WOOD,
            Material.STRIPPED_OAK_LOG, Material.STRIPPED_OAK_WOOD,
            Material.STRIPPED_SPRUCE_LOG, Material.STRIPPED_SPRUCE_WOOD
        };
    }

    private List<Material> FriendlyList;

    public StateCodec_Orientable(){
        FriendlyList=new ArrayList<Material>();
        for (Material material : getApplicableMaterials()) {
            FriendlyList.add(material);
        }
    }

    private class OrientableData implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 189543875L;
        public String Axis;

        public String AsString(){
            StringBuilder sb = new StringBuilder();
            sb.append("\nAxis: "+Axis);

            return sb.toString();
        }
    }

    @Override
    public String serialize(BlockState state) {
        if (FriendlyList.contains(state.getType())) {

            String direction= "";
            Orientable sig = (Orientable)state.getBlockData();
            direction = sig.getAxis().name();
            
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            OrientableData dat = new OrientableData();

            if(direction!= ""){
                dat.Axis = direction;
            }

            try {
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
                    TMP = File.createTempFile("state", "debug_orientable");
                    NullConfig.GetTempConfig("orientable_debug").set("state", state);
                    NullConfig.SaveTempConfig(TMP, "orientable_debug");
                    FileReader FR = new FileReader(TMP);
                    BufferedReader br = new BufferedReader(FR);
                    String contents = "";
                    String cur="";
                    while((cur = br.readLine())!=null){
                        contents+=cur+"\n";
                    }
                    Main.GetMainInstance().getLogger().info("ERROR\n\n: Below is the raw block data for orientable\n\n"+contents);

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
        Main.GetMainInstance().getLogger().info("Deserialize orientable called");
        if(FriendlyList.contains(state.getType())){
            Main.GetMainInstance().getLogger().info("Deserialize orientable called- is instanceof");
            Orientable dir = (Orientable)state.getBlockData();
            OrientableData dat = new OrientableData();

            ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(conf));
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);

                dat = (OrientableData)ois.readObject();
    
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

                dir.setAxis(Axis.valueOf(dat.Axis));
                state.setBlockData(dir);


                
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
        return "Orientable";
    }
    
}