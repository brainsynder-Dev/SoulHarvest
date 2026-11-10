package org.bsdevelopment.soul_harvest.utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class BlockTracker {
    private final Map<ResourceKey<Level>, Set<BlockPos>> trackedPositions = new HashMap<>();

    public void add(Level level, BlockPos pos) {
        if (level.isClientSide()) return;
        trackedPositions.computeIfAbsent(level.dimension(), dimension -> new HashSet<>()).add(pos);
    }

    public void remove(Level level, BlockPos pos) {
        if (level.isClientSide()) return;

        Set<BlockPos> positions = trackedPositions.get(level.dimension());
        if (positions == null) return;

        positions.remove(pos);
        if (positions.isEmpty()) trackedPositions.remove(level.dimension());
    }

    public void clearLevel(ResourceKey<Level> dimension) {
        trackedPositions.remove(dimension);
    }

    public boolean anyWithin(Level level, BlockPos center, int range, Predicate<BlockPos> filter) {
        Set<BlockPos> positions = trackedPositions.get(level.dimension());
        if (positions == null) return false;

        for (BlockPos pos : positions) {
            if (Math.abs(pos.getX() - center.getX()) > range || Math.abs(pos.getY() - center.getY()) > range
                    || Math.abs(pos.getZ() - center.getZ()) > range) continue;
            if (filter.test(pos)) return true;
        }
        return false;
    }
}
