package org.bsdevelopment.soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static final TagKey<Block> SOUL_ASH_CONVERTIBLE = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "soul_ash_convertible"));
    public static final TagKey<EntityType<?>> CLIMB_INHIBITED = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "climb_inhibited"));
    public static final TagKey<EntityType<?>> FLIGHT_INHIBITED = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "flight_inhibited"));
    public static final TagKey<EntityType<?>> RITUAL_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "soul_ritual_shrine_blacklist"));
}
