package org.bsdevelopment.soul_harvest.utilities;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;

public class FluidStorageIntegration {
    public static void attachCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlocks.WISDOM_VESSEL.getTileEntityType(), (blockEntity, direction) -> blockEntity.getTank());
    }
}
