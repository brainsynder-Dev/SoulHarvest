package org.bsdevelopment.soul_harvest.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bsdevelopment.soul_harvest.blocks.base.RedstonePoweredBlock;
import org.bsdevelopment.soul_harvest.config.ModConfig;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.utilities.BlockTracker;

public class GravityAnchorBlockEntity extends BlockEntity {
    private static final BlockTracker ACTIVE_ANCHORS = new BlockTracker();

    public GravityAnchorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.GRAVITY_ANCHOR.getTileEntityType(), pos, blockState);
    }

    public static boolean isWithinActiveAnchor(Level level, BlockPos entityPos) {
        return ACTIVE_ANCHORS.anyWithin(level, entityPos, ModConfig.gravityAnchorRange, anchorPos -> {
            BlockState anchorState = level.getBlockState(anchorPos);
            return anchorState.is(ModBlocks.GRAVITY_ANCHOR.getBlock()) && !anchorState.getValue(RedstonePoweredBlock.POWERED);
        });
    }

    public static void clearLevel(ResourceKey<Level> dimension) {
        ACTIVE_ANCHORS.clearLevel(dimension);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (level != null) ACTIVE_ANCHORS.add(level, worldPosition);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (level != null) ACTIVE_ANCHORS.remove(level, worldPosition);
    }
}
