package org.bsdevelopment.soul_harvest.client.screen.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import org.bsdevelopment.soul_harvest.client.screen.GuiUtils;

public class IconButton extends AbstractButton {
    private final Runnable action;
    private Widgets.WidgetInfo icon;

    public IconButton(int x, int y, Widgets.WidgetInfo icon, Component message, Runnable action) {
        super(x, y, 20, 20, message);
        this.icon = icon;
        this.action = action;
        setTooltip(Tooltip.create(message));
    }

    public void setIcon(Widgets.WidgetInfo icon) {
        this.icon = icon;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        action.run();
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Widgets.WidgetInfo background = !active ? Widgets.BUTTON_DISABLED : isHoveredOrFocused() ? Widgets.BUTTON_HOVER : Widgets.BUTTON_NORMAL;
        GuiUtils.drawWidget(guiGraphics, background, getX(), getY());
        GuiUtils.drawWidget(guiGraphics, icon, getX() + 2, getY() + 2);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
