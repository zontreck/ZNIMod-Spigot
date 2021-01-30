package zeenai.server.entitycodec;

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
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.util.EulerAngle;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import zeenai.server.Main;
import zeenai.server.NullConfig;
import zeenai.server.schematics.writer.Vector3;

public class ArmorStandCodec implements EntityCodec, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3959518457699988906L;

    @Override
    public EntityType[] getApplicableEntities() {
        return new EntityType[] { EntityType.ARMOR_STAND };
    }

    private List<EntityType> FriendlyList;

    public ArmorStandCodec() {
        FriendlyList = new ArrayList<EntityType>();
        for (EntityType material : getApplicableEntities()) {
            FriendlyList.add(material);
        }
    }

    class ArmorStandData implements Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        public Vector3 bodyPose;
        public Vector3 headPose;

        void setBodyPose(EulerAngle ang) {
            bodyPose = fromEuler(ang);
        }

        EulerAngle getBodyPose() {
            return toEuler(bodyPose);
        }

        Vector3 fromEuler(EulerAngle ang) {
            return new Vector3(ang.getX(), ang.getY(), ang.getZ());
        }

        EulerAngle toEuler(Vector3 vec) {
            return new EulerAngle(vec.getX(), vec.getY(), vec.getZ());
        }

        void setHeadPose(EulerAngle ang) {
            headPose = fromEuler(ang);
        }

        EulerAngle getHeadPose() {
            return toEuler(headPose);
        }

        public boolean hasArms;
        public Vector3 leftArmPose;
        public Vector3 leftLegPose;

        void setLeftArmPose(EulerAngle leftArm) {
            leftArmPose = fromEuler(leftArm);
        }

        EulerAngle getLeftArmPose() {
            return toEuler(leftArmPose);
        }

        void setLeftLegPose(EulerAngle leftLeg) {
            leftLegPose = fromEuler(leftLeg);
        }

        EulerAngle getLeftLegPose() {
            return toEuler(leftLegPose);
        }

        public Vector3 rightArmPose;
        public Vector3 rightLegPose;

        void setRightArmPose(EulerAngle ang) {
            rightArmPose = fromEuler(ang);
        }

        EulerAngle getRightArmPose() {
            return toEuler(rightArmPose);
        }

        void setRightLegPose(EulerAngle ang) {
            rightLegPose = fromEuler(ang);
        }

        EulerAngle getRightLegPose() {
            return toEuler(rightLegPose);
        }

        public boolean hasBasePlate;
        public boolean marker;
        public boolean small;
        public boolean visible;

        public String SerializedEquipment;

        public boolean AI;

        public float Yaw;
        public float Pitch;

        public boolean Gravity;

        public String AsString() {
            return "BodyPose: " + bodyPose.ToString() + "\nHeadPose: " + headPose.ToString() + "\nHas Arms: " + hasArms
                    + "\nLeft Leg Pose: " + leftLegPose.ToString() + "\nLeft Arm Pose: " + leftArmPose.ToString()
                    + "\nRight Arm Pose: " + rightArmPose.ToString() + "\nRight Leg Pose: " + rightLegPose.ToString()
                    + "\nHas Base Plate: " + hasBasePlate + "\nMarker: " + marker + "\nSmall: " + small + "\nVisible: "
                    + visible  + "\nSerialized Equipment: " + SerializedEquipment+"\nHas AI: "+AI+"\nYaw: "+Yaw+"\nPitch: "+Pitch+"\nGravity: "+Gravity;
        }

    }

    @Override
    public String serialize(Entity state) {
        if (FriendlyList.contains(state.getType())) {
            YamlConfiguration yml = new YamlConfiguration();

            ArmorStand stand = (ArmorStand) state;
            ArmorStandData dat = new ArmorStandData();
            dat.setBodyPose(stand.getBodyPose());
            dat.setHeadPose(stand.getHeadPose());
            dat.hasArms = stand.hasArms();
            EntityEquipment equip = stand.getEquipment();
            yml.set("boots", equip.getBoots());
            yml.set("chestplate", equip.getChestplate());
            yml.set("helmet", equip.getHelmet());
            yml.set("itemInMain", equip.getItemInMainHand());
            yml.set("itemInOff", equip.getItemInOffHand());
            dat.setLeftArmPose(stand.getLeftArmPose());
            dat.setLeftLegPose(stand.getLeftLegPose());
            yml.set("leggings", equip.getLeggings());
            dat.setRightArmPose(stand.getRightArmPose());
            dat.setRightLegPose(stand.getRightLegPose());
            dat.SerializedEquipment = yml.saveToString();
            dat.hasBasePlate = stand.hasBasePlate();
            dat.marker = stand.isMarker();
            dat.small = stand.isSmall();
            dat.visible = stand.isVisible();
            dat.AI=stand.hasAI();
            dat.Yaw = stand.getLocation().getYaw();
            dat.Pitch = stand.getLocation().getPitch();
            dat.Gravity=stand.hasGravity();

            /*
             * String direction= ""; Orientable sig = (Orientable)state.getBlockData();
             * direction = sig.getAxis().name();
             */

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // OrientableData dat = new OrientableData();

            // Set data fields here

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
            if (FriendlyList.contains(state.getType())) {
                File TMP = null;
                try {
                    TMP = File.createTempFile("state", "debug_armor_stand");
                    NullConfig.GetTempConfig("armor_stand_debug").set("state", state);
                    NullConfig.SaveTempConfig(TMP, "armor_stand_debug");
                    FileReader FR = new FileReader(TMP);
                    BufferedReader br = new BufferedReader(FR);
                    String contents = "";
                    String cur = "";
                    while ((cur = br.readLine()) != null) {
                        contents += cur + "\n";
                    }
                    Main.GetMainInstance().getLogger()
                            .info("ERROR\n\n: Below is the raw block data for orientable\n\n" + contents);

                    br.close();
                    FR.close();
                    TMP.delete();
                } catch (Exception e) {

                }
            }
        }
        return null;
    }

    @Override
    public void deserialize(Entity state, String conf) {

        Main.GetMainInstance().getLogger().info("Deserialize armor stand called");
        if (FriendlyList.contains(state.getType())) {
            Main.GetMainInstance().getLogger().info("Deserialize armor stand called- is instanceof");
            ArmorStand stand = (ArmorStand) state;
            ArmorStandData data = new ArmorStandData();

            ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(conf));
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(bais);

                data = (ArmorStandData) ois.readObject();

                ois.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            Main.GetMainInstance().getLogger().info("Deserialized!\n\n" + data.AsString());
            if (conf != null) {
                stand.setBodyPose(data.getBodyPose());
                stand.setHeadPose(data.getHeadPose());
                stand.setRightArmPose(data.getRightArmPose());
                stand.setRightLegPose(data.getRightLegPose());
                stand.setLeftArmPose(data.getLeftArmPose());
                stand.setLeftLegPose(data.getLeftLegPose());

                YamlConfiguration cfg = new YamlConfiguration();
                try {
                    cfg.loadFromString(data.SerializedEquipment);
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                //stand.teleport(data.Position.GetBukkitLocation(state.getLocation().getWorld()));
                stand.setArms(data.hasArms);
                EntityEquipment equip = stand.getEquipment();
                equip.setBoots(cfg.getItemStack("boots"));
                equip.setChestplate(cfg.getItemStack("chestplate"));
                equip.setHelmet(cfg.getItemStack("helmet"));
                equip.setItemInMainHand(cfg.getItemStack("itemInMain"));
                equip.setItemInOffHand(cfg.getItemStack("itemInOff"));
                equip.setLeggings(cfg.getItemStack("leggings"));
                stand.setMarker(data.marker);
                stand.setSmall(data.small);
                stand.setVisible(data.visible);
                stand.setAI(data.AI);
                stand.setBasePlate(data.hasBasePlate);

                stand.setRotation(data.Yaw, data.Pitch);
                stand.setGravity(data.Gravity);
                
                
            }
        }
    }

    @Override
    public String getID() {
        return "ARMOR_STAND_CODEC";
    }

    @Override
    public String toString(String conf) {
        return null;
    }
    
}
