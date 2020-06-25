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
import org.bukkit.block.data.type.Door;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import zeenai.server.Main;
import zeenai.server.NullConfig;

public class StateCodec_Door implements BlockStateCodec, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Material[] getApplicableMaterials() {
        return new Material[] { 
            Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.DARK_OAK_DOOR, Material.IRON_DOOR, Material.JUNGLE_DOOR, Material.OAK_DOOR, 
            Material.SPRUCE_DOOR
        };
    }

    private List<Material> FriendlyList;

    public StateCodec_Door(){
        FriendlyList=new ArrayList<Material>();
        for (Material material : getApplicableMaterials()) {
            FriendlyList.add(material);
        }
    }

    private class DoorData implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 189543875L;
        public String Hinge;
        public String Facing;
        public boolean Open;
        public boolean Power;
        public String Half;

        public String AsString(){
            StringBuilder sb = new StringBuilder();
            sb.append("\nHinge: "+Hinge);
            sb.append("\nFacing: "+Facing);
            sb.append("\nOpen: "+Open);
            sb.append("\nPowered: "+Power);
            sb.append("\nHalf: "+Half);

            return sb.toString();
        }
    }

    @Override
    public String serialize(BlockState state) {
        if (FriendlyList.contains(state.getType())) {

            Door sig = (Door)state.getBlockData();
            

            
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            DoorData dat = new DoorData();
            dat.Facing = sig.getFacing().name();
            dat.Hinge=sig.getHinge().name();
            dat.Open=sig.isOpen();
            dat.Power=sig.isPowered();
            dat.Half = sig.getHalf().name();


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
                    TMP = File.createTempFile("state", "debug_door");
                    NullConfig.GetTempConfig("door_debug").set("state", state);
                    NullConfig.SaveTempConfig(TMP, "door_debug");
                    FileReader FR = new FileReader(TMP);
                    BufferedReader br = new BufferedReader(FR);
                    String contents = "";
                    String cur="";
                    while((cur = br.readLine())!=null){
                        contents+=cur+"\n";
                    }
                    Main.GetMainInstance().getLogger().info("ERROR\n\n: Below is the raw block data for door\n\n"+contents);

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
            Door dir = (Door)state.getBlockData();
            DoorData dat = new DoorData();

            ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(conf));
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);

                dat = (DoorData)ois.readObject();
    
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
                dir.setFacing(BlockFace.valueOf(dat.Facing));
                dir.setHinge(Door.Hinge.valueOf(dat.Hinge));
                dir.setHalf(Bisected.Half.valueOf(dat.Half));
                dir.setOpen(dat.Open);
                dir.setPowered(dat.Power);
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