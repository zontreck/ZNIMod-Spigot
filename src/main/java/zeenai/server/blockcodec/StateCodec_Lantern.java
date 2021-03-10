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
import org.bukkit.block.data.type.Lantern;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import zeenai.server.Main;
import zeenai.server.NullConfig;

public class StateCodec_Lantern implements BlockStateCodec, Serializable
{

    /**
     *
     */
    private static final long serialVersionUID = 8079147004542252651L;

    @Override
    public Material[] getApplicableMaterials() {
        return new Material[] {
            Material.LANTERN, Material.SOUL_LANTERN
        };
    }
    private List<Material> FriendlyList;

    public StateCodec_Lantern(){
        FriendlyList=new ArrayList<Material>();
        for (Material material : getApplicableMaterials()) {
            FriendlyList.add(material);
        }
    }

    private class LanternData implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 189543875L;
        public boolean Hanging;
        public boolean Waterlogged;
        

        public String AsString(){
            StringBuilder sb = new StringBuilder();
            sb.append("Is Hanging: "+Hanging);
            sb.append("Waterlogged: "+Waterlogged);


            return sb.toString();
        }
    }



    @Override
    public String serialize(BlockState state) {
        if (FriendlyList.contains(state.getType())) {

            Lantern sig = (Lantern)state.getBlockData();
            

            
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            LanternData dat = new LanternData();

            dat.Hanging = sig.isHanging();
            dat.Waterlogged = sig.isWaterlogged();
            

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
                    TMP = File.createTempFile("state", "debug_lantern");
                    NullConfig.GetTempConfig("lantern_debug").set("state", state);
                    NullConfig.SaveTempConfig(TMP, "lantern_debug");
                    FileReader FR = new FileReader(TMP);
                    BufferedReader br = new BufferedReader(FR);
                    String contents = "";
                    String cur="";
                    while((cur = br.readLine())!=null){
                        contents+=cur+"\n";
                    }
                    Main.GetMainInstance().getLogger().info("ERROR\n\n: Below is the raw block data for lantern\n\n"+contents);

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
        Main.GetMainInstance().getLogger().info("Deserialize lantern called");
        if(FriendlyList.contains(state.getType())){
            Main.GetMainInstance().getLogger().info("Deserialize lantern called- is instanceof");
            Lantern dir = (Lantern)state.getBlockData();
            LanternData dat = new LanternData();

            ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(conf));
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);

                dat = (LanternData)ois.readObject();
    
                ois.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            Main.GetMainInstance().getLogger().info("Deserialized!\n\n"+dat.AsString());
            if(conf != null) {
                dir.setHanging(dat.Hanging);
                dir.setWaterlogged(dat.Waterlogged);
                state.setBlockData(dir);


                
            }
        }
    }

    @Override
    public String getID() {
        return "Lantern";
    }

    @Override
    public String toString(String conf) {
        return null;
    }
    
}
