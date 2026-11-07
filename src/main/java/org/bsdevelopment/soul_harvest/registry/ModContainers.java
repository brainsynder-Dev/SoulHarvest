package org.bsdevelopment.soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModContainers {
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, ModConstants.MOD_ID);

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}
