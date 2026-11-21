package org.bsdevelopment.soul_harvest;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.bsdevelopment.soul_harvest.client.render.HushAreaRenderer;
import org.bsdevelopment.soul_harvest.client.screen.FilterScreen;
import org.bsdevelopment.soul_harvest.client.screen.FluidTooltipRenderer;
import org.bsdevelopment.soul_harvest.client.screen.SoulHarvestAltarScreen;
import org.bsdevelopment.soul_harvest.client.screen.LootExtractorScreen;
import org.bsdevelopment.soul_harvest.client.screen.WisdomVesselScreen;
import org.bsdevelopment.soul_harvest.items.WisdomVesselItem;
import org.bsdevelopment.soul_harvest.registry.ModContainers;

@Mod(value = SoulHarvest.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = SoulHarvest.MOD_ID, value = Dist.CLIENT)
public class SoulHarvestClient {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModContainers.SOUL_HARVEST_ALTAR.get(), SoulHarvestAltarScreen::new);
        event.register(ModContainers.LOOT_EXTRACTOR.get(), LootExtractorScreen::new);
        event.register(ModContainers.FILTER.get(), FilterScreen::new);
        event.register(ModContainers.WISDOM_VESSEL.get(), WisdomVesselScreen::new);
    }

    @SubscribeEvent
    public static void registerTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(WisdomVesselItem.FluidTooltip.class, FluidTooltipRenderer::new);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        HushAreaRenderer.drawShownAreas();
    }
}
