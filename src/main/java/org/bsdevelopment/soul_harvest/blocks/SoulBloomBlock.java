package org.bsdevelopment.soul_harvest.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.bsdevelopment.soul_harvest.blocks.base.SpawnPadBlock;
import org.bsdevelopment.soul_harvest.config.MobSpawnConfig;
import org.bsdevelopment.soul_harvest.config.ModConfig;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public class SoulBloomBlock extends SpawnPadBlock {
    public SoulBloomBlock(Properties properties) {
        super(properties, "bloom_sigil");
    }

    public static boolean onlyPassives(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entityType) {
        return entityType.getCategory() == MobCategory.CREATURE;
    }

    @Override
    protected MobCategory getSpawnCategory() {
        return MobCategory.CREATURE;
    }

    @Override
    protected boolean isSpawnLightValid(int brightness) {
        return brightness >= 7;
    }

    @Override
    protected boolean useBiomeSpawns() {
        return ModConfig.soulBloomBiomeSpawns;
    }

    @Override
    protected int getBaseSpawnInterval() {
        return ModConfig.soulBloomSpawnInterval;
    }

    @Override
    protected int getBaseMaxEntities() {
        return ModConfig.soulBloomMaxEntities;
    }

    @Override
    protected boolean allowRedstoneUpdates() {
        return ModConfig.soulBloomRedstoneUpdates;
    }

    @Override
    protected MobSpawnConfig getSpawnConfig() {
        return ModConstants.SOUL_BLOOM_SPAWNS;
    }
}
