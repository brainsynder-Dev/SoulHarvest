package org.bsdevelopment.soul_harvest.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.bsdevelopment.soul_harvest.blocks.LootExtractorBlock;
import org.bsdevelopment.soul_harvest.blocks.entity.base.ILinkable;
import org.bsdevelopment.soul_harvest.blocks.entity.base.InventoryBlockEntity;
import org.bsdevelopment.soul_harvest.items.FilterItem;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModItems;

public class LootExtractorBlockEntity extends InventoryBlockEntity implements ILinkable {
    public static final int SETTING_WHITELIST = 0;
    public static final int SETTING_MATCH_COMPONENTS = 1;
    public static final int SETTING_ENABLED = 2;
    public static final int SETTING_PRIORITY = 3;
    public static final int DATA_EXPORTING = 4;
    public static final int DATA_COUNT = 5;
    public static final int FILTER_SIZE = 9;
    public static final int MIN_PRIORITY = -999;
    public static final int MAX_PRIORITY = 999;
    private final SimpleContainer filter = new SimpleContainer(FILTER_SIZE) {
        @Override
        public void setChanged() {
            super.setChanged();
            LootExtractorBlockEntity.this.setChanged();
            returnRejectedItems();
        }
    };
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case SETTING_WHITELIST -> whitelist ? 1 : 0;
                case SETTING_MATCH_COMPONENTS -> matchComponents ? 1 : 0;
                case SETTING_ENABLED -> enabled ? 1 : 0;
                case SETTING_PRIORITY -> priority;
                case DATA_EXPORTING -> exporting ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            applySetting(index, value);
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    private BlockPos altarPos = null;
    private boolean whitelist = false;
    private boolean matchComponents = false;
    private boolean enabled = true;
    private int priority = 0;
    private boolean exporting = false;
    private boolean exportedSinceTick = false;

    public LootExtractorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.LOOT_EXTRACTOR.getTileEntityType(), 9, pos, blockState);
    }

    public BlockPos getAltarPos() {
        return altarPos;
    }

    public SimpleContainer getFilter() {
        return filter;
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getPriority() {
        return priority;
    }

    public void applySetting(int setting, int value) {
        switch (setting) {
            case SETTING_WHITELIST -> whitelist = value != 0;
            case SETTING_MATCH_COMPONENTS -> matchComponents = value != 0;
            case SETTING_ENABLED -> enabled = value != 0;
            case SETTING_PRIORITY -> priority = Mth.clamp(value, MIN_PRIORITY, MAX_PRIORITY);
            default -> {
                return;
            }
        }
        setChanged();
        returnRejectedItems();
    }

    public boolean accepts(ItemStack stack) {
        boolean listed = false;
        for (ItemStack filterStack : filter.getItems()) {
            if (filterStack.isEmpty()) continue;
            if (filterStack.is(ModItems.FILTER.get()) ? FilterItem.passes(filterStack, stack, level) : matchesFilterStack(filterStack, stack)) {
                listed = true;
                break;
            }
        }
        return whitelist == listed;
    }

    public int receiveItems(ServerLevel level, ItemStack stack) {
        int offered = stack.getCount();
        ResourceHandler<ItemResource> destination = getDestination(level);
        if (destination != null) {
            int inserted = insertIntoDestination(destination, stack);
            stack.shrink(inserted);
            if (inserted > 0) exportedSinceTick = true;
        }
        if (!stack.isEmpty()) insertIntoBuffer(stack);
        return offered - stack.getCount();
    }

    public void tickTransfer(ServerLevel level) {
        if (level.getGameTime() % 10 != 0) return;
        if (altarPos != null && level.isLoaded(altarPos)) {
            if (level.getBlockEntity(altarPos) instanceof SoulHarvestAltarBlockEntity altar) {
                altar.addLinkedExtractor(worldPosition);
            } else {
                linkPosition(null);
            }
        }

        boolean moved = enabled && !isEmpty() && exportBuffer(level);
        exporting = moved || exportedSinceTick;
        exportedSinceTick = false;
        if (moved) setChanged();
    }

    @Override
    public void linkPosition(BlockPos pos) {
        BlockPos previousAltarPos = altarPos;
        altarPos = pos;
        setChanged();
        if (level == null) return;

        if (previousAltarPos != null && level.getBlockEntity(previousAltarPos) instanceof SoulHarvestAltarBlockEntity previousAltar)
            previousAltar.removeLinkedExtractor(worldPosition);
        if (pos != null && level.getBlockEntity(pos) instanceof SoulHarvestAltarBlockEntity altar)
            altar.addLinkedExtractor(worldPosition);
    }

    @Override
    public boolean isLinked() {
        return altarPos != null;
    }

    @Override
    public void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        whitelist = tag.getBooleanOr("whitelist", false);
        matchComponents = tag.getBooleanOr("match_components", false);
        enabled = tag.getBooleanOr("enabled", true);
        priority = Mth.clamp(tag.getIntOr("priority", 0), MIN_PRIORITY, MAX_PRIORITY);
        if (!tag.contains("bound_altar")) return;

        CompoundTag compound = tag.getCompoundOrEmpty("bound_altar");
        altarPos = new BlockPos(compound.getIntOr("x", 0), compound.getIntOr("y", 0), compound.getIntOr("z", 0));
    }

    @Override
    public void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putBoolean("whitelist", whitelist);
        tag.putBoolean("match_components", matchComponents);
        tag.putBoolean("enabled", enabled);
        tag.putInt("priority", priority);
        if (altarPos == null) return;

        CompoundTag compound = new CompoundTag();
        compound.putInt("x", altarPos.getX());
        compound.putInt("y", altarPos.getY());
        compound.putInt("z", altarPos.getZ());
        tag.put("bound_altar", compound);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        filter.clearContent();
        input.child("filter").ifPresent(filterInput -> ContainerHelper.loadAllItems(filterInput, filter.getItems()));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output.child("filter"), filter.getItems());
    }

    private void returnRejectedItems() {
        if (!(level instanceof ServerLevel) || altarPos == null || !level.isLoaded(altarPos)) return;
        if (!(level.getBlockEntity(altarPos) instanceof SoulHarvestAltarBlockEntity altar)) return;

        boolean returned = false;
        for (int slot = 0; slot < getContainerSize(); slot++) {
            ItemStack stack = getItem(slot);
            if (stack.isEmpty() || accepts(stack)) continue;

            altar.getBulkStorage().addItem(stack);
            getItems().set(slot, ItemStack.EMPTY);
            returned = true;
        }
        if (!returned) return;

        altar.setChanged();
        setChanged();
    }

    private boolean matchesFilterStack(ItemStack filterStack, ItemStack stack) {
        return matchComponents ? ItemStack.isSameItemSameComponents(filterStack, stack) : ItemStack.isSameItem(filterStack, stack);
    }

    private boolean exportBuffer(ServerLevel level) {
        ResourceHandler<ItemResource> destination = getDestination(level);
        if (destination == null) return false;

        boolean moved = false;
        for (int slot = 0; slot < getContainerSize(); slot++) {
            ItemStack stack = getItem(slot);
            if (stack.isEmpty()) continue;

            int inserted = insertIntoDestination(destination, stack);
            if (inserted <= 0) continue;

            stack.shrink(inserted);
            if (stack.isEmpty()) getItems().set(slot, ItemStack.EMPTY);
            moved = true;
        }
        return moved;
    }

    private int insertIntoBuffer(ItemStack stack) {
        int inserted = 0;
        for (int slot = 0; slot < getContainerSize() && !stack.isEmpty(); slot++) {
            ItemStack bufferStack = getItem(slot);
            if (bufferStack.isEmpty()) {
                int moved = Math.min(stack.getCount(), stack.getMaxStackSize());
                getItems().set(slot, stack.split(moved));
                inserted += moved;
            } else if (ItemStack.isSameItemSameComponents(bufferStack, stack)) {
                int moved = Math.min(stack.getCount(), bufferStack.getMaxStackSize() - bufferStack.getCount());
                if (moved <= 0) continue;
                bufferStack.grow(moved);
                stack.shrink(moved);
                inserted += moved;
            }
        }
        if (inserted > 0) setChanged();
        return inserted;
    }

    private ResourceHandler<ItemResource> getDestination(ServerLevel level) {
        Direction facing = getBlockState().getValue(LootExtractorBlock.FACING);
        return level.getCapability(Capabilities.Item.BLOCK, worldPosition.relative(facing.getOpposite()), facing);
    }

    private int insertIntoDestination(ResourceHandler<ItemResource> destination, ItemStack stack) {
        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = ResourceHandlerUtil.insertStacking(destination, ItemResource.of(stack), stack.getCount(), transaction);
            if (inserted > 0) transaction.commit();
            return inserted;
        }
    }
}
