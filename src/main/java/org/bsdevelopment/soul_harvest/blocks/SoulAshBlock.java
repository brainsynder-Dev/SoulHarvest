package org.bsdevelopment.soul_harvest.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.bsdevelopment.soul_harvest.blocks.base.SpawnPadBlock;
import org.bsdevelopment.soul_harvest.config.MobSpawnConfig;
import org.bsdevelopment.soul_harvest.config.ModConfig;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public class SoulAshBlock extends SpawnPadBlock {
    public SoulAshBlock(Properties properties) {
        super(properties, "sigil");
    }

    public static boolean onlyHostiles(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entityType) {
        return entityType.getCategory() == MobCategory.MONSTER;
    }

    @Override
    protected MobCategory getSpawnCategory() {
        return MobCategory.MONSTER;
    }

    @Override
    protected boolean isSpawnLightValid(int brightness) {
        return brightness == 0;
    }

    @Override
    protected boolean useBiomeSpawns() {
        return ModConfig.soulAshBiomeSpawns;
    }

    @Override
    protected int getBaseSpawnInterval() {
        return ModConfig.soulAshSpawnInterval;
    }

    @Override
    protected int getBaseMaxEntities() {
        return ModConfig.soulAshMaxEntities;
    }

    @Override
    protected boolean allowRedstoneUpdates() {
        return ModConfig.soulAshRedstoneUpdates;
    }

    @Override
    protected MobSpawnConfig getSpawnConfig() {
        return ModConstants.SOUL_ASH_SPAWNS;
    }

    @Override
    protected void playSpawnEffect(ServerLevel level, Entity spawnedMob) {
        level.sendParticles(ParticleTypes.SOUL, spawnedMob.getX(), spawnedMob.getY() + 0.5, spawnedMob.getZ(), 12, 0.3, 0.5, 0.3, 0.02);
    }
}
