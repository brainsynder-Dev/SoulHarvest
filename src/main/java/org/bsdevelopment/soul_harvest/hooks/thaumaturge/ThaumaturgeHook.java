package org.bsdevelopment.soul_harvest.hooks.thaumaturge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

public class ThaumaturgeHook {
    public static void voidAspectOrbs(LivingEntity entity) {
        Optional<EntityType<?>> aspectOrbType = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("thaumaturge", "aspect_orb"));
        if (aspectOrbType.isEmpty()) return;

        List<Entity> aspectOrbs = entity.level().getEntities(entity, entity.getBoundingBox().inflate(1), nearbyEntity -> (nearbyEntity.getType() == aspectOrbType.get()) && (nearbyEntity.tickCount == 0));
        aspectOrbs.forEach(Entity::discard);
    }
}
