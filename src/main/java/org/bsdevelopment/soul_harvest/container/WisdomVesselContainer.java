package org.bsdevelopment.soul_harvest.container;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.bsdevelopment.soul_harvest.blocks.entity.WisdomVesselBlockEntity;
import org.bsdevelopment.soul_harvest.container.base.BaseInventoryContainer;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModContainers;
import org.bsdevelopment.soul_harvest.utilities.ModUtilities;

public class WisdomVesselContainer extends BaseInventoryContainer<WisdomVesselBlockEntity> {
    private final ContainerData data;

    public WisdomVesselContainer(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(windowId, playerInventory, extraData.readBlockPos(), new SimpleContainerData(1));
    }

    public WisdomVesselContainer(int windowId, Inventory playerInventory, BlockPos blockPos, ContainerData data) {
        super(ModContainers.WISDOM_VESSEL.get(), windowId, playerInventory, blockPos);
        this.data = data;

        addSlot(new Slot(blockEntity, WisdomVesselBlockEntity.INPUT_SLOT, 145, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return WisdomVesselBlockEntity.isBucket(stack);
            }
        });
        addSlot(new Slot(blockEntity, WisdomVesselBlockEntity.OUTPUT_SLOT, 145, 51) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        addPlayerSlots(playerInventory, 8, 106);
        addDataSlots(data);
    }

    public float getTransferProgress() {
        return (float) data.get(0) / WisdomVesselBlockEntity.TRANSFER_TIME;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        int playerExperience = ModUtilities.getTotalExperience(player);
        int level = player.experienceLevel;
        float progress = player.experienceProgress;

        switch (buttonId) {
            case 0 -> blockEntity.storeExperience(player, playerExperience - experienceBelow(level, progress, 1));
            case 1 -> blockEntity.storeExperience(player, playerExperience - experienceBelow(level, progress, 10));
            case 2 -> blockEntity.storeExperience(player, playerExperience);
            case 3 ->
                    blockEntity.takeExperience(player, ModUtilities.getExperienceForLevel(level + 1, progress) - playerExperience);
            case 4 ->
                    blockEntity.takeExperience(player, ModUtilities.getExperienceForLevel(level + 10, progress) - playerExperience);
            case 5 -> blockEntity.takeExperience(player, blockEntity.getStoredExperience());
            default -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(player, ModBlocks.WISDOM_VESSEL.getBlock());
    }

    private int experienceBelow(int level, float progress, int levels) {
        if (level < levels) return 0;

        return ModUtilities.getExperienceForLevel(level - levels, progress);
    }
}
