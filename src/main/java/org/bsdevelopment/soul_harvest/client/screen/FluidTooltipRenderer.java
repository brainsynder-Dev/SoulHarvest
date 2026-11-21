package org.bsdevelopment.soul_harvest.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import org.bsdevelopment.soul_harvest.blocks.entity.WisdomVesselBlockEntity;
import org.bsdevelopment.soul_harvest.items.WisdomVesselItem;
import org.bsdevelopment.soul_harvest.registry.ModFluids;

public class FluidTooltipRenderer implements ClientTooltipComponent {
    private final WisdomVesselItem.FluidTooltip tooltip;

    public FluidTooltipRenderer(WisdomVesselItem.FluidTooltip tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public int getHeight(Font font) {
        return 14;
    }

    @Override
    public int getWidth(Font font) {
        return Math.max(100, font.width(getLabel()) + 8);
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor guiGraphics) {
        int barWidth = getWidth(font);
        guiGraphics.fill(x, y, x + barWidth, y + 12, GuiUtils.color("#1E1F22"));

        int fillWidth = Math.round((barWidth - 2) * Math.min(1f, (float) tooltip.storedAmount() / tooltip.capacity()));
        if (fillWidth > 0) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(ModFluids.LIQUID_EXPERIENCE.get().defaultFluidState()).stillMaterial().sprite();
            guiGraphics.enableScissor(x + 1, y + 1, x + 1 + fillWidth, y + 11);
            for (int tileX = 0; tileX < fillWidth; tileX += 16) guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x + 1 + tileX, y + 1, 16, 16);

            guiGraphics.disableScissor();
        }

        guiGraphics.outline(x, y, barWidth, 12, GuiUtils.color("#5A5E66"));
        guiGraphics.centeredText(font, getLabel(), x + barWidth / 2, y + 2, -1);
    }

    private Component getLabel() {
        int storedExperience = tooltip.storedAmount() / WisdomVesselBlockEntity.MB_PER_EXPERIENCE;
        int capacityExperience = tooltip.capacity() / WisdomVesselBlockEntity.MB_PER_EXPERIENCE;

        return Component.translatable("gui.soul_harvest.wisdom_vessel.capacity", String.format("%,d", storedExperience), String.format("%,d", capacityExperience));
    }
}
