package org.bsdevelopment.soul_harvest;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.bsdevelopment.soul_harvest.client.ModelHandler;
import org.bsdevelopment.soul_harvest.config.ModConfig;
import org.bsdevelopment.soul_harvest.listeners.*;
import org.bsdevelopment.soul_harvest.network.PacketHandler;
import org.bsdevelopment.soul_harvest.registry.*;
import org.bsdevelopment.soul_harvest.utilities.FluidStorageIntegration;
import org.slf4j.Logger;

@Mod(SoulHarvest.MOD_ID)
public class SoulHarvest {
    public static final String MOD_ID = "soul_harvest";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SoulHarvest(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(Type.COMMON, ModConfig.SPEC, ModConstants.CONFIG_DIR.resolve("config.toml").toString());

        ModComponents.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModFluids.register(modEventBus);
        ModParticles.register(modEventBus);
        ModContainers.register(modEventBus);

        modEventBus.addListener(FluidStorageIntegration::attachCapabilities);
        modEventBus.addListener(PacketHandler::registerNetworking);

        NeoForge.EVENT_BUS.register(new LiquidExperienceListener());
        NeoForge.EVENT_BUS.register(new SoulHarvestAltarListener());
        NeoForge.EVENT_BUS.register(new RiftAnchorListener());
        NeoForge.EVENT_BUS.register(new GravityAnchorListener());
        NeoForge.EVENT_BUS.register(new WraithfireListener());

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            ModelHandler.register(modEventBus);
        }
    }
}
