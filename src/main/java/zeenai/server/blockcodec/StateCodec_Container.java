package zeenai.server.blockcodec;

import org.bukkit.Axis;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Chest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import zeenai.server.Main;
import zeenai.server.NullConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StateCodec_Container implements BlockStateCodec, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Material[] getApplicableMaterials() {
        return new Material[] { Material.CHEST, Material.TRAPPED_CHEST, Material.HOPPER, Material.CHEST_MINECART, Material.HOPPER_MINECART, Material.SHULKER_BOX,
                Material.DROPPER, Material.DISPENSER,
                Material.BARREL
        };
    }

    private List<Material> FriendlyList;

    public StateCodec_Container(){
        FriendlyList=new ArrayList<Material>();
        for (Material material : getApplicableMaterials()) {
            FriendlyList.add(material);
        }
    }

    class ContainerData implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 189543875L;
        public String Contents;
        public boolean orient;
        public String orientation;

        public boolean rotat;
        public String rot;

        public boolean dirr;
        public String facing;

        public boolean doubleChest;
        public boolean left;

        public boolean hasName;
        public String customName;



        public String AsString(){
            StringBuilder sb = new StringBuilder();
            sb.append("Container Data "+Contents+"\nOrientable: "+orient+" - "+orientation+"\nRotatable: "+rotat+" - "+rot+"\nDirectional: "+dirr+" - "+facing+
                    "\nDoubleChest: "+doubleChest+"\n>Left: "+left+"\nHas Custom Name: "+hasName+"\nCustom Name: "+customName);

            return sb.toString();
        }
    }

    @Override
    public String serialize(BlockState state) {
        if (FriendlyList.contains(state.getType())) {
            
            Container cn = (Container)state;
            BlockData _data = state.getBlockData();

            ContainerData dat = new ContainerData();
            Inventory inv = cn.getInventory();
            List<ItemStack> its = new ArrayList<>();
            for (ItemStack itsx : inv.getContents()
                 ) {
                if(itsx!=null){
                    its.add(new ItemStack(itsx));
                }
            }

            YamlConfiguration yml = new YamlConfiguration();
            yml.set("contents", its);
            dat.Contents=yml.saveToString();
            if(_data instanceof Orientable){
                dat.orient=true;
                Orientable ori = (Orientable)_data;
                dat.orientation= ori.getAxis().name();
            }

            if(_data instanceof Rotatable){
                dat.rotat=true;
                Rotatable roo = (Rotatable) _data;
                dat.rot=roo.getRotation().name();
            }

            if(_data instanceof Directional){
                dat.dirr=true;
                Directional dirr = (Directional) _data;
                dat.facing = dirr.getFacing().name();
            }
            //org.bukkit.block.Chest nch = (org.bukkit.block.Chest)_data;
            //{
            //    dat.hasName=true;
            //    dat.customName=nch.getCustomName();
            //}

            if(_data instanceof Chest)
            {
                Chest chh = (Chest)_data;
                if(chh.getType() != Chest.Type.SINGLE) {
                    dat.doubleChest = true;
                    if (chh.getType() == Chest.Type.LEFT) {
                        dat.left = true;
                    } else dat.left = false;
                }
            }




            ByteArrayOutputStream baos = new ByteArrayOutputStream();


            try
            {
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
                    TMP = File.createTempFile("state", "debug_container");
                    NullConfig.GetTempConfig("container_debug").set("state", state);
                    NullConfig.SaveTempConfig(TMP, "container_debug");
                    FileReader FR = new FileReader(TMP);
                    BufferedReader br = new BufferedReader(FR);
                    String contents = "";
                    String cur="";
                    while((cur = br.readLine())!=null){
                        contents+=cur+"\n";
                    }
                    Main.GetMainInstance().getLogger().info("ERROR\n\n: Below is the raw block data for container\n\n"+contents);

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
        Main.GetMainInstance().getLogger().info("Deserialize container called");
        if(FriendlyList.contains(state.getType())){
            Main.GetMainInstance().getLogger().info("Deserialize container called- is instanceof");
            
            Container bn = (Container)state;
            ContainerData dat = new ContainerData();

            ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(conf));
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);

                dat = (ContainerData)ois.readObject();
    
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
                YamlConfiguration yml = new YamlConfiguration();
                try {
                    yml.loadFromString(dat.Contents);
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }


                if(dat.orient){
                    Orientable orii = (Orientable) state.getBlockData();
                    orii.setAxis(Axis.valueOf(dat.orientation));
                    bn.setBlockData(orii);
                }

                if(dat.rotat){
                    Rotatable roo = (Rotatable) state.getBlockData();
                    roo.setRotation(BlockFace.valueOf(dat.rot));
                    bn.setBlockData(roo);
                }

                if(dat.dirr){
                    Directional drr = (Directional) state.getBlockData();
                    drr.setFacing(BlockFace.valueOf(dat.facing));
                    bn.setBlockData(drr);
                }

                if(dat.hasName){
                    //org.bukkit.block.Chest nch = (org.bukkit.block.Chest) state.getBlockData();
                    //nch.setCustomName(dat.customName);
                }

                if(dat.doubleChest)
                {
                    Chest chh = (Chest)state.getBlockData();
                    if(dat.left)chh.setType(Chest.Type.LEFT);
                    else chh.setType(Chest.Type.RIGHT);
                    bn.setBlockData(chh);
                }



                List<ItemStack> itx = (List<ItemStack>)yml.getList("contents");
                ItemStack[] iix = (ItemStack[])itx.toArray();
                Main.GetMainInstance().containerRestore.put(state.getLocation(), iix);
                /*
                for (ItemStack its: itx
                     ) {
                    if(its!=null){
                        bn.getInventory().addItem(new ItemStack(its));
                    }
                }
                bn.update();
                */


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