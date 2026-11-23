package org.bsdevelopment.soul_harvest.container;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.bsdevelopment.soul_harvest.blocks.entity.LootExtractorBlockEntity;
import org.bsdevelopment.soul_harvest.component.FilterData;
import org.bsdevelopment.soul_harvest.container.base.BaseContainer;
import org.bsdevelopment.soul_harvest.container.base.GhostSlotContainer;
import org.bsdevelopment.soul_harvest.items.FilterItem;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModComponents;
import org.bsdevelopment.soul_harvest.registry.ModContainers;
import org.bsdevelopment.soul_harvest.registry.ModItems;
import org.bsdevelopment.soul_harvest.utilities.ItemFilterPredicates;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class FilterContainer extends BaseContainer implements GhostSlotContainer {
    public static final int SETTING_BLACKLIST = 0;
    public static final int SETTING_MATCH_COMPONENTS = 1;
    public static final int SETTING_IGNORE_DAMAGE = 2;
    public static final int SETTING_TAG = 3;
    public static final int SETTING_DATA = 4;
    public static final int PLAYER_START = FilterData.SIZE;

    private final Player player;
    private final @Nullable InteractionHand hand;
    private final @Nullable BlockPos extractorPos;
    private final @Nullable SimpleContainer extractorFilter;
    private final int filterIndex;
    private final int lockedSlot;
    private final SimpleContainer ghostSlots = new SimpleContainer(FilterData.SIZE);

    public FilterContainer(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(windowId, playerInventory, extraData.readNullable(buffer -> buffer.readEnum(InteractionHand.class)), extraData.readNullable(buffer -> buffer.readBlockPos()), extraData.readVarInt());
    }

    private FilterContainer(int windowId, Inventory playerInventory, @Nullable InteractionHand hand, @Nullable BlockPos extractorPos, int filterIndex) {
        super(ModContainers.FILTER.get(), windowId);
        this.player = playerInventory.player;
        this.hand = hand;
        this.extractorPos = extractorPos;
        this.filterIndex = filterIndex;
        this.lockedSlot = hand == InteractionHand.MAIN_HAND ? PLAYER_START + 27 + playerInventory.getSelectedSlot() : -1;
        this.extractorFilter = (extractorPos != null) && (player.level().getBlockEntity(extractorPos) instanceof LootExtractorBlockEntity extractor) ? extractor.getFilter() : null;

        FilterData data = getData();
        for (int index = 0; index < FilterData.SIZE; index++) {
            ghostSlots.setItem(index, data.entry(index).stack());
            addSlot(new Slot(ghostSlots, index, 12 + (index % 3) * 18, 20 + (index / 3) * 18) {
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
        addPlayerSlots(playerInventory, 8, 124);
        if (extractorFilter != null) addSlot(new Slot(extractorFilter, filterIndex, 0, 0) {
            @Override
            public boolean isActive() {
                return false;
            }

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

    public static void open(Player player, InteractionHand hand) {
        openMenu(player, hand, null, -1);
    }

    public static void open(Player player, BlockPos extractorPos, int filterIndex) {
        openMenu(player, null, extractorPos, filterIndex);
    }

    public ItemStack getFilter() {
        if (extractorFilter != null) return extractorFilter.getItem(filterIndex);
        if (hand == null) return ItemStack.EMPTY;

        return player.getItemInHand(hand);
    }

    public FilterData getData() {
        return getFilter().getOrDefault(ModComponents.FILTER_DATA.get(), FilterData.EMPTY);
    }

    private void setData(FilterData data) {
        getFilter().set(ModComponents.FILTER_DATA.get(), data);
    }

    @Override
    public boolean isGhostSlot(int slotId) {
        return slotId >= 0 && slotId < PLAYER_START;
    }

    @Override
    public void setGhostItem(int slotId, ItemStack stack) {
        setGhost(slotId, stack);
    }

    public void applySetting(int setting, int slot, int value, String text) {
        ItemStack filter = getFilter();
        FilterData data = getData();
        switch (setting) {
            case SETTING_BLACKLIST -> FilterItem.setBlacklist(filter, value != 0);
            case SETTING_MATCH_COMPONENTS -> setData(data.withMatchComponents(value != 0));
            case SETTING_IGNORE_DAMAGE -> setData(data.withIgnoreDamage(value != 0));
            case SETTING_TAG -> applyTag(data, slot, text);
            case SETTING_DATA -> applyDataFilter(data, slot, text);
            default -> {}
        }

        saveFilter();
    }

    @Override
    public void clicked(int slotId, int button, ContainerInput input, Player player) {
        if (slotId == lockedSlot || (input == ContainerInput.SWAP && hand == InteractionHand.MAIN_HAND && PLAYER_START + 27 + button == lockedSlot)) return;

        if (!isGhostSlot(slotId)) {
            super.clicked(slotId, button, input, player);
            return;
        }

        ItemStack carried = getCarried();
        if (input == ContainerInput.PICKUP && button == 0 && !carried.isEmpty()) {
            setGhost(slotId, carried);
        } else if ((input == ContainerInput.PICKUP && button == 1 && carried.isEmpty()) || input == ContainerInput.QUICK_MOVE) {
            setGhost(slotId, ItemStack.EMPTY);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (isGhostSlot(index) || index == lockedSlot) return ItemStack.EMPTY;

        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        for (int ghostIndex = 0; ghostIndex < FilterData.SIZE; ghostIndex++) {
            if (ItemStack.isSameItemSameComponents(ghostSlots.getItem(ghostIndex), slot.getItem())) return ItemStack.EMPTY;
        }

        for (int ghostIndex = 0; ghostIndex < FilterData.SIZE; ghostIndex++) {
            if (!ghostSlots.getItem(ghostIndex).isEmpty()) continue;
            setGhost(ghostIndex, slot.getItem());
            break;
        }

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
        if (!getFilter().is(ModItems.FILTER.get())) return false;
        if (extractorPos == null) return true;

        return stillValid(ContainerLevelAccess.create(player.level(), extractorPos), player, ModBlocks.LOOT_EXTRACTOR.getBlock());
    }

    private static void openMenu(Player player, @Nullable InteractionHand hand, @Nullable BlockPos extractorPos, int filterIndex) {
        player.openMenu(new SimpleMenuProvider((windowId, playerInventory, menuPlayer) -> new FilterContainer(windowId, playerInventory, hand, extractorPos, filterIndex), Component.translatable("gui.soul_harvest.filter.title")), buffer -> {
            buffer.writeNullable(hand, (output, value) -> output.writeEnum(value));
            buffer.writeNullable(extractorPos, (output, value) -> output.writeBlockPos(value));
            buffer.writeVarInt(filterIndex);
        });
    }

    private void setGhost(int index, ItemStack stack) {
        ItemStack copy = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        ghostSlots.setItem(index, copy);
        if (player.level().isClientSide()) return;

        Optional<ItemStackTemplate> template = copy.isEmpty() ? Optional.empty() : Optional.of(ItemStackTemplate.fromNonEmptyStack(copy));
        setData(getData().withEntry(index, new FilterData.Entry(template, Optional.empty(), "")));
        saveFilter();
    }

    private void saveFilter() {
        if (extractorFilter != null) extractorFilter.setChanged();
    }

    private void applyTag(FilterData data, int slot, String tagId) {
        if (!isGhostSlot(slot)) return;
        FilterData.Entry entry = data.entry(slot);
        if (entry.isEmpty()) return;

        Optional<TagKey<Item>> tag = Optional.empty();
        if (!tagId.isEmpty()) {
            Identifier id = Identifier.tryParse(tagId);
            if (id == null) return;

            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, id);
            if (!entry.stack().typeHolder().is(tagKey)) return;

            tag = Optional.of(tagKey);
        }

        setData(data.withEntry(slot, new FilterData.Entry(entry.item(), tag, entry.data())));
    }

    private void applyDataFilter(FilterData data, int slot, String text) {
        if (!isGhostSlot(slot) || text.length() > 256) return;
        FilterData.Entry entry = data.entry(slot);
        if (entry.isEmpty() || ItemFilterPredicates.validate(text, player.level()) != null) return;

        setData(data.withEntry(slot, new FilterData.Entry(entry.item(), entry.tag(), text)));
    }
}
