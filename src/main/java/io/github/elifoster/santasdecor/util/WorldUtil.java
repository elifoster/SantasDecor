package io.github.elifoster.santasdecor.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.Chunk;

public class WorldUtil {
    /**
     * Thread-safe world mutation-safe version of getTileEntity. This should be used in methods which might be called
     * on alternative threads, namely getActualState and getExtendedState.
     * @param world The world
     * @param pos The position
     * @return The tile entity in the position
     */
    public static TileEntity getTileEntitySafely(IBlockAccess world, BlockPos pos) {
        return world instanceof ChunkCache ? ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
    }
}
