package zeenai.server.generators.zni;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class Trees extends BlockPopulator {

    @Override
    public void populate( World world,  Random random,  Chunk source)
    {
        if(random.nextBoolean()){
            int amount = random.nextInt(4)+1;

            for (int i = 1; i < amount; i++) {
                int X = random.nextInt(15);
                int Z = random.nextInt(15);
                int Y;

                for(Y = world.getMaxHeight()-1; source.getBlock(X,Y,Z).getType() == Material.AIR; Y--);

                world.generateTree(source.getBlock(X,Y,Z).getLocation(), TreeType.TREE);
            }
        }
    }
    
}