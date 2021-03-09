package zeenai.server.schematics.writer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import zeenai.server.Main;

public class Vector3 implements ConfigurationSerializable, Comparable, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -3359581883765287486L;
    public double x, y, z;
    public float yaw, pitch;
    public String worldName;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public Vector3(Location x) {
        this.x = Math.round(x.getX());
        this.y = Math.round(x.getY());
        this.z = Math.round(x.getZ());
    }

    public Vector3(double x, double y, double z) {
        this.x = Math.round(x);
        this.y = Math.round(y);
        this.z = Math.round(z);

        // Main.GetMainInstance().getLogger().info(ToString());
    }

    public void LosePrecision() {
        this.x = Math.round(x);
        this.y = Math.round(y);
        this.z = Math.round(z);
        this.yaw = Math.round(yaw);
        this.pitch=Math.round(pitch);
    }

    public Vector3 Clone() {
        Vector3 copy = new Vector3();
        copy.x = x;
        copy.y = y;
        copy.z = z;
        copy.yaw = yaw;
        return copy;
    }

    public static Vector3 LosslessVector3(Location x) {
        Vector3 p = new Vector3();
        p.x = x.getX();
        p.y = x.getY();
        p.z = x.getZ();
        p.yaw = x.getYaw();
        p.pitch=x.getPitch();

        return p;
    }

    public Vector3() {
    }

    public Vector3 Add(Vector3 addThis) {
        x += addThis.x;
        y += addThis.y;
        z += addThis.z;

        return this;
    }

    public Vector3 Sub(Vector3 subThis) {
        x -= subThis.x;
        y -= subThis.y;
        z -= subThis.z;

        return this;
    }

    public boolean Greater(Vector3 check) {
        if ((this.x > check.x) || (this.x == check.x && this.y > check.y)
                || (this.x == check.x && this.y == check.y && this.z > check.z)) {
            return true;
        }

        return false;
    }

    public boolean Less(Vector3 check) {
        if ((this.x < check.x) || (this.x == check.x && this.y < check.y)
                || (this.x == check.x && this.y == check.y && this.z < check.z)) {
            return true;
        }

        return false;
    }

    public boolean Same(Vector3 check) {
        if ((this.x == check.x && this.y == check.y && this.z == check.z)) {
            return true;
        }

        return false;
    }

    public String ToString() {
        if(yaw>0.0||yaw<0.0){
            return "<"+x+", "+y+", "+z+"> y:"+yaw+"; p:"+pitch;
        }
        return "<" + x + ", " + y + ", " + z + ">";
    }

    public void Destroy() {
        try {
            this.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public Location GetBukkitLocation(World w) {
        worldName=w.getName();
        if(yaw >0.0 || yaw<0.0){
            Location L =new Location(w, x, y, z);
            L.setYaw(yaw);
            L.setPitch(pitch);
            return L;
            
        }
        return new Location(w, x, y, z);
    }

    public void ForEachCubed(Vector3 Target, Consumer<Vector3> callback)
    {
        Main.GetMainInstance().getLogger().info("Generating a cubed list and executing callback foreach!");
        Vector3 Vx = new Vector3();
        double xx = this.x;
        double yy = this.y;
        double zz = this.z;

        int ystate=0;
        int zstate=0;
        int xstate=0;

        for(xx = Math.round(x); (xx != Math.round(Target.x) && xstate!=2);){
            for(zz = Math.round(z); (zz != Math.round(Target.z) && zstate!=2);)
            {
                for(yy = Math.round(y); (yy != Math.round(Target.y) && ystate!=2);)
                {
                    Vx = new Vector3(xx,yy,zz);
                    callback.accept(Vx);
                    
                    if(yy > Target.y){
                        yy-=1.0;
                        if(yy==Math.round(Target.y) && ystate==0){
                            ystate++;
                        }else{
                            if(ystate==1)ystate++;
                        }
                    } else if(yy<Target.y){
                        yy+=1.0;
                        if(yy == Math.round(Target.y) && ystate==0){
                            ystate++;
                        }else{
                            if(ystate==1)ystate++;
                        }
                    }
                }

                ystate=0;

                Vx = new Vector3(xx,yy,zz);
                callback.accept(Vx);
                Vx.worldName=worldName;
                Vx.y = y;
                World www = Main.GetMainInstance().getServer().getWorld(Vx.worldName);
                Random rnd = new Random();
                www.playSound(Vx.GetBukkitLocation(www), Sound.ENTITY_ITEM_PICKUP , 1F,rnd.nextFloat() * 2);
                Vx.y=yy;
                
                if(zz>Target.z){
                    zz-=1.0;
                    if(zz == Math.round(Target.z) && zstate==0){
                        zstate++;
                    }else{
                        if(zstate==1)zstate++;
                    }
                }else if(zz<Target.z){
                    zz+=1.0;
                    if(zz == Math.round(Target.z) && zstate==0)zstate++;
                    else{
                        if(zstate==1)zstate++;
                    }
                }

            }

            zstate=0;
            Vx=new Vector3(xx,yy,zz);
            callback.accept(Vx);
            if(xx>Target.x){
                xx-=1.0;
                if(xx == Math.round(Target.x) && xstate==0)xstate++;
                else{
                    if(xstate==1)xstate++;
                }
            }else if(xx<Target.x){
                xx+=1.0;
                if(xx == Math.round(Target.x) && xstate==0)xstate++;
                else{
                    if(xstate==1)xstate++;
                }
            }
        }
    }

    public List<Vector3> Cube(Vector3 OtherPos) {
        // This generates a vector3 cube. It'll return a list with all vectors in 1.0
        // increments for every vector within the two vectors
        Main.GetMainInstance().getLogger().info("Beginning to generate cubed vector3 list");
        List<Vector3> positions = new ArrayList<Vector3>();
        Vector3 Vx = new Vector3();

        double xx = this.x;
        double yy = this.y;
        double zz = this.z;

        int ystate = 0;
        int zstate = 0;
        int xstate = 0;

        for (xx = Math.round(x); (xx != Math.round(OtherPos.x) && xstate != 2);) {
            Main.GetMainInstance().getLogger().info("X: "+xx+" / "+Math.round(OtherPos.x));

            for (zz = Math.round(z); (zz != Math.round(OtherPos.z) && zstate != 2);) {
                //Main.GetMainInstance().getLogger().info("Z: "+zz+" / "+Math.round(OtherPos.z));

                for (yy = Math.round(y); (yy != Math.round(OtherPos.y) && ystate != 2);) {

                    Vx = new Vector3(xx, yy, zz);
                    if (!positions.contains(Vx))
                        positions.add(Vx);
                    // Main.GetMainInstance().getLogger().info("YLOOP: "+Vx.ToString());

                    if (yy > OtherPos.y) {
                        yy -= 1.0;
                        if (yy == Math.round(OtherPos.y) && ystate == 0) {
                            ystate++;
                        } else {
                            if (ystate == 1)
                                ystate++;
                        }
                    } else if (yy < OtherPos.y) {
                        yy += 1.0;
                        if (yy == Math.round(OtherPos.y) && ystate == 0) {
                            ystate++;
                        } else {
                            if (ystate == 1)
                                ystate++;
                        }
                    }
                }

                ystate = 0;

                Vx = new Vector3(xx, yy, zz);
                Vx.worldName=worldName;
                World www = Main.GetMainInstance().getServer().getWorld(Vx.worldName);
                Random rnd = new Random();
                www.playSound(Vx.GetBukkitLocation(www), Sound.ENTITY_ITEM_PICKUP , 1F,rnd.nextFloat() * 2);
                if (!positions.contains(Vx))
                    positions.add(Vx);
                // Main.GetMainInstance().getLogger().info("ZLOOP: "+Vx.ToString());

                if (zz > OtherPos.z) {
                    zz -= 1.0;

                    if (zz == Math.round(OtherPos.z) && zstate == 0) {
                        zstate++;
                    } else {
                        if (zstate == 1)
                            zstate++;
                    }
                } else if (zz < OtherPos.z) {
                    zz += 1.0;
                    if (zz == Math.round(OtherPos.z) && zstate == 0) {
                        zstate++;
                    } else {
                        if (zstate == 1)
                            zstate++;
                    }
                }
            }

            zstate = 0;

            Vx = new Vector3(xx, yy, zz);
            if (!positions.contains(Vx))
                positions.add(Vx);
            // Main.GetMainInstance().getLogger().info("XLOOP: "+Vx.ToString());
            if (xx > OtherPos.x) {
                xx -= 1.0;

                if (xx == Math.round(OtherPos.x) && xstate == 0) {
                    xstate++;
                } else {
                    if (xstate == 1) {
                        xstate++;
                    }
                }
            } else if (xx < OtherPos.x) {
                xx += 1.0;
                if (xx == Math.round(OtherPos.x) && xstate == 0) {
                    xstate++;
                } else {
                    if (xstate == 1) {
                        xstate++;
                    }
                }
            }
        }

        Main.GetMainInstance().getLogger().info("Cubed list generated successfully");
        return positions;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Vector3) {
            Vector3 v3 = (Vector3) o;
            if (Less(v3))
                return -1;
            if (Same(v3))
                return 0;
            if (Greater(v3))
                return 1;
        }
        return -2;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> ret = new HashMap<String,Object>();
        ret.put("x", x);
        ret.put("y", y);
        ret.put("z", z);
        ret.put("yaw", yaw);
        ret.put("pitch", pitch);
        return ret;
    }

    public static Vector3 deserialize(Map<String, Object> re){
        Vector3 r = new Vector3();
        r.x = (double)re.get("x");
        r.y=(double)re.get("y");
        r.z = (double)re.get("z");
        double yaww = (double)re.get("yaw");
        String syaw = ""+yaww;
        r.yaw=Float.parseFloat(syaw);

        yaww=(double)re.get("pitch");
        syaw=""+yaww;
        r.pitch=Float.parseFloat(syaw);


        return r;
    }
    
}