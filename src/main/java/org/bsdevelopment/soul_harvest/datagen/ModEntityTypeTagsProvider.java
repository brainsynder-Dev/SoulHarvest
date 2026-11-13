package org.bsdevelopment.soul_harvest.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
import org.bsdevelopment.soul_harvest.registry.ModConstants;
import org.bsdevelopment.soul_harvest.registry.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends IntrinsicHolderTagsProvider<EntityType<?>> {
    public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Registries.ENTITY_TYPE, registries, entityType -> entityType.builtInRegistryHolder().key(), ModConstants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(ModTags.CLIMB_INHIBITED).add(
                EntityType.SPIDER,
                EntityType.CAVE_SPIDER
        );

        tag(ModTags.FLIGHT_INHIBITED).add(
                EntityType.PHANTOM,
                EntityType.GHAST,
                EntityType.BLAZE,
                EntityType.VEX,
                EntityType.BAT,
                EntityType.BEE,
                EntityType.PARROT,
                EntityType.ALLAY
        );

        tag(ModTags.RITUAL_BLACKLIST).addTag(Tags.EntityTypes.BOSSES).add(
                EntityType.WARDEN,
                EntityType.ELDER_GUARDIAN
        );
    }
}
