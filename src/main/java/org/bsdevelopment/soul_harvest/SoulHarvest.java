package org.bsdevelopment.soul_harvest;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(SoulHarvest.MOD_ID)
public class SoulHarvest {
    public static final String MOD_ID = "soul_harvest";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SoulHarvest(IEventBus modEventBus, ModContainer modContainer) {
    }
}
