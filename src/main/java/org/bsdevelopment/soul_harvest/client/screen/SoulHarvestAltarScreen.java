package org.bsdevelopment.soul_harvest.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import org.bsdevelopment.soul_harvest.blocks.entity.SoulHarvestAltarBlockEntity;
import org.bsdevelopment.soul_harvest.client.screen.widgets.Widgets;
import org.bsdevelopment.soul_harvest.container.SoulHarvestAltarContainer;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public class SoulHarvestAltarScreen extends AbstractContainerScreen<SoulHarvestAltarContainer> {
    private final SoulHarvestAltarBlockEntity altar;

    public SoulHarvestAltarScreen(SoulHarvestAltarContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 140);
        altar = menu.blockEntity;
        titleLabelY = 5;
        inventoryLabelY = 46;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Identifier texture = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/soul_harvest_altar.png");
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        int glowColor = ARGB.white(0.6f + 0.4f * Mth.sin(Util.getMillis() / 300f));
        altar.SLOTS.forEach((slotIndex, slotPosition) -> {
            int x = leftPos + slotPosition.x();
            int y = topPos + slotPosition.y();

            if (altar.getItem(slotIndex).isEmpty()) {
                GuiUtils.drawWidget(guiGraphics, slotPosition.ghostWidget(), x, y);
            } else {
                GuiUtils.drawWidget(guiGraphics, Widgets.SLOT_RUNE_GLOW, x - 1, y - 1, glowColor);
            }
        });
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(font, title, titleLabelX, titleLabelY, GuiUtils.color("#AEEBDC"), false);
        guiGraphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, GuiUtils.color("#A0A4AC"), false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
