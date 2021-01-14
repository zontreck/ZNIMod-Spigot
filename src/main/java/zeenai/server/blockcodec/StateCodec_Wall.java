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
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Wall;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import zeenai.server.Main;
import zeenai.server.NullConfig;

public class StateCodec_Wall implements BlockStateCodec, Serializable
{
    
    private static final long serialVersionUID = 1L;

    
    @Override
    public Material[] getApplicableMaterials() {
        return new Material[] { 
            Material.ANDESITE_WALL, Material.COBBLESTONE_WALL, 
            Material.DIORITE_WALL, Material.END_STONE_BRICK_WALL, Material.MOSSY_COBBLESTONE_WALL,
            Material.MOSSY_STONE_BRICK_WALL, Material.NETHER_BRICK_WALL, Material.PRISMARINE_WALL, 
            Material.RED_NETHER_BRICK_WALL, Material.RED_SANDSTONE_WALL, Material.SANDSTONE_WALL, Material.STONE_BRICK_WALL, Material.BRICK_WALL,
            Material.GRANITE_WALL, Material.BLACKSTONE_WALL, Material.POLISHED_BLACKSTONE_WALL, Material.POLISHED_BLACKSTONE_BRICK_WALL
        };
    }

    private class WallData implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 189543875L;
        public boolean isUp;
        public List<String> ActiveFaces;
        public List<String> heights;

        public String AsString(){
            StringBuilder sb = new StringBuilder();
            for (String face : ActiveFaces) {
                sb.append("\nActive Face: "+face);
            }
            sb.append("\nIs Up: "+isUp);

            return sb.toString();
        }
    }
    
    private List<Material> FriendlyList;

    public StateCodec_Wall(){
        FriendlyList=new ArrayList<Material>();
        for (Material material : getApplicableMaterials()) {
            FriendlyList.add(material);
        }
    }


    
    @Override
    public String serialize(BlockState state) {
        if (FriendlyList.contains(state.getType())) {

            Wall sig = (Wall)state.getBlockData();

            BlockFace[] facess = new BlockFace[] {
                BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH
            };
            
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            WallData dat = new WallData();
            dat.ActiveFaces = new ArrayList<String>();
            dat.heights = new ArrayList<String>();
            for (BlockFace blockFace : facess) {
                dat.ActiveFaces.add(blockFace.name());
                dat.heights.add(sig.getHeight(blockFace).name());
            }
            dat.isUp = sig.isUp();


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
                    TMP = File.createTempFile("state", "debug_wall");
                    NullConfig.GetTempConfig("wall_debug").set("state", state);
                    NullConfig.SaveTempConfig(TMP, "wall_debug");
                    FileReader FR = new FileReader(TMP);
                    BufferedReader br = new BufferedReader(FR);
                    String contents = "";
                    String cur="";
                    while((cur = br.readLine())!=null){
                        contents+=cur+"\n";
                    }
                    Main.GetMainInstance().getLogger().info("ERROR\n\n: Below is the raw block data for wall\n\n"+contents);

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
        Main.GetMainInstance().getLogger().info("Deserialize wall called");
        if(FriendlyList.contains(state.getType())){
            Main.GetMainInstance().getLogger().info("Deserialize wall called- is instanceof");
            Wall dir = (Wall)state.getBlockData();
            WallData dat = new WallData();

            ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(conf));
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);

                dat = (WallData)ois.readObject();
    
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
                for (String face : dat.ActiveFaces) {
                    //dir.setFace(BlockFace.valueOf(face), true);
                    dir.setHeight(BlockFace.valueOf(face), Wall.Height.valueOf(dat.heights.get(dat.ActiveFaces.indexOf(face))));
                }
                dir.setUp(dat.isUp);
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
        return "Wall";
    }
}