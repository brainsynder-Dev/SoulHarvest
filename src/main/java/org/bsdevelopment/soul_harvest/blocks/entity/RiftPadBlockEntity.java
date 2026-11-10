package org.bsdevelopment.soul_harvest.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bsdevelopment.soul_harvest.blocks.RiftPadBlock;
import org.bsdevelopment.soul_harvest.blocks.base.DirectionalPlateBlock;
import org.bsdevelopment.soul_harvest.blocks.entity.base.DataSaverBlockEntity;
import org.bsdevelopment.soul_harvest.blocks.entity.base.ILinkable;
import org.bsdevelopment.soul_harvest.config.ModConfig;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RiftPadBlockEntity extends DataSaverBlockEntity implements ILinkable {
    private static final Map<GlobalPos, Long> LAST_SOUND_TIMES = new HashMap<>();
    private BlockPos destinationPos = null;

    public RiftPadBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.RIFT_PAD.getTileEntityType(), pos, blockState);
    }

    public void teleportEntity(ServerLevel level, Entity entity) {
        if (destinationPos == null || entity.isOnPortalCooldown()) return;

        BlockState destinationState = level.getBlockState(destinationPos);
        if (!destinationState.is(ModBlocks.DESTINATION_PAD.getBlock())) {
            linkPosition(null);
            return;
        }

        float yaw = destinationState.getValue(DirectionalPlateBlock.FACING).toYRot();
        entity.teleportTo(level, destinationPos.getX() + 0.5, destinationPos.getY() + 0.0625, destinationPos.getZ() + 0.5, Set.of(), yaw, entity.getXRot(), true);
        entity.setPortalCooldown(10);
        level.gameEvent(GameEvent.TELEPORT, destinationPos, GameEvent.Context.of(entity));
        playTeleportSound(level);
    }

    @Override
    public void linkPosition(BlockPos pos) {
        destinationPos = pos;
        setChanged();
        if (level != null) level.setBlockAndUpdate(worldPosition, getBlockState().setValue(RiftPadBlock.LINKED, pos != null));
    }

    @Override
    public boolean isLinked() {
        return destinationPos != null;
    }

    @Override
    public void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        if (!tag.contains("bound_destination")) return;

        CompoundTag compound = tag.getCompoundOrEmpty("bound_destination");
        destinationPos = new BlockPos(compound.getIntOr("x", 0), compound.getIntOr("y", 0), compound.getIntOr("z", 0));
    }

    @Override
    public void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        if (destinationPos == null) return;

        CompoundTag compound = new CompoundTag();
        compound.putInt("x", destinationPos.getX());
        compound.putInt("y", destinationPos.getY());
        compound.putInt("z", destinationPos.getZ());
        tag.put("bound_destination", compound);
    }

    private void playTeleportSound(ServerLevel level) {
        if (ModConfig.riftPadSoundVolume <= 0) return;

        long gameTime = level.getGameTime();
        GlobalPos destination = GlobalPos.of(level.dimension(), destinationPos);
        LAST_SOUND_TIMES.values().removeIf(lastSoundTime -> gameTime - lastSoundTime >= ModConfig.riftPadSoundCooldown || lastSoundTime > gameTime);
        if (LAST_SOUND_TIMES.containsKey(destination)) return;

        LAST_SOUND_TIMES.put(destination, gameTime);
        level.playSound(null, destinationPos, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, ModConfig.riftPadSoundVolume, 0.5f);
    }
}
