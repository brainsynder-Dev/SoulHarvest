package org.bsdevelopment.soul_harvest.container;

import net.minecraft.world.item.Item;
import org.bsdevelopment.soul_harvest.client.screen.widgets.Widgets;

public record SlotPosition(int x, int y, Widgets.WidgetInfo ghostWidget, Item item) {}
