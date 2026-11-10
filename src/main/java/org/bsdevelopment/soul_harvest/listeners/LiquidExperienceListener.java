package org.bsdevelopment.soul_harvest.listeners;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.bsdevelopment.soul_harvest.registry.ModFluids;
import org.bsdevelopment.soul_harvest.registry.ModParticles;

public class LiquidExperienceListener {
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        if (player.tickCount % 5 != 0 || !player.isInFluidType(ModFluids.LIQUID_EXPERIENCE_TYPE.get())) return;

        serverLevel.sendParticles(ModParticles.XP_SPARKLE.get(), player.getX(), player.getY() + player.getBbHeight() * 0.5, player.getZ(), 2, player.getBbWidth() * 0.5, player.getBbHeight() * 0.3, player.getBbWidth() * 0.5, 0.01);
    }
}
