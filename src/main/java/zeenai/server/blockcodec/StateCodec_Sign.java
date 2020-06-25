package zeenai.server.blockcodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import zeenai.server.Main;

public class StateCodec_Sign implements BlockStateCodec, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Material[] getApplicableMaterials() {
        return new Material[] { Material.ACACIA_SIGN, Material.ACACIA_WALL_SIGN, Material.BIRCH_SIGN,
                Material.BIRCH_WALL_SIGN, Material.DARK_OAK_SIGN, Material.DARK_OAK_WALL_SIGN, Material.JUNGLE_SIGN,
                Material.JUNGLE_WALL_SIGN, Material.OAK_SIGN, Material.OAK_WALL_SIGN, Material.SPRUCE_SIGN,
                Material.SPRUCE_WALL_SIGN };
    }

    private class SignData implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 189543875L;
        public List<String> lines;
        public String color;
        public boolean wallSign;
        public String facing;
        public boolean Waterlogged;

        public String AsString(){
            StringBuilder sb = new StringBuilder();
            sb.append("Lines: ");
            sb.append(lines);
            sb.append("\n\nColor: "+color);
            sb.append("\nWallSign: "+wallSign);
            sb.append("\nFacing: "+facing);
            sb.append("\nWaterlogged: "+Waterlogged);

            return sb.toString();
        }
    }

    @Override
    public String serialize(BlockState state) {
        if (state instanceof Sign) {
            Sign sign = (Sign) state;
            
            String[] lines = sign.getLines();
            boolean hasText = false;
            for (int i = 0; i < lines.length; i++) {
                if (lines[i] != null && lines[i].length() > 0) {
                    hasText = true;
                    break;
                }
            }

            DyeColor dcol = sign.getColor();
            if (dcol == null)
                dcol = DyeColor.BLACK;

            String facing = "";
            String direction = "";
            boolean waterlog=false;

            if(state.getType().name().contains("WALL_SIGN")){
                WallSign ws = (WallSign)state.getBlockData();
                facing = ws.getFacing().name();
                waterlog=ws.isWaterlogged();
            }else {
                org.bukkit.block.data.type.Sign sig = (org.bukkit.block.data.type.Sign)state.getBlockData();
                direction = sig.getRotation().name();
                waterlog = sig.isWaterlogged();
            }
            

            if (hasText || dcol != DyeColor.BLACK) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                SignData dat = new SignData();

                if (hasText)
                    dat.lines = Arrays.asList(lines);

                if (dcol != DyeColor.BLACK)
                    dat.color = dcol.name();

                if(facing != "")
                {
                    dat.wallSign = true;
                    dat.facing = facing;
                }

                if(direction!= ""){
                    dat.wallSign=false;
                    dat.facing=direction;
                }
                dat.Waterlogged=waterlog;

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
        }
        return null;
    }

    @Override
    public void deserialize(BlockState state, String conf) {
        Main.GetMainInstance().getLogger().info("Deserialize sign called");
        if(state instanceof Sign){
            Main.GetMainInstance().getLogger().info("Deserialize sign called- is instanceof");
            Sign sign = (Sign)state;
            SignData dat = null;
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(conf));
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);

                dat = (SignData)ois.readObject();
    
                ois.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Main.GetMainInstance().getLogger().info("Deserialized!\n\n"+dat.AsString());
            DyeColor dcol = DyeColor.BLACK;
            List<String> lines = Collections.emptyList();
            if(conf != null) {
                if(dat.lines!=null) lines = dat.lines;

                if(dat.color!= null) {
                    try{
                        dcol = DyeColor.valueOf(dat.color);
                    } catch(IllegalArgumentException | NullPointerException e){
                        e.printStackTrace();
                    }
                }

                if(dat.wallSign){
                    WallSign ws = (WallSign)state.getBlockData();
                    ws.setFacing(BlockFace.valueOf(dat.facing));
                    ws.setWaterlogged(dat.Waterlogged);

                    state.setBlockData(ws);
                }else{
                    org.bukkit.block.data.type.Sign dir = (org.bukkit.block.data.type.Sign)sign.getBlockData();
                    dir.setRotation(BlockFace.valueOf(dat.facing));
                    dir.setWaterlogged(dat.Waterlogged);

                    state.setBlockData(dir);
                }

                
            }

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.size() > i && lines.get(i) != null ? lines.get(i) : "";
                sign.setLine(i,line);
            }
            sign.setColor(dcol);
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
        return "Sign";
    }
    
}