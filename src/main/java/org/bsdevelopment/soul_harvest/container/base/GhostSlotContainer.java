package org.bsdevelopment.soul_harvest.container.base;

import net.minecraft.world.item.ItemStack;

public interface GhostSlotContainer {
    boolean isGhostSlot(int slotId);

    void setGhostItem(int slotId, ItemStack stack);
}
