package org.bsdevelopment.soul_harvest.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.bsdevelopment.soul_harvest.blocks.base.SpawnPadBlock;
import org.bsdevelopment.soul_harvest.config.MobSpawnConfig;
import org.bsdevelopment.soul_harvest.config.ModConfig;
import org.bsdevelopment.soul_harvest.registry.ModConstants;
import org.bsdevelopment.soul_harvest.registry.ModParticles;

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

    @Override
    protected void playSpawnEffect(ServerLevel level, Entity spawnedMob) {
        double x = spawnedMob.getX();
        double y = spawnedMob.getY() + spawnedMob.getBbHeight() * 0.5;
        double z = spawnedMob.getZ();
        RandomSource random = level.getRandom();

        level.sendParticles(ModParticles.BLOOM_BURST.get(), x, y, z, 1, 0, 0, 0, 0);
        for (int petal = 0; petal < 8; petal++) {
            double angle = random.nextDouble() * Mth.TWO_PI;
            double speed = 0.15 + random.nextDouble() * 0.15;
            level.sendParticles(ModParticles.BLOOM_PETAL.get(), x + Math.cos(angle) * 0.5, y + random.nextDouble() * 0.5, z + Math.sin(angle) * 0.5, 0, Math.cos(angle) * speed, 0.1 + random.nextDouble() * 0.15, Math.sin(angle) * speed, 1);
        }
    }
}
