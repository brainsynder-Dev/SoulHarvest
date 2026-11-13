package org.bsdevelopment.soul_harvest.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bsdevelopment.soul_harvest.blocks.SoulRitualShrineBlock;
import org.bsdevelopment.soul_harvest.blocks.entity.base.DataSaverBlockEntity;
import org.bsdevelopment.soul_harvest.registry.ModAttachments;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModParticles;

import java.util.UUID;

public class SoulRitualShrineBlockEntity extends DataSaverBlockEntity {
    private UUID targetId = null;
    private boolean targetHadNoAi;
    private boolean targetWasInvulnerable;
    private int ritualTicks;

    public SoulRitualShrineBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.SOUL_RITUAL_SHRINE.getTileEntityType(), pos, blockState);
    }

    public void startRitual(Mob mob) {
        targetId = mob.getUUID();
        targetHadNoAi = mob.isNoAi();
        targetWasInvulnerable = mob.isInvulnerable();
        ritualTicks = 0;
        mob.setNoAi(true);
        setChanged();
    }

    public void tickRitual(ServerLevel level) {
        if (targetId == null) return;

        if (!(level.getEntity(targetId) instanceof Mob mob) || !SoulRitualShrineBlock.isOnShrine(mob, worldPosition)) {
            stopRitual(level);
            return;
        }

        ritualTicks++;
        mob.setData(ModAttachments.RITUAL_TICKS, ritualTicks);
        if (ritualTicks == 56) mob.setInvulnerable(true);

        double lift = Math.clamp((ritualTicks - 24) / 16.0, 0, 1) * 0.25;
        mob.teleportTo(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5 + lift, worldPosition.getZ() + 0.5);
        mob.setDeltaMovement(Vec3.ZERO);

        double chestY = mob.getY() + mob.getBbHeight() * 0.6;
        for (int rune = 0; rune < 2; rune++) {
            double runeX = worldPosition.getX() + level.getRandom().nextInt(2);
            double runeY = worldPosition.getY() + 0.25;
            double runeZ = worldPosition.getZ() + level.getRandom().nextInt(2);
            level.sendParticles(ModParticles.BINDING_RUNE.get(), runeX, runeY, runeZ, 0, mob.getX() - runeX, chestY - runeY, mob.getZ() - runeZ, 1);
        }

        double centerY = mob.getY() + mob.getBbHeight() * 0.5;
        if (ritualTicks == 72) {
            level.sendParticles(ModParticles.BINDING_FLASH.get(), mob.getX(), centerY, mob.getZ(), 0, Math.max(mob.getBbWidth(), mob.getBbHeight()), 0, 0, 1);
            for (int puff = 0; puff < 12; puff++) {
                float angle = puff * Mth.TWO_PI / 12;
                level.sendParticles(ParticleTypes.END_ROD, mob.getX(), centerY, mob.getZ(), 0, Mth.cos(angle), 0.1, Mth.sin(angle), 0.12);
            }
        }

        if (ritualTicks < 80) return;


        SpawnEggItem.byId(mob.getType()).ifPresent(spawnEgg -> {
            ItemEntity eggEntity = new ItemEntity(level, mob.getX(), centerY, mob.getZ(), new ItemStack(spawnEgg), 0, 0, 0);
            eggEntity.setDefaultPickUpDelay();
            level.addFreshEntity(eggEntity);
        });
        level.playSound(null, worldPosition, SoundEvents.ENDER_EYE_DEATH, SoundSource.BLOCKS, 1f, 1.2f);
        mob.discard();
        targetId = null;
        finishRitual(level);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel serverLevel) releaseTarget(serverLevel);
    }

    @Override
    public void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        targetId = tag.read("ritual_target", UUIDUtil.CODEC).orElse(null);
        targetHadNoAi = tag.getBooleanOr("target_had_no_ai", false);
        targetWasInvulnerable = tag.getBooleanOr("target_was_invulnerable", false);
        ritualTicks = tag.getIntOr("ritual_ticks", 0);
    }

    @Override
    public void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        if (targetId == null) return;

        tag.store("ritual_target", UUIDUtil.CODEC, targetId);
        tag.putBoolean("target_had_no_ai", targetHadNoAi);
        tag.putBoolean("target_was_invulnerable", targetWasInvulnerable);
        tag.putInt("ritual_ticks", ritualTicks);
    }

    private void stopRitual(ServerLevel level) {
        releaseTarget(level);
        finishRitual(level);
    }

    private void releaseTarget(ServerLevel level) {
        if (targetId == null) return;

        if (level.getEntity(targetId) instanceof Mob mob) {
            mob.setNoAi(targetHadNoAi);
            mob.setInvulnerable(targetWasInvulnerable);
            mob.removeData(ModAttachments.RITUAL_TICKS);
        }

        targetId = null;
    }

    private void finishRitual(ServerLevel level) {
        ritualTicks = 0;
        setChanged();
        if (getBlockState().getValue(SoulRitualShrineBlock.ACTIVE)) level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SoulRitualShrineBlock.ACTIVE, false));
    }
}
