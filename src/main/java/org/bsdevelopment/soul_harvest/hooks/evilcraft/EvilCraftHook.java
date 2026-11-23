package org.bsdevelopment.soul_harvest.hooks.evilcraft;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.bsdevelopment.soul_harvest.config.ModConfig;

import java.util.List;
import java.util.Optional;

public class EvilCraftHook {
    public static void voidVengeanceSpirits(LivingEntity entity) {
        if (!ModConfig.voidVengeanceSpirits) return;

        Optional<EntityType<?>> vengeanceSpiritType = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("evilcraft", "vengeance_spirit"));
        if (vengeanceSpiritType.isEmpty()) return;

        List<Entity> vengeanceSpirits = entity.level().getEntities(entity, entity.getBoundingBox().inflate(1), nearbyEntity -> (nearbyEntity.getType() == vengeanceSpiritType.get()) && (nearbyEntity.tickCount == 0));
        vengeanceSpirits.forEach(Entity::discard);
    }
}
