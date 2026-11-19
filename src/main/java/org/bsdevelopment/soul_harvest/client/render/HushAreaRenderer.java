package org.bsdevelopment.soul_harvest.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bsdevelopment.soul_harvest.blocks.entity.HushedMaskBlockEntity;
import org.bsdevelopment.soul_harvest.client.screen.GuiUtils;

import java.util.HashSet;
import java.util.Set;

public class HushAreaRenderer {
    private static final Set<BlockPos> SHOWN_MASKS = new HashSet<>();

    public static boolean isShown(BlockPos maskPos) {
        return SHOWN_MASKS.contains(maskPos);
    }

    public static void toggle(BlockPos maskPos) {
        if (!SHOWN_MASKS.remove(maskPos)) SHOWN_MASKS.add(maskPos.immutable());
    }

    public static void drawShownAreas() {
        if (SHOWN_MASKS.isEmpty()) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            SHOWN_MASKS.clear();
            return;
        }

        int areaColor = GuiUtils.color("#63E3C7");
        SHOWN_MASKS.removeIf(maskPos -> !(minecraft.level.getBlockEntity(maskPos) instanceof HushedMaskBlockEntity));
        for (BlockPos maskPos : SHOWN_MASKS) {
            if (!(minecraft.level.getBlockEntity(maskPos) instanceof HushedMaskBlockEntity mask)) continue;

            AABB area = mask.getArea();
            Gizmos.cuboid(area.inflate(0.002), GizmoStyle.strokeAndFill(ARGB.opaque(areaColor), 2.5f, ARGB.color(0x48, areaColor))).setAlwaysOnTop();
            drawGrid(area, ARGB.color(0x99, areaColor));
        }
    }

    private static void drawGrid(AABB area, int lineColor) {
        for (double x = area.minX + 1; x < area.maxX; x++) {
            drawLine(x, area.minY, area.minZ, x, area.maxY, area.minZ, lineColor);
            drawLine(x, area.minY, area.maxZ, x, area.maxY, area.maxZ, lineColor);
            drawLine(x, area.minY, area.minZ, x, area.minY, area.maxZ, lineColor);
            drawLine(x, area.maxY, area.minZ, x, area.maxY, area.maxZ, lineColor);
        }

        for (double y = area.minY + 1; y < area.maxY; y++) {
            drawLine(area.minX, y, area.minZ, area.maxX, y, area.minZ, lineColor);
            drawLine(area.minX, y, area.maxZ, area.maxX, y, area.maxZ, lineColor);
            drawLine(area.minX, y, area.minZ, area.minX, y, area.maxZ, lineColor);
            drawLine(area.maxX, y, area.minZ, area.maxX, y, area.maxZ, lineColor);
        }

        for (double z = area.minZ + 1; z < area.maxZ; z++) {
            drawLine(area.minX, area.minY, z, area.maxX, area.minY, z, lineColor);
            drawLine(area.minX, area.maxY, z, area.maxX, area.maxY, z, lineColor);
            drawLine(area.minX, area.minY, z, area.minX, area.maxY, z, lineColor);
            drawLine(area.maxX, area.minY, z, area.maxX, area.maxY, z, lineColor);
        }
    }

    private static void drawLine(double startX, double startY, double startZ, double endX, double endY, double endZ, int lineColor) {
        Gizmos.line(new Vec3(startX, startY, startZ), new Vec3(endX, endY, endZ), lineColor, 1f).setAlwaysOnTop();
    }
}
