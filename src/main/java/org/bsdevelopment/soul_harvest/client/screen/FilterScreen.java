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
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.bsdevelopment.soul_harvest.client.screen.widgets.IconButton;
import org.bsdevelopment.soul_harvest.client.screen.widgets.Widgets;
import org.bsdevelopment.soul_harvest.component.FilterData;
import org.bsdevelopment.soul_harvest.container.FilterContainer;
import org.bsdevelopment.soul_harvest.network.FilterSettingPayload;
import org.bsdevelopment.soul_harvest.network.PacketHandler;
import org.bsdevelopment.soul_harvest.registry.ModComponents;
import org.bsdevelopment.soul_harvest.registry.ModConstants;
import org.bsdevelopment.soul_harvest.utilities.ItemFilterPredicates;

import java.util.ArrayList;
import java.util.List;

public class FilterScreen extends AbstractContainerScreen<FilterContainer> {
    private IconButton modeButton;
    private IconButton componentsButton;
    private IconButton damageButton;
    private EditBox dataField;
    private int selectedSlot = -1;
    private boolean dropdownOpen = false;
    private int dropdownScroll = 0;
    private boolean dataFieldWasFocused = false;
    private String dataError = null;
    private int lastButtonState = -1;

    public FilterScreen(FilterContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 206);
        titleLabelY = 5;
        inventoryLabelY = 112;
    }

    @Override
    protected void init() {
        super.init();
        lastButtonState = -1;

        modeButton = addRenderableWidget(new IconButton(leftPos + 72, topPos + 19, Widgets.ICON_WHITELIST, translate("whitelist"), () -> sendSetting(FilterContainer.SETTING_BLACKLIST, isBlacklist() ? 0 : 1, "")));
        componentsButton = addRenderableWidget(new IconButton(leftPos + 94, topPos + 19, Widgets.ICON_NBT_IGNORE, translate("nbt_off"), () -> sendSetting(FilterContainer.SETTING_MATCH_COMPONENTS, menu.getData().matchComponents() ? 0 : 1, "")));
        damageButton = addRenderableWidget(new IconButton(leftPos + 116, topPos + 19, Widgets.ICON_DAMAGE_MATCH, translate("damage_match"), () -> sendSetting(FilterContainer.SETTING_IGNORE_DAMAGE, menu.getData().ignoreDamage() ? 0 : 1, "")));

        dataField = new EditBox(font, leftPos + 11, topPos + 95, 154, 10, translate("data"));
        dataField.setBordered(false);
        dataField.setMaxLength(256);
        dataField.setTextColor(GuiUtils.color("#F0C45C"));
        dataField.setHint(translate("data.hint").copy().withColor(GuiUtils.color("#60646C")));
        dataField.setResponder(this::validateData);
        addRenderableWidget(dataField);

        loadSelectedSlot();
        updateButtons();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (dataFieldWasFocused && !dataField.isFocused()) submitData();

        dataFieldWasFocused = dataField.isFocused();
        dataField.setEditable(hasSelectedEntry());
        updateButtons();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        if (dropdownOpen) {
            drawDropdownList(guiGraphics, mouseX, mouseY);
        } else {
            drawTooltips(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Identifier texture = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/filter.png");
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        for (int index = 0; index < FilterData.SIZE; index++) {
            int x = leftPos + 12 + (index % 3) * 18, y = topPos + 20 + (index / 3) * 18;
            if (menu.slots.get(index).getItem().isEmpty()) GuiUtils.drawWidget(guiGraphics, Widgets.GHOST_FILTER_SLOT, x, y);

            if (index == selectedSlot) GuiUtils.drawWidget(guiGraphics, Widgets.SLOT_SELECTED, x - 1, y - 1);
        }

        ItemStack selected = selectedStack();
        if (!selected.isEmpty()) guiGraphics.item(selected, leftPos + 146, topPos + 21);

        boolean hoverDropdown = isOver(mouseX, mouseY, 72, 55, 94, 14);
        Widgets.WidgetInfo dropdownBox = !hasSelectedEntry() ? Widgets.BOX_DISABLED : dropdownOpen ? Widgets.BOX_FOCUSED : hoverDropdown ? Widgets.BOX_HOVER : Widgets.BOX_NORMAL;
        GuiUtils.drawNineSlice(guiGraphics, dropdownBox, leftPos + 72, topPos + 55, 94, 14, 3);
        if (hasSelectedEntry()) guiGraphics.text(font, font.plainSubstrByWidth(currentTagLabel(), 76), leftPos + 76, topPos + 58, GuiUtils.color("#AEEBDC"), false);

        GuiUtils.drawWidget(guiGraphics, dropdownOpen ? Widgets.CHEVRON_UP : Widgets.CHEVRON_DOWN, leftPos + 72 + 83, topPos + 55 + 5);

        Widgets.WidgetInfo dataBox = !hasSelectedEntry() ? Widgets.BOX_DISABLED : dataError != null ? Widgets.BOX_INVALID : dataField.isFocused() ? Widgets.BOX_FOCUSED : Widgets.BOX_NORMAL;
        GuiUtils.drawNineSlice(guiGraphics, dataBox, leftPos + 7, topPos + 92, 162, 14, 3);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int labelColor = GuiUtils.color("#A0A4AC");
        guiGraphics.text(font, title, titleLabelX, titleLabelY, GuiUtils.color("#AEEBDC"), false);
        guiGraphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, labelColor, false);

        String slotNumber = selectedSlot >= 0 ? String.valueOf(selectedSlot + 1) : "-";
        guiGraphics.text(font, Component.translatable("gui.soul_harvest.filter.tag", slotNumber), 73, 45, labelColor, false);
        guiGraphics.text(font, Component.translatable("gui.soul_harvest.filter.data_label", slotNumber), 8, 82, labelColor, false);

        FilterData data = menu.getData();
        for (int index = 0; index < FilterData.SIZE; index++) {
            int x = 12 + (index % 3) * 18, y = 20 + (index / 3) * 18;
            FilterData.Entry entry = data.entry(index);
            if (entry.tag().isPresent()) GuiUtils.drawWidget(guiGraphics, Widgets.TAG_BADGE, x + 9, y);
            if (!entry.data().isEmpty()) GuiUtils.drawWidget(guiGraphics, Widgets.DATA_BADGE, x, y);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (dropdownOpen) {
            int row = dropdownRowAt(event.x(), event.y());
            if (row >= 0) {
                List<String> options = tagOptions();
                sendSetting(FilterContainer.SETTING_TAG, 0, row == 0 ? "" : options.get(row));
            }

            dropdownOpen = false;
            return true;
        }
        if (hasSelectedEntry() && isOver(event.x(), event.y(), 72, 55, 94, 14)) {
            dropdownOpen = true;
            dropdownScroll = 0;
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!dropdownOpen) return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

        int maxScroll = Math.max(0, tagOptions().size() - 6);
        dropdownScroll = Math.max(0, Math.min(maxScroll, dropdownScroll - (int) Math.signum(scrollY)));
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (dropdownOpen && event.isEscape()) {
            dropdownOpen = false;
            return true;
        }

        if (!dataField.isFocused() || event.isEscape()) return super.keyPressed(event);
        if (event.isConfirmation()) {
            dataField.setFocused(false);
            setFocused(null);
            return true;
        }

        dataField.keyPressed(event);
        return true;
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ContainerInput input) {
        if (slot != null && menu.isGhostSlot(slotId) && mouseButton == 0 && input == ContainerInput.PICKUP && menu.getCarried().isEmpty()) {
            selectedSlot = slotId;
            dropdownOpen = false;
            loadSelectedSlot();
            return;
        }

        super.slotClicked(slot, slotId, mouseButton, input);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void drawDropdownList(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        List<String> options = tagOptions();
        int visibleRows = Math.min(6, options.size());
        int x = leftPos + 72, y = topPos + 69;
        guiGraphics.nextStratum();
        GuiUtils.drawNineSlice(guiGraphics, Widgets.LIST_PANEL, x, y, 94, visibleRows * 12 + 4, 3);

        int hoveredRow = dropdownRowAt(mouseX, mouseY);
        for (int row = 0; row < visibleRows; row++) {
            int optionIndex = row + dropdownScroll;
            int rowY = y + 2 + row * 12;
            if (optionIndex == hoveredRow) GuiUtils.drawNineSlice(guiGraphics, Widgets.LIST_ROW_HOVER, x + 2, rowY, 84, 12, 1);

            guiGraphics.text(font, font.plainSubstrByWidth(optionLabel(options, optionIndex), 80), x + 4, rowY + 2, GuiUtils.color("#AEEBDC"), false);
        }

        if (options.size() > 6) {
            int trackHeight = visibleRows * 12;
            GuiUtils.drawNineSlice(guiGraphics, Widgets.SCROLL_TRACK, x + 87, y + 2, 4, trackHeight, 1);
            int thumbY = y + 2 + (trackHeight - 12) * dropdownScroll / Math.max(1, options.size() - 6);
            GuiUtils.drawWidget(guiGraphics, Widgets.SCROLL_THUMB, x + 87, thumbY);
        }

        if (hoveredRow > 0) guiGraphics.setTooltipForNextFrame(font, Component.literal("#" + options.get(hoveredRow)), mouseX, mouseY);
    }

    private void drawTooltips(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if (isOver(mouseX, mouseY, 7, 92, 162, 14) && hasSelectedEntry()) {
            MutableComponent tooltip = dataError != null
                    ? tooltip("data.invalid", null).append("\n").append(Component.literal(dataError).withColor(GuiUtils.color("#E66E6E")))
                    : tooltip("data", "data.description");

            guiGraphics.setTooltipForNextFrame(font, tooltip, mouseX, mouseY);
        }

        if (hoveredSlot != null && menu.isGhostSlot(hoveredSlot.index) && !hoveredSlot.hasItem() && menu.getCarried().isEmpty()) guiGraphics.setTooltipForNextFrame(font, translate("filter_slot"), mouseX, mouseY);
    }

    private int dropdownRowAt(double mouseX, double mouseY) {
        List<String> options = tagOptions();
        int visibleRows = Math.min(6, options.size());
        if (!isOver(mouseX, mouseY, 72, 71, 86, visibleRows * 12)) return -1;

        int row = (int) (mouseY - topPos - 71) / 12 + dropdownScroll;
        return row < options.size() ? row : -1;
    }

    private List<String> tagOptions() {
        List<String> options = new ArrayList<>();
        options.add("");
        ItemStack stack = selectedStack();
        if (!stack.isEmpty()) stack.typeHolder().tags().map(tag -> tag.location().toString()).sorted().forEach(options::add);

        return options;
    }

    private String optionLabel(List<String> options, int index) {
        if (index == 0) return translate("exact_item").getString();

        return "#" + options.get(index).replace("minecraft:", "");
    }

    private String currentTagLabel() {
        return menu.getData().entry(selectedSlot).tag().map(TagKey::location).map(id -> "#" + id.toString().replace("minecraft:", "")).orElse(translate("exact_item").getString());
    }

    private void loadSelectedSlot() {
        if (dataField == null) return;

        dataField.setValue(hasSelectedEntry() ? menu.getData().entry(selectedSlot).data() : "");
        dataError = null;
    }

    private void validateData(String text) {
        dataError = minecraft.level == null ? null : ItemFilterPredicates.validate(text, minecraft.level);
    }

    private void submitData() {
        if (!hasSelectedEntry() || dataError != null) return;
        if (dataField.getValue().equals(menu.getData().entry(selectedSlot).data())) return;

        sendSetting(FilterContainer.SETTING_DATA, 0, dataField.getValue());
    }

    private void updateButtons() {
        boolean blacklist = isBlacklist();
        boolean matchComponents = menu.getData().matchComponents();
        boolean ignoreDamage = menu.getData().ignoreDamage();
        int buttonState = (blacklist ? 1 : 0) | (matchComponents ? 2 : 0) | (ignoreDamage ? 4 : 0);
        if (buttonState == lastButtonState) return;
        lastButtonState = buttonState;

        updateButton(modeButton, blacklist ? Widgets.ICON_BLACKLIST : Widgets.ICON_WHITELIST, blacklist ? "blacklist" : "whitelist");
        updateButton(componentsButton, matchComponents ? Widgets.ICON_NBT_MATCH : Widgets.ICON_NBT_IGNORE, matchComponents ? "nbt_on" : "nbt_off");
        updateButton(damageButton, ignoreDamage ? Widgets.ICON_DAMAGE_IGNORE : Widgets.ICON_DAMAGE_MATCH, ignoreDamage ? "damage_ignore" : "damage_match");
        damageButton.active = matchComponents;
    }

    private void updateButton(IconButton button, Widgets.WidgetInfo icon, String key) {
        button.setIcon(icon);
        button.setMessage(translate(key));
        button.setTooltip(Tooltip.create(tooltip(key, key + ".description")));
    }

    private void sendSetting(int setting, int value, String text) {
        PacketHandler.sendPayload(new FilterSettingPayload(setting, Math.max(0, selectedSlot), value, text));
    }

    private boolean isBlacklist() {
        return menu.getFilter().has(ModComponents.FILTER_BLACKLIST.get());
    }

    private boolean hasSelectedEntry() {
        return selectedSlot >= 0 && !menu.getData().entry(selectedSlot).isEmpty();
    }

    private ItemStack selectedStack() {
        return selectedSlot >= 0 ? menu.getData().entry(selectedSlot).stack() : ItemStack.EMPTY;
    }

    private boolean isOver(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= leftPos + x && mouseY >= topPos + y && mouseX < leftPos + x + width && mouseY < topPos + y + height;
    }

    private MutableComponent tooltip(String titleKey, String bodyKey) {
        MutableComponent tooltip = translate(titleKey).copy();
        if (bodyKey != null) tooltip.append("\n").append(translate(bodyKey).copy().withColor(GuiUtils.color("#A0A4AC")));

        return tooltip;
    }

    private Component translate(String key) {
        return Component.translatable("gui.soul_harvest.filter." + key);
    }
}
