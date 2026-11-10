package org.bsdevelopment.soul_harvest.client.screen.widgets;

import net.minecraft.resources.Identifier;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public interface Widgets {
    Identifier WIDGETS_TEXTURE = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/widgets.png");

    WidgetInfo SLOT = new WidgetInfo(0, 0, 18, 18);
    WidgetInfo SLOT_RUNE = new WidgetInfo(20, 0, 18, 18);
    WidgetInfo SLOT_LOCKED = new WidgetInfo(40, 0, 18, 18);
    WidgetInfo SLOT_RUNE_GLOW = new WidgetInfo(60, 0, 18, 18);
    WidgetInfo SLOT_HOVER = new WidgetInfo(80, 0, 16, 16);
    WidgetInfo SLOT_FILTER = new WidgetInfo(98, 0, 18, 18);

    WidgetInfo BUTTON_NORMAL = new WidgetInfo(0, 22, 20, 20);
    WidgetInfo BUTTON_HOVER = new WidgetInfo(22, 22, 20, 20);
    WidgetInfo BUTTON_PRESSED = new WidgetInfo(44, 22, 20, 20);
    WidgetInfo BUTTON_DISABLED = new WidgetInfo(66, 22, 20, 20);

    WidgetInfo ARROW_RIGHT_NORMAL = new WidgetInfo(88, 22, 10, 14);
    WidgetInfo ARROW_RIGHT_HOVER = new WidgetInfo(100, 22, 10, 14);
    WidgetInfo ARROW_RIGHT_DISABLED = new WidgetInfo(112, 22, 10, 14);
    WidgetInfo ARROW_LEFT_NORMAL = new WidgetInfo(124, 22, 10, 14);
    WidgetInfo ARROW_LEFT_HOVER = new WidgetInfo(136, 22, 10, 14);
    WidgetInfo ARROW_LEFT_DISABLED = new WidgetInfo(148, 22, 10, 14);

    WidgetInfo ICON_CHECK = new WidgetInfo(0, 46, 16, 16);
    WidgetInfo ICON_CROSS = new WidgetInfo(18, 46, 16, 16);
    WidgetInfo ICON_INFO = new WidgetInfo(36, 46, 16, 16);
    WidgetInfo ICON_POWER_ON = new WidgetInfo(54, 46, 16, 16);
    WidgetInfo ICON_POWER_OFF = new WidgetInfo(72, 46, 16, 16);
    WidgetInfo ICON_XP_INSERT = new WidgetInfo(90, 46, 16, 16);
    WidgetInfo ICON_XP_INSERT_10 = new WidgetInfo(108, 46, 16, 16);
    WidgetInfo ICON_XP_INSERT_ALL = new WidgetInfo(126, 46, 16, 16);
    WidgetInfo ICON_XP_EXTRACT = new WidgetInfo(144, 46, 16, 16);
    WidgetInfo ICON_XP_EXTRACT_10 = new WidgetInfo(162, 46, 16, 16);
    WidgetInfo ICON_XP_EXTRACT_ALL = new WidgetInfo(180, 46, 16, 16);

    WidgetInfo GHOST_RUNE = new WidgetInfo(0, 66, 16, 16);
    WidgetInfo GHOST_SAW = new WidgetInfo(18, 66, 16, 16);
    WidgetInfo GHOST_BUCKET = new WidgetInfo(36, 66, 16, 16);
    WidgetInfo SMITE_RUNE_SLOT = new WidgetInfo(54, 66, 16, 16);
    WidgetInfo BANE_RUNE_SLOT = new WidgetInfo(72, 66, 16, 16);
    WidgetInfo SHARPNESS_RUNE_SLOT = new WidgetInfo(90, 66, 16, 16);
    WidgetInfo FLAME_RUNE_SLOT = new WidgetInfo(108, 66, 16, 16);
    WidgetInfo SPEED_RUNE_SLOT = new WidgetInfo(126, 66, 16, 16);
    WidgetInfo WISDOM_RUNE_SLOT = new WidgetInfo(144, 66, 16, 16);
    WidgetInfo LOOTING_RUNE_SLOT = new WidgetInfo(162, 66, 16, 16);
    WidgetInfo BEHEADING_RUNE_SLOT = new WidgetInfo(180, 66, 16, 16);
    WidgetInfo GHOST_FILTER = new WidgetInfo(198, 66, 16, 16);

    WidgetInfo PROGRESS_ARROW_EMPTY = new WidgetInfo(0, 86, 22, 16);
    WidgetInfo PROGRESS_ARROW_FULL = new WidgetInfo(24, 86, 22, 16);
    WidgetInfo ARROW_DOWN_EMPTY = new WidgetInfo(48, 86, 7, 9);
    WidgetInfo ARROW_DOWN_FULL = new WidgetInfo(57, 86, 7, 9);
    WidgetInfo SOUL_FLAME_EMPTY = new WidgetInfo(66, 86, 14, 14);
    WidgetInfo SOUL_FLAME_FULL = new WidgetInfo(82, 86, 14, 14);
    WidgetInfo SAW_IDLE = new WidgetInfo(98, 86, 16, 16);
    WidgetInfo SAW_SPIN = new WidgetInfo(116, 86, 16, 16);

    WidgetInfo SOUL_TANK_EMPTY = new WidgetInfo(0, 106, 16, 52);
    WidgetInfo SOUL_TANK_FILL = new WidgetInfo(18, 106, 14, 50);
    WidgetInfo XP_TANK_EMPTY = new WidgetInfo(34, 106, 24, 72);
    WidgetInfo XP_TANK_FILL = new WidgetInfo(60, 106, 22, 70);
    WidgetInfo XP_TANK_GAUGE = new WidgetInfo(84, 106, 22, 70);
    WidgetInfo SOUL_BAR_EMPTY = new WidgetInfo(110, 106, 104, 7);
    WidgetInfo SOUL_BAR_FILL = new WidgetInfo(110, 115, 102, 5);
    WidgetInfo XP_BAR_EMPTY = new WidgetInfo(110, 122, 86, 7);
    WidgetInfo XP_BAR_FILL = new WidgetInfo(110, 131, 84, 5);
    WidgetInfo XP_TANK_SURFACE = new WidgetInfo(110, 138, 22, 2);
    WidgetInfo TAB_ACTIVE = new WidgetInfo(110, 142, 28, 26);
    WidgetInfo TAB_INACTIVE = new WidgetInfo(140, 142, 28, 26);

    WidgetInfo ICON_WHITELIST = new WidgetInfo(0, 182, 16, 16);
    WidgetInfo ICON_BLACKLIST = new WidgetInfo(18, 182, 16, 16);
    WidgetInfo ICON_NBT_MATCH = new WidgetInfo(36, 182, 16, 16);
    WidgetInfo ICON_NBT_IGNORE = new WidgetInfo(54, 182, 16, 16);
    WidgetInfo NUMBER_FIELD = new WidgetInfo(72, 182, 36, 14);
    WidgetInfo NUMBER_FIELD_FOCUSED = new WidgetInfo(110, 182, 36, 14);

    WidgetInfo ICON_DAMAGE_MATCH = new WidgetInfo(0, 202, 16, 16);
    WidgetInfo ICON_DAMAGE_IGNORE = new WidgetInfo(18, 202, 16, 16);
    WidgetInfo SLOT_SELECTED = new WidgetInfo(36, 202, 18, 18);
    WidgetInfo GHOST_FILTER_SLOT = new WidgetInfo(56, 202, 16, 16);
    WidgetInfo TAG_BADGE = new WidgetInfo(74, 202, 7, 7);
    WidgetInfo DATA_BADGE = new WidgetInfo(83, 202, 7, 7);
    WidgetInfo LIST_PANEL = new WidgetInfo(92, 202, 20, 20);
    WidgetInfo SCROLL_TRACK = new WidgetInfo(114, 202, 4, 20);
    WidgetInfo SCROLL_THUMB = new WidgetInfo(120, 202, 4, 12);
    WidgetInfo CHEVRON_DOWN = new WidgetInfo(126, 202, 7, 4);
    WidgetInfo CHEVRON_UP = new WidgetInfo(135, 202, 7, 4);

    WidgetInfo BOX_NORMAL = new WidgetInfo(0, 226, 20, 14);
    WidgetInfo BOX_HOVER = new WidgetInfo(22, 226, 20, 14);
    WidgetInfo BOX_FOCUSED = new WidgetInfo(44, 226, 20, 14);
    WidgetInfo BOX_INVALID = new WidgetInfo(66, 226, 20, 14);
    WidgetInfo BOX_DISABLED = new WidgetInfo(88, 226, 20, 14);
    WidgetInfo LIST_ROW_HOVER = new WidgetInfo(110, 226, 20, 12);

    record WidgetInfo(int u, int v, int width, int height) {}
}
