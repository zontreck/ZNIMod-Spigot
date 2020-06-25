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

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Slab;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import zeenai.server.Main;
import zeenai.server.NullConfig;

public class StateCodec_Slabs implements BlockStateCodec, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Material[] getApplicableMaterials() {
        return new Material[] { 
            Material.ACACIA_SLAB, Material.ANDESITE_SLAB, Material.BIRCH_SLAB, Material.BRICK_SLAB, Material.COBBLESTONE_SLAB,
            Material.CUT_RED_SANDSTONE_SLAB, Material.CUT_SANDSTONE_SLAB, Material.DARK_OAK_SLAB, Material.DARK_PRISMARINE_SLAB,
            Material.DIORITE_SLAB, Material.END_STONE_BRICK_SLAB, Material.GRANITE_SLAB, Material.JUNGLE_SLAB, Material.MOSSY_COBBLESTONE_SLAB,
            Material.MOSSY_STONE_BRICK_SLAB, Material.NETHER_BRICK_SLAB, Material.OAK_SLAB, Material.PETRIFIED_OAK_SLAB, 
            Material.POLISHED_ANDESITE_SLAB, Material.POLISHED_DIORITE_SLAB, Material.POLISHED_GRANITE_SLAB,
            Material.PRISMARINE_BRICK_SLAB, Material.PRISMARINE_SLAB, Material.PURPUR_SLAB, Material.QUARTZ_SLAB, Material.RED_NETHER_BRICK_SLAB,
            Material.RED_SANDSTONE_SLAB, Material.SANDSTONE_SLAB, Material.SMOOTH_QUARTZ_SLAB, Material.SMOOTH_RED_SANDSTONE_SLAB,
            Material.SMOOTH_SANDSTONE_SLAB, Material.SMOOTH_STONE_SLAB, Material.SPRUCE_SLAB, Material.STONE_BRICK_SLAB,
            Material.STONE_SLAB
        };
    }

    private List<Material> FriendlyList;

    public StateCodec_Slabs(){
        FriendlyList=new ArrayList<Material>();
        for (Material material : getApplicableMaterials()) {
            FriendlyList.add(material);
        }
    }

    private class SlabsData implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 189543875L;
        public String SlabType;

        public String AsString(){
            StringBuilder sb = new StringBuilder();
            sb.append("\nAxis: "+SlabType);

            return sb.toString();
        }
    }

    @Override
    public String serialize(BlockState state) {
        if (FriendlyList.contains(state.getType())) {

            String direction= "";
            Slab sig = (Slab)state.getBlockData();
            direction = sig.getType().name();
            
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            SlabsData dat = new SlabsData();

            if(direction!= ""){
                dat.SlabType = direction;
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
                    TMP = File.createTempFile("state", "debug_slabs");
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
            Slab dir = (Slab)state.getBlockData();
            SlabsData dat = new SlabsData();

            ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(conf));
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);

                dat = (SlabsData)ois.readObject();
    
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

                dir.setType(Slab.Type.valueOf(dat.SlabType));
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
        return "Slabs";
    }
    
}