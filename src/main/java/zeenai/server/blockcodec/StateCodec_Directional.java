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
import org.bukkit.block.data.Directional;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import zeenai.server.Main;
import zeenai.server.NullConfig;

public class StateCodec_Directional implements BlockStateCodec, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Material[] getApplicableMaterials() {
        return new Material[] { Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL, 
                Material.ATTACHED_MELON_STEM, Material.ATTACHED_PUMPKIN_STEM, 
                Material.BLUE_GLAZED_TERRACOTTA,
                Material.BROWN_GLAZED_TERRACOTTA,
                Material.CARVED_PUMPKIN,
                Material.CREEPER_WALL_HEAD,
                Material.CYAN_GLAZED_TERRACOTTA,
                Material.DRAGON_WALL_HEAD,
                Material.END_ROD,
                Material.GRAY_GLAZED_TERRACOTTA,
                Material.GREEN_GLAZED_TERRACOTTA,
                Material.JACK_O_LANTERN,
                Material.JIGSAW,
                Material.LIGHT_BLUE_GLAZED_TERRACOTTA,
                Material.LIGHT_GRAY_GLAZED_TERRACOTTA,
                Material.LIME_GLAZED_TERRACOTTA,
                Material.LOOM,
                Material.MAGENTA_GLAZED_TERRACOTTA,
                Material.ORANGE_GLAZED_TERRACOTTA,
                Material.PINK_GLAZED_TERRACOTTA,
                Material.PLAYER_WALL_HEAD,
                Material.PURPLE_GLAZED_TERRACOTTA,
                Material.RED_GLAZED_TERRACOTTA,
                Material.SKELETON_WALL_SKULL,
                Material.STONECUTTER,
                Material.WALL_TORCH,
                Material.REDSTONE_WALL_TORCH,
                Material.WHITE_GLAZED_TERRACOTTA,
                Material.WITHER_SKELETON_WALL_SKULL,
                Material.YELLOW_GLAZED_TERRACOTTA,
                Material.ZOMBIE_WALL_HEAD,
                Material.BARREL, Material.BLACK_WALL_BANNER, Material.BLUE_WALL_BANNER, Material.LIGHT_BLUE_WALL_BANNER,
                Material.BROWN_WALL_BANNER, Material.CYAN_WALL_BANNER, Material.GRAY_WALL_BANNER, Material.GREEN_WALL_BANNER, 
                Material.LIGHT_GRAY_WALL_BANNER, Material.LIME_WALL_BANNER, Material.MAGENTA_WALL_BANNER, Material.ORANGE_WALL_BANNER,
                Material.PINK_WALL_BANNER, Material.PURPLE_WALL_BANNER, Material.RED_WALL_BANNER, Material.WHITE_WALL_BANNER,
                Material.YELLOW_WALL_BANNER

        };
    }

    private List<Material> FriendlyList;

    public StateCodec_Directional(){
        FriendlyList=new ArrayList<Material>();
        for (Material material : getApplicableMaterials()) {
            FriendlyList.add(material);
        }
    }

    private class DirectionalData implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 189543875L;
        public String facing;

        public String AsString(){
            StringBuilder sb = new StringBuilder();
            sb.append("\nFacing: "+facing);

            return sb.toString();
        }
    }

    @Override
    public String serialize(BlockState state) {
        if (FriendlyList.contains(state.getType())) {

            String direction= "";
            Directional sig = (Directional)state.getBlockData();
            direction = sig.getFacing().name();
            
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            DirectionalData dat = new DirectionalData();

            if(direction!= ""){
                dat.facing=direction;
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
                    TMP = File.createTempFile("state", "debug");
                    NullConfig.GetTempConfig("directional_debug").set("state", state);
                    NullConfig.SaveTempConfig(TMP, "directional_debug");
                    FileReader FR = new FileReader(TMP);
                    BufferedReader br = new BufferedReader(FR);
                    String contents = "";
                    String cur="";
                    while((cur = br.readLine())!=null){
                        contents+=cur+"\n";
                    }
                    Main.GetMainInstance().getLogger().info("ERROR\n\n: Below is the raw block data for directional\n\n"+contents);

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
        Main.GetMainInstance().getLogger().info("Deserialize directional called");
        if(FriendlyList.contains(state.getType())){
            Main.GetMainInstance().getLogger().info("Deserialize directional called- is instanceof");
            Directional dir = (Directional)state.getBlockData();
            DirectionalData dat = new DirectionalData();

            ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(conf));
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);

                dat = (DirectionalData)ois.readObject();
    
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

                dir.setFacing(BlockFace.valueOf(dat.facing));
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
        return "Directional";
    }
    
}