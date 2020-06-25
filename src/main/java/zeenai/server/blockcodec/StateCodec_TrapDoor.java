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
import org.bukkit.block.data.type.TrapDoor;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import zeenai.server.Main;
import zeenai.server.NullConfig;

public class StateCodec_TrapDoor implements BlockStateCodec, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Material[] getApplicableMaterials() {
        return new Material[] { 

        };
    }

    private List<Material> FriendlyList;

    public StateCodec_TrapDoor(){
        FriendlyList=new ArrayList<Material>();
        for (Material material : getApplicableMaterials()) {
            FriendlyList.add(material);
        }
    }

    private class TrapDoorData implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 189543875L;
        public String Half;
        public String Facing;
        public boolean Open;
        public boolean Powered;
        public boolean Waterlogged;

        public String AsString(){
            StringBuilder sb = new StringBuilder();
            sb.append("\nHalf: "+Half);
            sb.append("\nFacing: "+Facing);
            sb.append("\nOpen: "+Open);
            sb.append("\nPowered: "+Powered);
            sb.append("\nWaterlogged: "+Waterlogged);

            return sb.toString();
        }
    }

    @Override
    public String serialize(BlockState state) {
        if (FriendlyList.contains(state.getType())) {

            TrapDoor sig = (TrapDoor)state.getBlockData();
            
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            TrapDoorData dat = new TrapDoorData();

            dat.Facing=sig.getFacing().name();
            dat.Half = sig.getHalf().name();
            dat.Open=sig.isOpen();
            dat.Powered=sig.isPowered();
            dat.Waterlogged=sig.isWaterlogged();
            

            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(dat);
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
                
            String encoded = Base64Coder.encodeLines(baos.toByteArray());

                
            return encoded;
            
        }
        
        {
            if(FriendlyList.contains(state.getType())){
                File TMP=null;
                try {
                    TMP = File.createTempFile("state", "debug_trapdoor");
                    NullConfig.GetTempConfig("trapdoor_debug").set("state", state);
                    NullConfig.SaveTempConfig(TMP, "trapdoor_debug");
                    FileReader FR = new FileReader(TMP);
                    BufferedReader br = new BufferedReader(FR);
                    String contents = "";
                    String cur="";
                    while((cur = br.readLine())!=null){
                        contents+=cur+"\n";
                    }
                    Main.GetMainInstance().getLogger().info("ERROR\n\n: Below is the raw block data for trapdoor\n\n"+contents);

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
        Main.GetMainInstance().getLogger().info("Deserialize trapdoor called");
        if(FriendlyList.contains(state.getType())){
            Main.GetMainInstance().getLogger().info("Deserialize trapdoor called- is instanceof");
            TrapDoor dir = (TrapDoor)state.getBlockData();
            TrapDoorData dat = new TrapDoorData();

            ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(conf));
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);

                dat = (TrapDoorData)ois.readObject();
    
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
                dir.setHalf(Bisected.Half.valueOf(dat.Half));
                dir.setOpen(dat.Open);
                dir.setPowered(dat.Powered);
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
        return "Orientable";
    }
    
}