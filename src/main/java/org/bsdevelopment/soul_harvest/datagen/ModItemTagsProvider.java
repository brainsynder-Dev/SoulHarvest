package org.bsdevelopment.soul_harvest.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Registries.ITEM, registries, item -> item.builtInRegistryHolder().key(), ModConstants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {}
}
