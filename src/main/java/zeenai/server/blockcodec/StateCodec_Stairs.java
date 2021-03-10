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
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import zeenai.server.Main;
import zeenai.server.NullConfig;

public class StateCodec_Stairs implements BlockStateCodec, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Material[] getApplicableMaterials() {
        return new Material[] { 
            Material.ACACIA_STAIRS, Material.ANDESITE_STAIRS, Material.BIRCH_STAIRS, Material.BRICK_STAIRS, Material.COBBLESTONE_STAIRS,
            Material.DARK_OAK_STAIRS, Material.DARK_PRISMARINE_STAIRS, Material.DIORITE_STAIRS, Material.END_STONE_BRICK_STAIRS,
            Material.GRANITE_STAIRS, Material.JUNGLE_STAIRS, Material.MOSSY_COBBLESTONE_STAIRS, Material.MOSSY_STONE_BRICK_STAIRS,
            Material.NETHER_BRICK_STAIRS, Material.OAK_STAIRS, Material.POLISHED_ANDESITE_STAIRS, Material.POLISHED_DIORITE_STAIRS,
            Material.POLISHED_GRANITE_STAIRS, Material.PRISMARINE_BRICK_STAIRS, Material.PRISMARINE_STAIRS, 
            Material.PURPUR_STAIRS, Material.QUARTZ_STAIRS, Material.RED_NETHER_BRICK_STAIRS, Material.RED_SANDSTONE_STAIRS,
            Material.SANDSTONE_STAIRS, Material.SMOOTH_QUARTZ_STAIRS, Material.SMOOTH_RED_SANDSTONE_STAIRS, Material.SMOOTH_SANDSTONE_STAIRS,
            Material.SPRUCE_STAIRS, Material.STONE_BRICK_STAIRS, Material.STONE_STAIRS
        };
    }

    private List<Material> FriendlyList;

    public StateCodec_Stairs(){
        FriendlyList=new ArrayList<Material>();
        for (Material material : getApplicableMaterials()) {
            FriendlyList.add(material);
        }
    }

    private class StairsData implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 189543875L;
        public String Shape;
        public String Facing;
        public String Half;
        public boolean Waterlogged;

        public String AsString(){
            StringBuilder sb = new StringBuilder();
            sb.append("\nShape: "+Shape);
            sb.append("\nFacing: "+Facing);
            sb.append("\nHalf: "+Half);
            sb.append("\nWaterlogged: "+Waterlogged);


            return sb.toString();
        }
    }

    @Override
    public String serialize(BlockState state) {
        try{

            if (FriendlyList.contains(state.getType())) {

                String direction= "";
                Stairs sig = (Stairs)state.getBlockData();
                direction = sig.getShape().name();
                
                
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
                StairsData dat = new StairsData();
    
                if(direction!= ""){
                    dat.Shape = direction;
                }

                dat.Facing = sig.getFacing().name();
                dat.Half = sig.getHalf().name();
                dat.Waterlogged = sig.isWaterlogged();
    
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
        }catch(Exception e){

        }
        
        {
            if(FriendlyList.contains(state.getType())){
                File TMP=null;
                try {
                    TMP = File.createTempFile("state", "debug_stairs");
                    NullConfig.GetTempConfig("stairs_debug").set("state", state);
                    NullConfig.SaveTempConfig(TMP, "stairs_debug");
                    FileReader FR = new FileReader(TMP);
                    BufferedReader br = new BufferedReader(FR);
                    String contents = "";
                    String cur="";
                    while((cur = br.readLine())!=null){
                        contents+=cur+"\n";
                    }
                    Main.GetMainInstance().getLogger().info("ERROR\n\n: Below is the raw block data for stairs\n\n"+contents);

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
        Main.GetMainInstance().getLogger().info("Deserialize stairs called");
        if(FriendlyList.contains(state.getType())){
            Main.GetMainInstance().getLogger().info("Deserialize stairs called- is instanceof");
            Stairs dir = (Stairs)state.getBlockData();
            StairsData dat = new StairsData();

            ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(conf));
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);

                dat = (StairsData)ois.readObject();
    
                ois.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            Main.GetMainInstance().getLogger().info("Deserialized!\n\n"+dat.AsString());
            if(conf != null) {
                dir.setShape(Stairs.Shape.valueOf(dat.Shape));
                dir.setFacing(BlockFace.valueOf(dat.Facing));
                dir.setHalf(Bisected.Half.valueOf(dat.Half));
                dir.setWaterlogged(dat.Waterlogged);
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
        return "Stairs";
    }
    
}