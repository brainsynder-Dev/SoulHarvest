package org.bsdevelopment.soul_harvest.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.bsdevelopment.soul_harvest.blocks.entity.HushedMaskBlockEntity;
import org.bsdevelopment.soul_harvest.blocks.entity.HushedMaskBlockEntity.MuteType;
import org.bsdevelopment.soul_harvest.client.render.HushAreaRenderer;
import org.bsdevelopment.soul_harvest.client.screen.widgets.IconButton;
import org.bsdevelopment.soul_harvest.client.screen.widgets.Widgets;
import org.bsdevelopment.soul_harvest.network.HushSettingsPayload;
import org.bsdevelopment.soul_harvest.network.PacketHandler;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;

public class HushedMaskScreen extends Screen {
    private static final int IMAGE_WIDTH = 176;
    private static final int IMAGE_HEIGHT = 216;
    private static final int SIZE_ROW_Y = 63;
    private static final int SIZE_ROW_HEIGHT = 14;
    private static final int MUTE_ROW_Y = 121;
    private static final int MUTE_ROW_HEIGHT = 18;
    private static final int RESET_Y = 198;
    private static final String[] SIZE_KEYS = {"width", "length", "depth"};
    private final HushedMaskBlockEntity mask;
    private final int[] sizes;
    private final EnumSet<MuteType> mutedTypes = EnumSet.noneOf(MuteType.class);
    private IconButton showAreaButton;
    private boolean resetArmed = false;
    private int resetWidth;
    private int leftPos;
    private int topPos;

    public HushedMaskScreen(HushedMaskBlockEntity mask) {
        super(mask.getBlockState().getBlock().getName());
        this.mask = mask;
        sizes = new int[] {mask.getWidth(), mask.getHeight(), mask.getDepth()};
        for (MuteType muteType : MuteType.values()) if (mask.isMuted(muteType)) mutedTypes.add(muteType);
    }

    public static void open(HushedMaskBlockEntity mask) {
        Minecraft.getInstance().setScreen(new HushedMaskScreen(mask));
    }

    @Override
    protected void init() {
        leftPos = (width - IMAGE_WIDTH) / 2;
        topPos = (height - IMAGE_HEIGHT) / 2;
        resetWidth = Math.max(font.width(translate("reset")), font.width(translate("reset_confirm"))) + 10;

        Widgets.WidgetInfo showAreaIcon = HushAreaRenderer.isShown(mask.getBlockPos()) ? Widgets.ICON_CHECK : Widgets.ICON_CROSS;
        showAreaButton = addRenderableWidget(new IconButton(leftPos + 144, topPos + 19, showAreaIcon, translate("show_area"), this::toggleShowArea));
        showAreaButton.setTooltip(Tooltip.create(getTooltip("show_area")));
    }

    @Override
    public void tick() {
        if (mask.isRemoved() || (minecraft.player == null) || !minecraft.player.isWithinBlockInteractionRange(mask.getBlockPos(), 1)) onClose();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);

        Identifier texture = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/hushed_mask.png");
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, 256, 256);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        int titleColor = GuiUtils.color("#AEEBDC");
        int labelColor = GuiUtils.color("#A0A4AC");
        int mutedColor = GuiUtils.color("#70747E");

        guiGraphics.text(font, title, leftPos + 8, topPos + 5, titleColor, false);
        guiGraphics.item(new ItemStack(mask.getBlockState().getBlock()), leftPos + 11, topPos + 21);
        guiGraphics.text(font, translate("facing", translate("direction." + mask.getBlockState().getValue(BlockStateProperties.FACING).getSerializedName())), leftPos + 32, topPos + 20, titleColor, false);
        guiGraphics.text(font, translate("blocks", sizes[0] * sizes[1] * sizes[2]), leftPos + 32, topPos + 31, labelColor, false);

        int showLabelColor = showAreaButton.isHovered() ? -1 : labelColor;
        drawRightAlignedText(guiGraphics, translate("show"), 140, 20, showLabelColor);
        drawRightAlignedText(guiGraphics, translate("area"), 140, 31, showLabelColor);

        drawSectionHeader(guiGraphics, translate("area_section"), 49, mutedColor);
        drawSectionHeader(guiGraphics, translate("mute_section"), 107, mutedColor);

        for (int row = 0; row < SIZE_KEYS.length; row++) drawSizeSetting(guiGraphics, row, mouseX, mouseY, titleColor, labelColor);
        drawPreview(guiGraphics);
        for (MuteType muteType : MuteType.values()) drawMuteSetting(guiGraphics, muteType, mouseX, mouseY, titleColor, labelColor, mutedColor);

        guiGraphics.text(font, translate("max_size"), leftPos + 8, topPos + 202, mutedColor, false);
        drawResetButton(guiGraphics, mouseX, mouseY, titleColor, labelColor);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != 0) return super.mouseClicked(event, doubleClick);

        for (int row = 0; row < SIZE_KEYS.length; row++) {
            int rowY = SIZE_ROW_Y + row * SIZE_ROW_HEIGHT;
            if (isHovering(event.x(), event.y(), 58, rowY, 10, 14)) return changeSize(row, -1);
            if (isHovering(event.x(), event.y(), 86, rowY, 10, 14)) return changeSize(row, 1);
        }

        for (MuteType muteType : MuteType.values()) {
            if (!isHovering(event.x(), event.y(), 8, MUTE_ROW_Y + muteType.ordinal() * MUTE_ROW_HEIGHT, 154, MUTE_ROW_HEIGHT)) continue;

            if (!mutedTypes.remove(muteType)) mutedTypes.add(muteType);
            sendSettings();
            return true;
        }

        if (isHovering(event.x(), event.y(), 169 - resetWidth, RESET_Y, resetWidth, 14)) {
            resetSettings();
            return true;
        }

        resetArmed = false;
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (int row = 0; row < SIZE_KEYS.length; row++) {
            if (isHovering(mouseX, mouseY, 8, SIZE_ROW_Y + row * SIZE_ROW_HEIGHT, 90, SIZE_ROW_HEIGHT) && (scrollY != 0)) return changeSize(row, (int) Math.signum(scrollY));
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (super.keyPressed(event)) return true;
        if (!minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(event))) return false;

        onClose();
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void drawSizeSetting(GuiGraphicsExtractor guiGraphics, int row, int mouseX, int mouseY, int titleColor, int labelColor) {
        int rowY = SIZE_ROW_Y + row * SIZE_ROW_HEIGHT;
        boolean hovered = isHovering(mouseX, mouseY, 8, rowY, 90, SIZE_ROW_HEIGHT);
        if (hovered) GuiUtils.drawNineSlice(guiGraphics, Widgets.LIST_ROW_HOVER, leftPos + 8, topPos + rowY, 90, SIZE_ROW_HEIGHT, 1);
        if (hovered && hasDescription(SIZE_KEYS[row])) guiGraphics.setTooltipForNextFrame(font, Tooltip.splitTooltip(minecraft, getTooltip(SIZE_KEYS[row])), mouseX, mouseY);

        guiGraphics.text(font, translate(SIZE_KEYS[row]), leftPos + 10, topPos + rowY + 3, hovered ? -1 : labelColor, false);
        GuiUtils.drawWidget(guiGraphics, (sizes[row] <= 1) ? Widgets.ARROW_LEFT_DISABLED : isHovering(mouseX, mouseY, 58, rowY, 10, 14) ? Widgets.ARROW_LEFT_HOVER : Widgets.ARROW_LEFT_NORMAL, leftPos + 58, topPos + rowY);
        GuiUtils.drawWidget(guiGraphics, (sizes[row] >= HushedMaskBlockEntity.MAX_SIZE) ? Widgets.ARROW_RIGHT_DISABLED : isHovering(mouseX, mouseY, 86, rowY, 10, 14) ? Widgets.ARROW_RIGHT_HOVER : Widgets.ARROW_RIGHT_NORMAL, leftPos + 86, topPos + rowY);
        GuiUtils.drawNineSlice(guiGraphics, hovered ? Widgets.BOX_FOCUSED : Widgets.BOX_NORMAL, leftPos + 69, topPos + rowY, 16, SIZE_ROW_HEIGHT, 3);

        String value = String.valueOf(sizes[row]);
        guiGraphics.text(font, value, leftPos + 69 + (16 - font.width(value) + 1) / 2, topPos + rowY + 3, titleColor, false);
    }

    private void drawPreview(GuiGraphicsExtractor guiGraphics) {
        int cellSize = 6;
        int gridWidth = sizes[0] * cellSize + 1;
        int gridHeight = sizes[2] * cellSize + 1;
        int gridX = leftPos + 100 + (62 - gridWidth) / 2;
        int gridY = topPos + 67 + Math.floorDiv(32 - gridHeight, 2);
        int cellColor = HushAreaRenderer.isShown(mask.getBlockPos()) ? GuiUtils.color("#3CBAA8") : GuiUtils.color("#2C6E68");

        for (int row = 0; row < sizes[2]; row++) {
            for (int column = 0; column < sizes[0]; column++) {
                int cellX = gridX + column * cellSize + 1;
                int cellY = gridY + row * cellSize + 1;
                guiGraphics.fill(cellX, cellY, cellX + cellSize - 1, cellY + cellSize - 1, cellColor);
            }
        }

        int maskX = leftPos + 128;
        guiGraphics.fill(maskX, gridY + gridHeight + 1, maskX + 6, gridY + gridHeight + 3, GuiUtils.color("#C4C0B8"));
        guiGraphics.fill(maskX + 1, gridY + gridHeight + 2, maskX + 5, gridY + gridHeight + 3, GuiUtils.color("#22787A"));
    }

    private void drawMuteSetting(GuiGraphicsExtractor guiGraphics, MuteType muteType, int mouseX, int mouseY, int titleColor, int labelColor, int mutedColor) {
        String key = "mute." + muteType.name().toLowerCase(Locale.ROOT);
        int rowY = MUTE_ROW_Y + muteType.ordinal() * MUTE_ROW_HEIGHT;
        boolean hovered = isHovering(mouseX, mouseY, 8, rowY, 154, MUTE_ROW_HEIGHT);
        boolean muted = mutedTypes.contains(muteType);
        if (hovered) GuiUtils.drawNineSlice(guiGraphics, Widgets.LIST_ROW_HOVER, leftPos + 8, topPos + rowY, 154, MUTE_ROW_HEIGHT, 1);
        if (hovered && hasDescription(key)) guiGraphics.setTooltipForNextFrame(font, Tooltip.splitTooltip(minecraft, getTooltip(key)), mouseX, mouseY);

        guiGraphics.text(font, translate(key), leftPos + 10, topPos + rowY + 5, hovered ? -1 : labelColor, false);
        drawRightAlignedText(guiGraphics, translate(muted ? "on" : "off"), 140, rowY + 5, muted ? titleColor : mutedColor);
        GuiUtils.drawWidget(guiGraphics, muted ? Widgets.ICON_POWER_ON : Widgets.ICON_POWER_OFF, leftPos + 144, topPos + rowY + 1);
    }

    private void drawResetButton(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, int titleColor, int labelColor) {
        int resetX = 169 - resetWidth;
        boolean hovered = isHovering(mouseX, mouseY, resetX, RESET_Y, resetWidth, 14);
        GuiUtils.drawNineSlice(guiGraphics, resetArmed ? Widgets.BOX_FOCUSED : hovered ? Widgets.BOX_HOVER : Widgets.BOX_NORMAL, leftPos + resetX, topPos + RESET_Y, resetWidth, 14, 3);

        Component label = translate(resetArmed ? "reset_confirm" : "reset");
        guiGraphics.text(font, label, leftPos + resetX + (resetWidth - font.width(label) + 1) / 2, topPos + RESET_Y + 3, resetArmed ? titleColor : hovered ? -1 : labelColor, false);
    }

    private void drawSectionHeader(GuiGraphicsExtractor guiGraphics, Component header, int headerY, int headerColor) {
        guiGraphics.text(font, header, leftPos + 10, topPos + headerY + 4, headerColor, false);
        guiGraphics.fill(leftPos + 14 + font.width(header), topPos + headerY + 8, leftPos + 160, topPos + headerY + 9, GuiUtils.color("#34373E"));
    }

    private void drawRightAlignedText(GuiGraphicsExtractor guiGraphics, Component text, int rightX, int textY, int textColor) {
        guiGraphics.text(font, text, leftPos + rightX - font.width(text), topPos + textY, textColor, false);
    }

    private boolean changeSize(int row, int direction) {
        int newSize = Mth.clamp(sizes[row] + direction * ((row == 2) ? 1 : 2), 1, HushedMaskBlockEntity.MAX_SIZE);
        if (newSize == sizes[row]) return true;

        sizes[row] = newSize;
        sendSettings();
        return true;
    }

    private void resetSettings() {
        if (!resetArmed) {
            resetArmed = true;
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
            return;
        }

        Arrays.fill(sizes, HushedMaskBlockEntity.MAX_SIZE);
        mutedTypes.clear();
        mutedTypes.addAll(MuteType.getDefaultMutedTypes());
        sendSettings();
    }

    private void toggleShowArea() {
        HushAreaRenderer.toggle(mask.getBlockPos());
        showAreaButton.setIcon(HushAreaRenderer.isShown(mask.getBlockPos()) ? Widgets.ICON_CHECK : Widgets.ICON_CROSS);
    }

    private void sendSettings() {
        resetArmed = false;
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
        PacketHandler.sendPayload(new HushSettingsPayload(mask.getBlockPos(), sizes[0], sizes[1], sizes[2], mutedTypes));
    }

    private boolean isHovering(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + width && mouseY >= topPos + y && mouseY < topPos + y + height;
    }

    private boolean hasDescription(String key) {
        return I18n.exists("gui.soul_harvest.hushed_mask." + key + ".description");
    }

    private MutableComponent getTooltip(String key) {
        if (!hasDescription(key)) return translate(key).copy();

        return translate(key).copy().append("\n").append(translate(key + ".description").copy().withColor(GuiUtils.color("#A0A4AC")));
    }

    private Component translate(String key, Object... arguments) {
        return Component.translatable("gui.soul_harvest.hushed_mask." + key, arguments);
    }
}
