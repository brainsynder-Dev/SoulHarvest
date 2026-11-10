package org.bsdevelopment.soul_harvest.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ARGB;
import org.bsdevelopment.soul_harvest.client.screen.widgets.Widgets;

public class GuiUtils {
    public static void drawWidget(GuiGraphicsExtractor guiGraphics, Widgets.WidgetInfo widget, int x, int y) {
        drawWidget(guiGraphics, widget, x, y, 0, 0, widget.width(), widget.height(), -1);
    }

    public static void drawWidget(GuiGraphicsExtractor guiGraphics, Widgets.WidgetInfo widget, int x, int y, int color) {
        drawWidget(guiGraphics, widget, x, y, 0, 0, widget.width(), widget.height(), color);
    }

    public static void drawWidget(GuiGraphicsExtractor guiGraphics, Widgets.WidgetInfo widget, int x, int y, int offsetX, int offsetY, int width, int height, int color) {
        if (width <= 0 || height <= 0) return;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Widgets.WIDGETS_TEXTURE, x + offsetX, y + offsetY, widget.u() + offsetX, widget.v() + offsetY, width, height, 256, 256, color);
    }

    public static void drawNineSlice(GuiGraphicsExtractor guiGraphics, Widgets.WidgetInfo widget, int x, int y, int width, int height, int border) {
        int centerWidth = widget.width() - border * 2, centerHeight = widget.height() - border * 2;
        int[][] columns = {{0, border, 0, border}, {border, centerWidth, border, width - border * 2}, {widget.width() - border, border, width - border, border}};
        int[][] rows = {{0, border, 0, border}, {border, centerHeight, border, height - border * 2}, {widget.height() - border, border, height - border, border}};

        for (int[] row : rows) {
            for (int[] column : columns) {
                if (column[3] <= 0 || row[3] <= 0) continue;
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Widgets.WIDGETS_TEXTURE, x + column[2], y + row[2], widget.u() + column[0], widget.v() + row[0], column[3], row[3], column[1], row[1], 256, 256);
            }
        }
    }

    public static int color(String hex) {
        return ARGB.opaque(TextColor.parseColor(hex).getOrThrow().getValue());
    }
}
