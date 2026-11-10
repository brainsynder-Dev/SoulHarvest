package org.bsdevelopment.soul_harvest.container.base;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public abstract class BaseContainer extends AbstractContainerMenu {
    public BaseContainer(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    protected void addPlayerSlots(Inventory playerInventory, int x, int y) {
        addStandardInventorySlots(playerInventory, x, y);
    }
}
