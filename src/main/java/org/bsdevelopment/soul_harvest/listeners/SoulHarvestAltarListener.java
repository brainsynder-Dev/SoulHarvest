package org.bsdevelopment.soul_harvest.listeners;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import org.bsdevelopment.soul_harvest.blocks.entity.SoulHarvestAltarBlockEntity;
import org.bsdevelopment.soul_harvest.utilities.fakeplayer.CustomFakePlayer;

public class SoulHarvestAltarListener {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDrops(LivingDropsEvent event) {
        SoulHarvestAltarBlockEntity altar = getAltar(event.getEntity());
        if (altar == null) return;

        altar.collectDrops(event.getEntity(), event.getDrops());
        event.getDrops().clear();
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onExperienceDrop(LivingExperienceDropEvent event) {
        SoulHarvestAltarBlockEntity altar = getAltar(event.getEntity());
        if (altar == null) return;

        altar.collectExperience(event.getDroppedExperience());
        event.setCanceled(true);
    }

    private SoulHarvestAltarBlockEntity getAltar(LivingEntity entity) {
        if (entity.level().isClientSide()) return null;
        if (!(entity.getLastHurtByPlayer() instanceof CustomFakePlayer fakePlayer)) return null;
        if (!(entity.level().getBlockEntity(fakePlayer.getOwnerPos()) instanceof SoulHarvestAltarBlockEntity altar)) return null;

        return altar;
    }
}
