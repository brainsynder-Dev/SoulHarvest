package org.bsdevelopment.soul_harvest.items.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.bsdevelopment.soul_harvest.utilities.ModUtilities;

import java.util.LinkedList;
import java.util.function.Consumer;

public class BlockItemHandler extends BlockItem {
    public BlockItemHandler(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag flag) {
        String key = getDescriptionId() + ".description";
        LinkedList<Component> multiLine = ModUtilities.formatMultiLine(key);
        if (!multiLine.isEmpty()) {
            multiLine.forEach(component -> consumer.accept(component.copy().withStyle(ChatFormatting.GRAY)));
        } else {
            MutableComponent component = Component.translatable(key);
            if (!component.getString().equals(key)) consumer.accept(component.copy().withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, context, tooltipDisplay, consumer, flag);
    }
}
