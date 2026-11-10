package org.bsdevelopment.soul_harvest.utilities;

import net.minecraft.world.entity.Entity;
import org.bsdevelopment.soul_harvest.registry.ModAttachments;

public class Wraithfire {
    public static void ignite(Entity entity, float seconds) {
        if (entity.level().isClientSide() || entity.fireImmune()) return;

        entity.igniteForSeconds(seconds);
        markBurning(entity);
    }

    public static void markBurning(Entity entity) {
        if (entity.getRemainingFireTicks() <= 0 || isBurning(entity)) return;

        entity.setData(ModAttachments.WRAITHFIRE, true);
    }

    public static boolean isBurning(Entity entity) {
        return entity.hasData(ModAttachments.WRAITHFIRE) && entity.getData(ModAttachments.WRAITHFIRE);
    }
}
