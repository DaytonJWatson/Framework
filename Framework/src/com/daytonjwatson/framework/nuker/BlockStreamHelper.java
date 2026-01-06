package com.daytonjwatson.framework.nuker;

import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

public final class BlockStreamHelper {

    private BlockStreamHelper() {
    }

    public static Stream<Block> cube(World world, int centerX, int centerY, int centerZ, int radius, int minY, int maxY) {
        Builder<Block> builder = Stream.builder();
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    builder.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return builder.build();
    }
}
