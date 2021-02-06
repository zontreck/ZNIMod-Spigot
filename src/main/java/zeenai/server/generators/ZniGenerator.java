package zeenai.server.generators;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.PerlinNoiseGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import zeenai.server.Main;

public class ZniGenerator extends ChunkGenerator {
    public int currentHeight = 0;

    public ZniGenerator(String id){

    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        Main.GetMainInstance().getLogger().info("ZNIMod generator.. generating chunk!");
        ChunkData chunk = createChunkData(world);
        PerlinOctaveGenerator perlin = new PerlinOctaveGenerator(new Random(world.getSeed()), 8);
        
        perlin.setScale(0.005D);

//        octaves.setScale(0.0005D);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                boolean standard=true;
                switch(biome.getBiome(x, currentHeight, z)){
                    case PLAINS:
                    {
                        currentHeight = (int)((perlin.noise(chunkX * 16 + x, chunkZ * 16 + z, 0.5D)+1)*15D+30D);
                    }
                    case DESERT:{
                        standard=false;
                        currentHeight = (int)((perlin.noise(chunkX * 16 + x, chunkZ * 16 + z, 0.5D)+1)*15D+27D);

                        chunk.setBlock(x, currentHeight, z, Material.SAND);
                        chunk.setBlock(x, currentHeight-1, z, Material.SANDSTONE);
                    }
                    
                    default : {
                        
                        currentHeight = (int)((perlin.noise(chunkX * 16 + x, chunkZ * 16+z, 0.5D, 0.5D)+1)*15D+50D);
                    }
                }
//                currentHeight = (int) ((octaves.noise(chunkX * 16 + x, chunkZ * 16+z, 0.5D, 0.5D)+1)*15D+50D);
                if(standard){

                    chunk.setBlock(x, currentHeight, z, Material.GRASS_BLOCK);
                    chunk.setBlock(x, currentHeight-1, z, Material.DIRT);
                }
                for (int i=currentHeight-2; i> 0; i--){
                    chunk.setBlock(x, i, z, Material.STONE);


                }

                chunk.setBlock(x, 0, z, Material.BEDROCK);
            }
        }


        return chunk;
    }

    @Override
    public boolean shouldGenerateCaves(){
        return true;
    }


    @Override
    public boolean shouldGenerateDecorations() {
        return true;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return true;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world){
        return Arrays.asList((BlockPopulator)new Trees());
    }
}