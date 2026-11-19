package org.bsdevelopment.soul_harvest.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.bsdevelopment.soul_harvest.blocks.entity.base.DataSaverBlockEntity;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.utilities.BlockTracker;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

public class HushedMaskBlockEntity extends DataSaverBlockEntity {
    public static final int MAX_SIZE = 5;
    private static final BlockTracker LOADED_MASKS = new BlockTracker();
    private final EnumSet<MuteType> mutedTypes = MuteType.getDefaultMutedTypes();
    private int width = MAX_SIZE;
    private int height = MAX_SIZE;
    private int depth = MAX_SIZE;

    public HushedMaskBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.HUSHED_MASK.getTileEntityType(), pos, blockState);
    }

    public static boolean isHushed(Entity entity, MuteType muteType) {
        if (!(entity instanceof Mob)) return false;

        Level level = entity.level();
        return LOADED_MASKS.anyWithin(level, entity.blockPosition(), MAX_SIZE - 1, maskPos -> (level.getBlockEntity(maskPos) instanceof HushedMaskBlockEntity mask) && mask.isMuted(muteType) && mask.getArea().contains(entity.position()));
    }

    public static void clearLevel(ResourceKey<Level> dimension) {
        LOADED_MASKS.clearLevel(dimension);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isMuted(MuteType muteType) {
        return mutedTypes.contains(muteType);
    }

    public void applySettings(int width, int height, int depth, Set<MuteType> mutedTypes) {
        this.width = validateSize(width);
        this.height = validateSize(height);
        this.depth = Mth.clamp(depth, 1, MAX_SIZE);
        this.mutedTypes.clear();
        this.mutedTypes.addAll(mutedTypes);
        setChanged();

        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    public AABB getArea() {
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        Direction across = facing.getAxis().isVertical() ? Direction.EAST : facing.getClockWise();
        Direction upDown = facing.getAxis().isVertical() ? Direction.SOUTH : Direction.UP;
        int halfWidth = (width - 1) / 2;
        int halfHeight = (height - 1) / 2;

        BlockPos firstCorner = worldPosition.relative(across, -halfWidth).relative(upDown, -halfHeight);
        BlockPos secondCorner = worldPosition.relative(facing, depth - 1).relative(across, halfWidth).relative(upDown, halfHeight);
        return AABB.encapsulatingFullBlocks(firstCorner, secondCorner);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (level != null) LOADED_MASKS.add(level, worldPosition);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (level != null) LOADED_MASKS.remove(level, worldPosition);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        width = validateSize(tag.getIntOr("width", MAX_SIZE));
        height = validateSize(tag.getIntOr("height", MAX_SIZE));
        depth = Mth.clamp(tag.getIntOr("depth", MAX_SIZE), 1, MAX_SIZE);
        for (MuteType muteType : MuteType.values()) {
            if (tag.getBooleanOr(muteType.getKey(), isMuted(muteType))) mutedTypes.add(muteType);
            else mutedTypes.remove(muteType);
        }
    }

    @Override
    public void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("width", width);
        tag.putInt("height", height);
        tag.putInt("depth", depth);
        for (MuteType muteType : MuteType.values()) tag.putBoolean(muteType.getKey(), isMuted(muteType));
    }

    private int validateSize(int size) {
        int clampedSize = Mth.clamp(size, 1, MAX_SIZE);
        return (clampedSize % 2 == 0) ? clampedSize + 1 : clampedSize;
    }

    public enum MuteType {
        IDLE, DEATH, PUFF, HURT;

        public static EnumSet<MuteType> getDefaultMutedTypes() {
            return EnumSet.noneOf(MuteType.class);
        }

        public String getKey() {
            return "mute_" + name().toLowerCase(Locale.ROOT);
        }
    }
}
