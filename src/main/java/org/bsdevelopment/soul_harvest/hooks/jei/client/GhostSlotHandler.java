package org.bsdevelopment.soul_harvest.hooks.jei.client;

import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.bsdevelopment.soul_harvest.container.base.GhostSlotContainer;
import org.bsdevelopment.soul_harvest.network.GhostSlotPayload;
import org.bsdevelopment.soul_harvest.network.PacketHandler;

import java.util.ArrayList;
import java.util.List;

public class GhostSlotHandler<T extends AbstractContainerScreen<?>> implements IGhostIngredientHandler<T> {
    @Override
    public <I> List<Target<I>> getTargetsTyped(T screen, ITypedIngredient<I> ingredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();
        if (!(screen.getMenu() instanceof GhostSlotContainer ghostSlotContainer) || ingredient.getItemStack().isEmpty())
            return targets;

        for (Slot slot : screen.getMenu().slots) {
            if (!ghostSlotContainer.isGhostSlot(slot.index)) continue;

            Rect2i area = new Rect2i(screen.getLeftPos() + slot.x, screen.getTopPos() + slot.y, 16, 16);
            targets.add(new Target<>() {
                @Override
                public Rect2i getArea() {
                    return area;
                }

                @Override
                public void accept(I value) {
                    if (!(value instanceof ItemStack stack)) return;

                    ghostSlotContainer.setGhostItem(slot.index, stack);
                    PacketHandler.sendPayload(new GhostSlotPayload(slot.index, stack.copyWithCount(1)));
                }
            });
        }
        return targets;
    }

    @Override
    public void onComplete() {}
}
