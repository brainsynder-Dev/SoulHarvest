package org.bsdevelopment.soul_harvest.items;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bsdevelopment.soul_harvest.component.FilterData;
import org.bsdevelopment.soul_harvest.container.FilterContainer;
import org.bsdevelopment.soul_harvest.items.base.BaseItem;
import org.bsdevelopment.soul_harvest.registry.ModComponents;

public class FilterItem extends BaseItem {
    public FilterItem(Properties properties) {
        super(properties);
    }

    public static boolean passes(ItemStack filter, ItemStack candidate, Level level) {
        boolean listed = filter.getOrDefault(ModComponents.FILTER_DATA.get(), FilterData.EMPTY).lists(candidate, level);
        return filter.has(ModComponents.FILTER_BLACKLIST.get()) != listed;
    }

    public static void setBlacklist(ItemStack filter, boolean blacklist) {
        if (blacklist) {
            filter.set(ModComponents.FILTER_BLACKLIST.get(), Unit.INSTANCE);
        } else {
            filter.remove(ModComponents.FILTER_BLACKLIST.get());
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ItemStack filter = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            setBlacklist(filter, !filter.has(ModComponents.FILTER_BLACKLIST.get()));
            return InteractionResult.SUCCESS;
        }

        player.openMenu(new SimpleMenuProvider((windowId, playerInventory, menuPlayer) -> new FilterContainer(windowId, playerInventory, hand), Component.translatable("gui.soul_harvest.filter.title")), buffer -> buffer.writeEnum(hand));
        return InteractionResult.SUCCESS;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.has(ModComponents.FILTER_BLACKLIST.get())) return Component.translatable(getDescriptionId() + ".blacklist");

        return super.getName(stack);
    }
}
