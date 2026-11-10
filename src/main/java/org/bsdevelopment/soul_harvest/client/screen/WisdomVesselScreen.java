package org.bsdevelopment.soul_harvest.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.bsdevelopment.soul_harvest.blocks.entity.WisdomVesselBlockEntity;
import org.bsdevelopment.soul_harvest.client.screen.widgets.IconButton;
import org.bsdevelopment.soul_harvest.client.screen.widgets.Widgets;
import org.bsdevelopment.soul_harvest.container.WisdomVesselContainer;
import org.bsdevelopment.soul_harvest.registry.ModConstants;
import org.bsdevelopment.soul_harvest.utilities.ModUtilities;

import java.util.ArrayList;
import java.util.List;

public class WisdomVesselScreen extends AbstractContainerScreen<WisdomVesselContainer> {
    private final WisdomVesselBlockEntity vessel;
    private final List<IconButton> storeButtons = new ArrayList<>();
    private final List<IconButton> takeButtons = new ArrayList<>();

    public WisdomVesselScreen(WisdomVesselContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 188);
        vessel = menu.blockEntity;
        titleLabelY = 5;
        inventoryLabelY = 95;
    }

    @Override
    protected void init() {
        super.init();
        storeButtons.clear();
        takeButtons.clear();

        storeButtons.add(addButton(41, 48, Widgets.ICON_XP_INSERT, "store_1", 0));
        storeButtons.add(addButton(63, 48, Widgets.ICON_XP_INSERT_10, "store_10", 1));
        storeButtons.add(addButton(85, 48, Widgets.ICON_XP_INSERT_ALL, "store_all", 2));
        takeButtons.add(addButton(41, 69, Widgets.ICON_XP_EXTRACT, "take_1", 3));
        takeButtons.add(addButton(63, 69, Widgets.ICON_XP_EXTRACT_10, "take_10", 4));
        takeButtons.add(addButton(85, 69, Widgets.ICON_XP_EXTRACT_ALL, "take_all", 5));
        updateButtons();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateButtons();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Identifier texture = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/wisdom_vessel.png");
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        float fillLevel = vessel.getFillLevel();
        int fillHeight = fillLevel > 0 ? Math.max(1, Math.round(70 * fillLevel)) : 0;
        GuiUtils.drawWidget(guiGraphics, Widgets.XP_TANK_FILL, leftPos + 12, topPos + 18, 0, 70 - fillHeight, 22, fillHeight, -1);
        if (fillHeight > 0) GuiUtils.drawWidget(guiGraphics, Widgets.XP_TANK_SURFACE, leftPos + 12, topPos + 18 + 70 - fillHeight);

        GuiUtils.drawWidget(guiGraphics, Widgets.XP_TANK_GAUGE, leftPos + 12, topPos + 18);

        int experienceAfterTaking = ModUtilities.getTotalExperience(minecraft.player) + vessel.getStoredExperience();
        int barWidth = Math.round(84 * ModUtilities.getLevelProgress(experienceAfterTaking));
        GuiUtils.drawWidget(guiGraphics, Widgets.XP_BAR_FILL, leftPos + 43, topPos + 41, 0, 0, barWidth, 5, -1);

        if (vessel.getItem(WisdomVesselBlockEntity.INPUT_SLOT).isEmpty()) GuiUtils.drawWidget(guiGraphics, Widgets.GHOST_BUCKET, leftPos + 145, topPos + 18);
        if (vessel.getItem(WisdomVesselBlockEntity.OUTPUT_SLOT).isEmpty()) GuiUtils.drawWidget(guiGraphics, Widgets.GHOST_BUCKET, leftPos + 145, topPos + 51);

        int arrowHeight = Math.round(9 * menu.getTransferProgress());
        GuiUtils.drawWidget(guiGraphics, Widgets.ARROW_DOWN_FULL, leftPos + 149, topPos + 38, 0, 0, 7, arrowHeight, -1);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int labelColor = GuiUtils.color("#A0A4AC");
        guiGraphics.text(font, title, titleLabelX, titleLabelY, GuiUtils.color("#AEEBDC"), false);
        guiGraphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, labelColor, false);
        guiGraphics.text(font, Component.translatable("gui.soul_harvest.wisdom_vessel.store"), 109, 54, labelColor, false);
        guiGraphics.text(font, Component.translatable("gui.soul_harvest.wisdom_vessel.take"), 109, 75, labelColor, false);

        int storedExperience = vessel.getStoredExperience();
        int playerExperience = ModUtilities.getTotalExperience(minecraft.player);
        float storedLevels = ModUtilities.getExactLevel(playerExperience + storedExperience) - ModUtilities.getExactLevel(playerExperience);
        Component experience = Component.translatable("gui.soul_harvest.wisdom_vessel.experience", String.format("%,d", storedExperience));
        Component levels = Component.translatable("gui.soul_harvest.wisdom_vessel.levels", String.format("%.1f", storedLevels));
        guiGraphics.text(font, experience, 85 - font.width(experience) / 2, 19, GuiUtils.color("#80FF20"), false);
        guiGraphics.text(font, levels, 85 - font.width(levels) / 2, 28, GuiUtils.color("#AEEBDC"), false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private IconButton addButton(int x, int y, Widgets.WidgetInfo icon, String key, int buttonId) {
        Component message = Component.translatable("gui.soul_harvest.wisdom_vessel." + key);
        return addRenderableWidget(new IconButton(leftPos + x, topPos + y, icon, message, () -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId)));
    }

    private void updateButtons() {
        boolean canStore = ModUtilities.getTotalExperience(minecraft.player) > 0 && vessel.getFreeExperience() > 0;
        boolean canTake = vessel.getStoredExperience() > 0;
        storeButtons.forEach(button -> button.active = canStore);
        takeButtons.forEach(button -> button.active = canTake);
    }
}
