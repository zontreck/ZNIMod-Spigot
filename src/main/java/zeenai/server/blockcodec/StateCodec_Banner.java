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

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import zeenai.server.Main;
import zeenai.server.NullConfig;

public class StateCodec_Banner implements BlockStateCodec, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Material[] getApplicableMaterials() {
        return new Material[] { Material.BLACK_WALL_BANNER, Material.BLUE_WALL_BANNER, Material.LIGHT_BLUE_WALL_BANNER,
            Material.BROWN_WALL_BANNER, Material.CYAN_WALL_BANNER, Material.GRAY_WALL_BANNER, Material.GREEN_WALL_BANNER, 
            Material.LIGHT_GRAY_WALL_BANNER, Material.LIME_WALL_BANNER, Material.MAGENTA_WALL_BANNER, Material.ORANGE_WALL_BANNER,
            Material.PINK_WALL_BANNER, Material.PURPLE_WALL_BANNER, Material.RED_WALL_BANNER, Material.WHITE_WALL_BANNER,
            Material.YELLOW_WALL_BANNER,Material.BLACK_BANNER,  Material.BLUE_BANNER, Material.BROWN_BANNER,  Material.CYAN_BANNER,
            Material.GRAY_BANNER, Material.GREEN_BANNER, Material.LIGHT_BLUE_BANNER,
           Material.LIGHT_GRAY_BANNER, Material.LIME_BANNER, Material.MAGENTA_BANNER, Material.ORANGE_BANNER,
           Material.PINK_BANNER, Material.PURPLE_BANNER, Material.RED_BANNER, Material.SKELETON_SKULL,
           Material.WHITE_BANNER, Material.YELLOW_BANNER
        };
    }

    private List<Material> FriendlyList;

    public StateCodec_Banner(){
        FriendlyList=new ArrayList<Material>();
        for (Material material : getApplicableMaterials()) {
            FriendlyList.add(material);
        }
    }

    class BannerPattern implements Serializable{

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        public String dyeString;
        public String patternType;
        public Pattern AsPattern(){
            return new Pattern(DyeColor.valueOf(dyeString), PatternType.valueOf(patternType));
        }

        public String AsString(){
            return "";
        }
    }

    class BannerData implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 189543875L;
        public boolean directional;
        public StateCodec_Directional.DirectionalData dir_data;
        public StateCodec_Rotatable.RotatableData rot_data;
        public List<BannerPattern> patterns;
        public String dye;

        public List<Pattern> GetPatternList(){
            List<Pattern> tmp = new ArrayList<Pattern>();
            for (BannerPattern pattern : patterns) {
                tmp.add(pattern.AsPattern());
            }
            return tmp;
        }

        public String AsString(){
            StringBuilder sb = new StringBuilder();

            return sb.toString();
        }
    }

    @Override
    public String serialize(BlockState state) {
        if (FriendlyList.contains(state.getType())) {
            
            Banner bn = (Banner)state;
            BannerData dat = new BannerData();

            BlockData sig = state.getBlockData();
            Rotatable rt=null;
            Directional dr=null;
            if(sig instanceof Rotatable){
                rt=(Rotatable)sig;
                dat.directional=false;
            }
            if(sig instanceof Directional){
                dr=(Directional)sig;
                dat.directional=true;
            }

            if(dat.directional){
                dat.dir_data=new StateCodec_Directional().new DirectionalData();
                dat.dir_data.facing=dr.getFacing().name();
            } else {
                dat.rot_data = new StateCodec_Rotatable().new RotatableData();
                dat.rot_data.Rot = rt.getRotation().name();
            }

            dat.patterns=new ArrayList<BannerPattern>();
            dat.dye = bn.getBaseColor().name();
            for (int i = 0; i < bn.numberOfPatterns(); i++) {
                BannerPattern pt = new BannerPattern();
                Pattern ptx = bn.getPattern(i);
                pt.dyeString=ptx.getColor().name();
                pt.patternType=ptx.getPattern().name();
                dat.patterns.add(pt);
            }

            
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            

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
                    TMP = File.createTempFile("state", "debug_banner");
                    NullConfig.GetTempConfig("banner_debug").set("state", state);
                    NullConfig.SaveTempConfig(TMP, "banner_debug");
                    FileReader FR = new FileReader(TMP);
                    BufferedReader br = new BufferedReader(FR);
                    String contents = "";
                    String cur="";
                    while((cur = br.readLine())!=null){
                        contents+=cur+"\n";
                    }
                    Main.GetMainInstance().getLogger().info("ERROR\n\n: Below is the raw block data for banner\n\n"+contents);

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
        Main.GetMainInstance().getLogger().info("Deserialize banner called");
        if(FriendlyList.contains(state.getType())){
            Main.GetMainInstance().getLogger().info("Deserialize banner called- is instanceof");
            
            Banner bn = (Banner)state;
            BlockData dir = state.getBlockData();
            BannerData dat = new BannerData();
            Directional dixr = null;
            Rotatable rot = null;

            ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(conf));
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);

                dat = (BannerData)ois.readObject();
    
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
                if(dat.directional){
                    dixr = (Directional)dir;
                    dixr.setFacing(BlockFace.valueOf(dat.dir_data.facing));
                }else{
                    rot=(Rotatable)dir;
                    rot.setRotation(BlockFace.valueOf(dat.rot_data.Rot));
                }
                bn.setBaseColor(DyeColor.valueOf(dat.dye));
                bn.setPatterns(dat.GetPatternList());
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
        return "Fence";
    }
    
}