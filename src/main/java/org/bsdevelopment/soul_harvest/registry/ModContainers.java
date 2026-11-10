package org.bsdevelopment.soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.bsdevelopment.soul_harvest.container.FilterContainer;
import org.bsdevelopment.soul_harvest.container.SoulHarvestAltarContainer;
import org.bsdevelopment.soul_harvest.container.LootExtractorContainer;
import org.bsdevelopment.soul_harvest.container.WisdomVesselContainer;

public class ModContainers {
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, ModConstants.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<FilterContainer>> FILTER = CONTAINERS.register("filter", () -> IMenuTypeExtension.create(FilterContainer::new));
    public static final DeferredHolder<MenuType<?>, MenuType<SoulHarvestAltarContainer>> SOUL_HARVEST_ALTAR = CONTAINERS.register("soul_harvest_altar", () -> IMenuTypeExtension.create(SoulHarvestAltarContainer::new));
    public static final DeferredHolder<MenuType<?>, MenuType<LootExtractorContainer>> LOOT_EXTRACTOR = CONTAINERS.register("loot_extractor", () -> IMenuTypeExtension.create(LootExtractorContainer::new));
    public static final DeferredHolder<MenuType<?>, MenuType<WisdomVesselContainer>> WISDOM_VESSEL = CONTAINERS.register("wisdom_vessel", () -> IMenuTypeExtension.create(WisdomVesselContainer::new));

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}
