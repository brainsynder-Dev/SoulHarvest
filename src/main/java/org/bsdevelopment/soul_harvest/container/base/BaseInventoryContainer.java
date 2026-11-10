package org.bsdevelopment.soul_harvest.container.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.bsdevelopment.soul_harvest.blocks.entity.base.InventoryBlockEntity;

public abstract class BaseInventoryContainer<T extends InventoryBlockEntity> extends BaseContainer {
    public final T blockEntity;
    protected final Player player;
    protected final BlockPos pos;

    @SuppressWarnings("unchecked")
    public BaseInventoryContainer(MenuType<?> menuType, int windowId, Inventory playerInventory, BlockPos blockPos) {
        super(menuType, windowId);
        this.pos = blockPos;
        this.player = playerInventory.player;
        this.blockEntity = (T) player.level().getBlockEntity(pos);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        int containerSlots = blockEntity.getContainerSize();
        ItemStack currentStack = slot.getItem();
        ItemStack originalStack = currentStack.copy();

        if (index < containerSlots) {
            if (!moveItemStackTo(currentStack, containerSlots, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(currentStack, 0, containerSlots, false)) {
            return ItemStack.EMPTY;
        }

        if (currentStack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (currentStack.getCount() == originalStack.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, currentStack);
        return originalStack;
    }

    protected boolean stillValid(Player player, Block block) {
        return stillValid(ContainerLevelAccess.create(player.level(), pos), player, block);
    }
}
