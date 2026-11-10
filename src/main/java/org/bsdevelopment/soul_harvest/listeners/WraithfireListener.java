package org.bsdevelopment.soul_harvest.listeners;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.bsdevelopment.soul_harvest.registry.ModAttachments;

public class WraithfireListener {
    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide() || entity.getRemainingFireTicks() > 0 || !entity.hasData(ModAttachments.WRAITHFIRE)) return;

        entity.removeData(ModAttachments.WRAITHFIRE);
    }
}
