package org.bsdevelopment.soul_harvest.items.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.bsdevelopment.soul_harvest.utilities.ModUtilities;

import java.util.LinkedList;
import java.util.function.Consumer;

public class BaseItem extends Item {
    public BaseItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag flag) {
        this.appendItemInformation(stack, context, consumer, flag.isAdvanced());
        super.appendHoverText(stack, context, tooltipDisplay, consumer, flag);
    }

    public void appendItemInformation(ItemStack stack, Item.TooltipContext context, Consumer<Component> info, boolean advanced) {
        String key = getDescriptionId() + ".description";
        LinkedList<Component> multiLine = ModUtilities.formatMultiLine(key);
        if (!multiLine.isEmpty()) {
            multiLine.forEach(component -> info.accept(component.copy().withStyle(ChatFormatting.GRAY)));
            return;
        }

        MutableComponent component = Component.translatable(key);
        if (component.getString().equals(key)) return;

        info.accept(component.copy().withStyle(ChatFormatting.GRAY));
    }
}
