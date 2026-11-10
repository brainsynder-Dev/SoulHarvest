package org.bsdevelopment.soul_harvest.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import org.bsdevelopment.soul_harvest.blocks.entity.LootExtractorBlockEntity;
import org.bsdevelopment.soul_harvest.client.screen.widgets.IconButton;
import org.bsdevelopment.soul_harvest.client.screen.widgets.Widgets;
import org.bsdevelopment.soul_harvest.container.LootExtractorContainer;
import org.bsdevelopment.soul_harvest.network.PacketHandler;
import org.bsdevelopment.soul_harvest.network.LootExtractorSettingPayload;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public class LootExtractorScreen extends AbstractContainerScreen<LootExtractorContainer> {
    private IconButton modeButton;
    private IconButton componentsButton;
    private IconButton enabledButton;
    private EditBox priorityField;
    private boolean priorityFieldWasFocused = false;
    private int lastButtonState = -1;

    public LootExtractorScreen(LootExtractorContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 208);
        titleLabelY = 5;
        inventoryLabelY = 114;
    }

    @Override
    protected void init() {
        super.init();
        lastButtonState = -1;

        modeButton = addRenderableWidget(new IconButton(leftPos + 72, topPos + 19, Widgets.ICON_BLACKLIST, translate("blacklist"), () -> toggleSetting(LootExtractorBlockEntity.SETTING_WHITELIST)));
        componentsButton = addRenderableWidget(new IconButton(leftPos + 94, topPos + 19, Widgets.ICON_NBT_IGNORE, translate("nbt_off"), () -> toggleSetting(LootExtractorBlockEntity.SETTING_MATCH_COMPONENTS)));
        enabledButton = addRenderableWidget(new IconButton(leftPos + 116, topPos + 19, Widgets.ICON_POWER_ON, translate("enabled"), () -> toggleSetting(LootExtractorBlockEntity.SETTING_ENABLED)));

        priorityField = new EditBox(font, leftPos + 76, topPos + 58, 72, 10, translate("priority"));
        priorityField.setBordered(false);
        priorityField.setMaxLength(4);
        priorityField.setFilter(text -> text.matches("-?\\d{0,3}"));
        priorityField.setTextColor(GuiUtils.color("#AEEBDC"));
        priorityField.setValue(String.valueOf(menu.getPriority()));
        priorityField.setTooltip(Tooltip.create(tooltip("priority", "priority.description")));
        addRenderableWidget(priorityField);

        updateButtons();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (priorityFieldWasFocused && !priorityField.isFocused()) submitPriorityField();
        priorityFieldWasFocused = priorityField.isFocused();

        if (!priorityField.isFocused() && !priorityField.getValue().equals(String.valueOf(menu.getPriority()))) priorityField.setValue(String.valueOf(menu.getPriority()));

        updateButtons();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!priorityField.isFocused() || event.isEscape()) return super.keyPressed(event);
        if (event.isConfirmation()) {
            priorityField.setFocused(false);
            setFocused(null);
            return true;
        }

        priorityField.keyPressed(event);
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            int step = event.hasShiftDown() ? 10 : 1;
            if (isOver(event.x(), event.y(), 153, 56, 12, 5)) {
                sendPriority(menu.getPriority() + step);
                return true;
            }

            if (isOver(event.x(), event.y(), 153, 62, 12, 6)) {
                sendPriority(menu.getPriority() - step);
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!isOver(mouseX, mouseY, 72, 55, 94, 14) || scrollY == 0) return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

        sendPriority(menu.getPriority() + (int) Math.signum(scrollY));
        return true;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Identifier texture = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/loot_extractor.png");
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        for (int slotId = LootExtractorContainer.FILTER_START; slotId < LootExtractorContainer.PLAYER_START; slotId++) {
            if (menu.slots.get(slotId).hasItem()) continue;
            int filterIndex = slotId - LootExtractorContainer.FILTER_START;
            GuiUtils.drawWidget(guiGraphics, Widgets.GHOST_FILTER, leftPos + 12 + (filterIndex % 3) * 18, topPos + 20 + (filterIndex / 3) * 18);
        }

        if (menu.getSetting(LootExtractorBlockEntity.DATA_EXPORTING)) {
            int arrowHeight = Mth.ceil(9 * (Util.getMillis() % 1000 / 1000f));
            GuiUtils.drawWidget(guiGraphics, Widgets.ARROW_DOWN_FULL, leftPos + 161, topPos + 82, 0, 0, 7, arrowHeight, -1);
        }

        Widgets.WidgetInfo priorityBox = priorityField.isFocused() ? Widgets.BOX_FOCUSED : isOver(mouseX, mouseY, 72, 55, 94, 14) ? Widgets.BOX_HOVER : Widgets.BOX_NORMAL;
        GuiUtils.drawNineSlice(guiGraphics, priorityBox, leftPos + 72, topPos + 55, 94, 14, 3);
        guiGraphics.fill(leftPos + 152, topPos + 57, leftPos + 153, topPos + 67, GuiUtils.color("#0A0B0E"));
        drawStepper(guiGraphics, Widgets.CHEVRON_UP, 155, 57, 153, 56, 12, 5, mouseX, mouseY);
        drawStepper(guiGraphics, Widgets.CHEVRON_DOWN, 155, 63, 153, 62, 12, 6, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int labelColor = GuiUtils.color("#A0A4AC");
        guiGraphics.text(font, title, titleLabelX, titleLabelY, GuiUtils.color("#AEEBDC"), false);
        guiGraphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, labelColor, false);
        guiGraphics.text(font, translate("priority"), 73, 45, labelColor, false);
        guiGraphics.text(font, translate("waiting"), 8, 82, labelColor, false);

        int priority = menu.getPriority();
        if (!priorityField.isFocused() && priority != 0) {
            Component hint = translate(priority > 0 ? "filled_first" : "filled_last");
            guiGraphics.text(font, hint, 149 - font.width(hint), 58, GuiUtils.color("#60646C"), false);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltip(guiGraphics, mouseX, mouseY);
        if (hoveredSlot != null && menu.isGhostSlot(hoveredSlot.index) && !hoveredSlot.hasItem() && menu.getCarried().isEmpty()) guiGraphics.setTooltipForNextFrame(font, translate("filter_slot"), mouseX, mouseY);
        if (isOver(mouseX, mouseY, 153, 56, 12, 5)) guiGraphics.setTooltipForNextFrame(font, translate("priority_increase"), mouseX, mouseY);
        if (isOver(mouseX, mouseY, 153, 62, 12, 6)) guiGraphics.setTooltipForNextFrame(font, translate("priority_decrease"), mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void drawStepper(GuiGraphicsExtractor guiGraphics, Widgets.WidgetInfo chevron, int chevronX, int chevronY, int areaX, int areaY, int areaWidth, int areaHeight, int mouseX, int mouseY) {
        boolean hovered = isOver(mouseX, mouseY, areaX, areaY, areaWidth, areaHeight);
        if (hovered) guiGraphics.fill(leftPos + areaX, topPos + areaY, leftPos + areaX + areaWidth, topPos + areaY + areaHeight, ARGB.color(0x32, GuiUtils.color("#96FFEB")));

        GuiUtils.drawWidget(guiGraphics, chevron, leftPos + chevronX, topPos + chevronY, hovered ? -1 : ARGB.white(0.7f));
    }

    private boolean isOver(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + width && mouseY >= topPos + y && mouseY < topPos + y + height;
    }

    private void updateButtons() {
        boolean whitelist = menu.getSetting(LootExtractorBlockEntity.SETTING_WHITELIST);
        boolean matchComponents = menu.getSetting(LootExtractorBlockEntity.SETTING_MATCH_COMPONENTS);
        boolean enabled = menu.getSetting(LootExtractorBlockEntity.SETTING_ENABLED);
        int buttonState = (whitelist ? 1 : 0) | (matchComponents ? 2 : 0) | (enabled ? 4 : 0);
        if (buttonState == lastButtonState) return;
        lastButtonState = buttonState;

        updateButton(modeButton, whitelist ? Widgets.ICON_WHITELIST : Widgets.ICON_BLACKLIST, whitelist ? "whitelist" : "blacklist");
        updateButton(componentsButton, matchComponents ? Widgets.ICON_NBT_MATCH : Widgets.ICON_NBT_IGNORE, matchComponents ? "nbt_on" : "nbt_off");
        updateButton(enabledButton, enabled ? Widgets.ICON_POWER_ON : Widgets.ICON_POWER_OFF, enabled ? "enabled" : "disabled");
    }

    private void updateButton(IconButton button, Widgets.WidgetInfo icon, String key) {
        button.setIcon(icon);
        button.setMessage(translate(key));
        button.setTooltip(Tooltip.create(tooltip(key, key + ".description")));
    }

    private void toggleSetting(int setting) {
        sendSetting(setting, menu.getSetting(setting) ? 0 : 1);
    }

    private void submitPriorityField() {
        String value = priorityField.getValue();
        if (value.isEmpty() || value.equals("-")) {
            priorityField.setValue(String.valueOf(menu.getPriority()));
            return;
        }

        sendPriority(Integer.parseInt(value));
    }

    private void sendPriority(int priority) {
        int clampedPriority = Mth.clamp(priority, LootExtractorBlockEntity.MIN_PRIORITY, LootExtractorBlockEntity.MAX_PRIORITY);
        sendSetting(LootExtractorBlockEntity.SETTING_PRIORITY, clampedPriority);
        if (!priorityField.isFocused()) priorityField.setValue(String.valueOf(clampedPriority));
    }

    private void sendSetting(int setting, int value) {
        PacketHandler.sendPayload(new LootExtractorSettingPayload(menu.blockEntity.getBlockPos(), setting, value));
    }

    private MutableComponent tooltip(String titleKey, String bodyKey) {
        return translate(titleKey).copy().append("\n").append(translate(bodyKey).copy().withColor(GuiUtils.color("#A0A4AC")));
    }

    private Component translate(String key) {
        return Component.translatable("gui.soul_harvest.loot_extractor." + key);
    }
}
