package org.bsdevelopment.soul_harvest.client.screen.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import org.bsdevelopment.soul_harvest.client.screen.GuiUtils;

import java.util.function.Consumer;

public class ArrowButton extends AbstractButton {
    private final boolean pointsRight;
    private final Consumer<InputWithModifiers> action;

    public ArrowButton(int x, int y, boolean pointsRight, Component message, Consumer<InputWithModifiers> action) {
        super(x, y, 10, 14, message);
        this.pointsRight = pointsRight;
        this.action = action;
        setTooltip(Tooltip.create(message));
    }

    @Override
    public void onPress(InputWithModifiers input) {
        action.accept(input);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Widgets.WidgetInfo sprite;
        if (pointsRight) {
            sprite = !active ? Widgets.ARROW_RIGHT_DISABLED : isHoveredOrFocused() ? Widgets.ARROW_RIGHT_HOVER : Widgets.ARROW_RIGHT_NORMAL;
        } else {
            sprite = !active ? Widgets.ARROW_LEFT_DISABLED : isHoveredOrFocused() ? Widgets.ARROW_LEFT_HOVER : Widgets.ARROW_LEFT_NORMAL;
        }

        GuiUtils.drawWidget(guiGraphics, sprite, getX(), getY());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
