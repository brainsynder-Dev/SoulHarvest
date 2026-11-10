package org.bsdevelopment.soul_harvest.container;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.bsdevelopment.soul_harvest.blocks.entity.SoulHarvestAltarBlockEntity;
import org.bsdevelopment.soul_harvest.config.ModConfig;
import org.bsdevelopment.soul_harvest.container.base.BaseInventoryContainer;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModContainers;

public class SoulHarvestAltarContainer extends BaseInventoryContainer<SoulHarvestAltarBlockEntity> {
    public SoulHarvestAltarContainer(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(windowId, playerInventory, extraData.readBlockPos());
    }

    public SoulHarvestAltarContainer(int windowId, Inventory playerInventory, BlockPos blockPos) {
        super(ModContainers.SOUL_HARVEST_ALTAR.get(), windowId, playerInventory, blockPos);

        blockEntity.SLOTS.forEach((slotIndex, slotPosition) -> addSlot(new Slot(blockEntity, slotIndex, slotPosition.x(), slotPosition.y()) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(slotPosition.item());
            }

            @Override
            public int getMaxStackSize(ItemStack stack) {
                return Math.min(super.getMaxStackSize(stack), ModConfig.getRuneMaxStack(stack.getItem()));
            }
        }));

        addPlayerSlots(playerInventory, 8, 58);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(player, ModBlocks.SOUL_HARVEST_ALTAR.getBlock());
    }
}
