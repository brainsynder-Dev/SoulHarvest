package org.bsdevelopment.soul_harvest.container;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.bsdevelopment.soul_harvest.blocks.entity.LootExtractorBlockEntity;
import org.bsdevelopment.soul_harvest.container.base.BaseInventoryContainer;
import org.bsdevelopment.soul_harvest.container.base.GhostSlotContainer;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModContainers;

public class LootExtractorContainer extends BaseInventoryContainer<LootExtractorBlockEntity> implements GhostSlotContainer {
    public static final int BUFFER_START = 0;
    public static final int FILTER_START = 9;
    public static final int PLAYER_START = 18;

    private final ContainerData data;

    public LootExtractorContainer(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(windowId, playerInventory, extraData.readBlockPos(), new SimpleContainerData(LootExtractorBlockEntity.DATA_COUNT));
    }

    public LootExtractorContainer(int windowId, Inventory playerInventory, BlockPos blockPos, ContainerData data) {
        super(ModContainers.LOOT_EXTRACTOR.get(), windowId, playerInventory, blockPos);
        this.data = data;

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(blockEntity, column, 8 + column * 18, 93) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        for (int index = 0; index < LootExtractorBlockEntity.FILTER_SIZE; index++) {
            addSlot(new Slot(blockEntity.getFilter(), index, 12 + (index % 3) * 18, 20 + (index / 3) * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }

                @Override
                public boolean mayPickup(Player player) {
                    return false;
                }
            });
        }

        addPlayerSlots(playerInventory, 8, 126);
        addDataSlots(data);
    }

    @Override
    public boolean isGhostSlot(int slotId) {
        return slotId >= FILTER_START && slotId < PLAYER_START;
    }

    @Override
    public void setGhostItem(int slotId, ItemStack stack) {
        slots.get(slotId).set(stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
    }

    public boolean getSetting(int setting) {
        return data.get(setting) != 0;
    }

    public int getPriority() {
        return data.get(LootExtractorBlockEntity.SETTING_PRIORITY);
    }

    @Override
    public void clicked(int slotId, int button, ContainerInput input, Player player) {
        if (!isGhostSlot(slotId)) {
            super.clicked(slotId, button, input, player);
            return;
        }

        if (input != ContainerInput.PICKUP && input != ContainerInput.QUICK_MOVE) return;

        ItemStack carried = getCarried();
        Slot slot = slots.get(slotId);
        slot.set(input == ContainerInput.PICKUP && !carried.isEmpty() ? carried.copyWithCount(1) : ItemStack.EMPTY);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack currentStack = slot.getItem();
        if (index < FILTER_START) {
            ItemStack originalStack = currentStack.copy();
            if (!moveItemStackTo(currentStack, PLAYER_START, slots.size(), true)) return ItemStack.EMPTY;
            if (currentStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            return originalStack;
        }

        if (index >= PLAYER_START) addToFilter(currentStack);
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canDragTo(Slot slot) {
        return !isGhostSlot(slot.index) && super.canDragTo(slot);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack carried, Slot slot) {
        return !isGhostSlot(slot.index) && super.canTakeItemForPickAll(carried, slot);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(player, ModBlocks.LOOT_EXTRACTOR.getBlock());
    }

    private void addToFilter(ItemStack stack) {
        for (int slotId = FILTER_START; slotId < PLAYER_START; slotId++) {
            if (ItemStack.isSameItemSameComponents(slots.get(slotId).getItem(), stack)) return;
        }
        
        for (int slotId = FILTER_START; slotId < PLAYER_START; slotId++) {
            Slot filterSlot = slots.get(slotId);
            if (filterSlot.hasItem()) continue;

            filterSlot.set(stack.copyWithCount(1));
            return;
        }
    }
}
