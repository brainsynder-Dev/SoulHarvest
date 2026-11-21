package org.bsdevelopment.soul_harvest.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.bsdevelopment.soul_harvest.blocks.entity.WisdomVesselBlockEntity;
import org.bsdevelopment.soul_harvest.items.base.BlockItemHandler;
import org.bsdevelopment.soul_harvest.registry.ModComponents;
import org.bsdevelopment.soul_harvest.utilities.ModUtilities;

import java.util.Optional;
import java.util.function.Consumer;

public class WisdomVesselItem extends BlockItemHandler {
    public WisdomVesselItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, consumer, flag);

        int storedExperience = getStoredAmount(stack) / WisdomVesselBlockEntity.MB_PER_EXPERIENCE;
        int expansionRunes = getExpansionRunes(stack);
        consumer.accept(Component.translatable(getDescriptionId() + ".stored", String.format("%,d", storedExperience), String.format("%.1f", ModUtilities.getExactLevel(storedExperience))).withStyle(ChatFormatting.GREEN));
        if (expansionRunes > 0) consumer.accept(Component.translatable(getDescriptionId() + ".expansion_runes", expansionRunes).withStyle(ChatFormatting.AQUA));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new FluidTooltip(getStoredAmount(stack), WisdomVesselBlockEntity.getCapacity(getExpansionRunes(stack))));
    }

    private int getStoredAmount(ItemStack stack) {
        return stack.getOrDefault(ModComponents.FLUID_CONTENTS.get(), SimpleFluidContent.EMPTY).getAmount();
    }

    private int getExpansionRunes(ItemStack stack) {
        return stack.getOrDefault(ModComponents.EXPANSION_RUNES.get(), 0);
    }

    public record FluidTooltip(int storedAmount, int capacity) implements TooltipComponent {}
}
