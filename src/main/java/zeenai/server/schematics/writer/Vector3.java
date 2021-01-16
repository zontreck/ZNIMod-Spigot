package zeenai.server.schematics.writer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import zeenai.server.Main;

public class Vector3 implements Comparable{
    double x, y, z;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector3(Location x){
        this.x = x.getX();
        this.y=x.getY();
        this.z=x.getZ();
    }

    public Vector3(double x, double y, double z) {
        this.x = Math.round(x);
        this.y = Math.round(y);
        this.z = Math.round(z);

        //Main.GetMainInstance().getLogger().info(ToString());
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
        return "<" + x + ", " + y + ", " + z + ">";
    }

    public void Destroy() {
        try {
            this.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public Location GetBukkitLocation(World w){
        return new Location(w, x,y,z);
    }

    public List<Vector3> Cube(Vector3 OtherPos){
        // This generates a vector3 cube. It'll return a list with all vectors in 1.0 increments for every vector within the two vectors
        List<Vector3> positions = new ArrayList<Vector3>();
        Vector3 Vx = new Vector3();

        double xx=this.x;
        double yy=this.y;
        double zz=this.z;

        int ystate=0;
        int zstate=0;
        int xstate=0;

        for ( xx=Math.round(x); (xx != Math.round(OtherPos.x) && xstate != 2); ){

            for (zz=Math.round(z); (zz != Math.round(OtherPos.z) && zstate != 2); ) {

                for (yy=Math.round(y); (yy != Math.round(OtherPos.y) && ystate != 2); ) {

                    
                    Vx = new Vector3(xx, yy, zz);
                    if(!positions.contains(Vx)) positions.add(Vx);
                    //Main.GetMainInstance().getLogger().info("YLOOP: "+Vx.ToString());

                    if(yy > OtherPos.y) {
                        yy-=1.0;
                        if(yy==Math.round(OtherPos.y) && ystate==0){
                            ystate++;
                        } else {
                            if(ystate==1)ystate++;
                        }
                    }
                    else if(yy < OtherPos.y){
                        yy+=1.0;
                        if(yy==Math.round(OtherPos.y) && ystate==0){
                            ystate++;
                        } else {
                            if(ystate==1)ystate++;
                        }
                    } 
                }

                ystate=0;

                Vx = new Vector3(xx, yy, zz);
                if(!positions.contains(Vx)) positions.add(Vx);
                //Main.GetMainInstance().getLogger().info("ZLOOP: "+Vx.ToString());

                if(zz > OtherPos.z) {
                    zz-=1.0;

                    if(zz == Math.round(OtherPos.z) && zstate==0){
                        zstate++;
                    }else{
                        if(zstate==1)zstate++;
                    }
                }
                else if(zz < OtherPos.z) {
                    zz+=1.0;
                    if(zz == Math.round(OtherPos.z) && zstate==0){
                        zstate++;
                    }else{
                        if(zstate==1)zstate++;
                    }
                }
            }

            zstate=0;

            Vx = new Vector3(xx, yy, zz);
            if(!positions.contains(Vx)) positions.add(Vx);
            //Main.GetMainInstance().getLogger().info("XLOOP: "+Vx.ToString());
            if(xx > OtherPos.x) {
                xx-=1.0;

                if(xx == Math.round(OtherPos.x) && xstate==0){
                    xstate++;
                }else{
                    if(xstate==1){
                        xstate++;
                    }
                }
            }
            else if(xx < OtherPos.x) {
                xx+=1.0;
                if(xx == Math.round(OtherPos.x) && xstate==0){
                    xstate++;
                }else{
                    if(xstate==1){
                        xstate++;
                    }
                }
            }
        }
        return positions;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Vector3){
            Vector3 v3 = (Vector3)o;
            if(Less(v3))return -1;
            if(Same(v3))return 0;
            if(Greater(v3))return 1;
        }
        return -2;
    }
    
}