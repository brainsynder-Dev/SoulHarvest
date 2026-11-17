package org.bsdevelopment.soul_harvest.listeners;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.bsdevelopment.soul_harvest.blocks.entity.HushedMaskBlockEntity;

public class HushedMaskListener {
    @SubscribeEvent
    public void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level && !level.isClientSide()) HushedMaskBlockEntity.clearLevel(level.dimension());
    }
}
