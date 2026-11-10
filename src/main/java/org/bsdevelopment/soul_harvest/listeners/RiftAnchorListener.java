package org.bsdevelopment.soul_harvest.listeners;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.bsdevelopment.soul_harvest.blocks.entity.RiftAnchorBlockEntity;

public class RiftAnchorListener {
    @SubscribeEvent
    public void onEnderTeleport(EntityTeleportEvent.EnderEntity event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof Player || entity.level().isClientSide()) return;
        if (RiftAnchorBlockEntity.isTeleportBlocked(entity.level(), entity.blockPosition())) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level && !level.isClientSide()) RiftAnchorBlockEntity.clearLevel(level.dimension());
    }
}
