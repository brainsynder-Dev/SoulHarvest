package org.bsdevelopment.soul_harvest.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModConstants;
import org.bsdevelopment.soul_harvest.registry.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, ModConstants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModBlocks.ALTAR_BRICKS.getBlock(),
                ModBlocks.SOUL_ALTAR_BRICKS.getBlock(),
                ModBlocks.SOUL_HARVEST_ALTAR.getBlock(),
                ModBlocks.LOOT_EXTRACTOR.getBlock(),
                ModBlocks.WISDOM_VESSEL.getBlock(),
                ModBlocks.WARDSTONE_TILES.getBlock(),
                ModBlocks.WITHER_WARD.getBlock(),
                ModBlocks.SOULSTREAM_PAD.getBlock(),
                ModBlocks.RIFT_PAD.getBlock(),
                ModBlocks.DESTINATION_PAD.getBlock(),
                ModBlocks.RIFT_ANCHOR.getBlock(),
                ModBlocks.GRAVITY_ANCHOR.getBlock(),
                ModBlocks.SOUL_RITUAL_SHRINE.getBlock(),
                ModBlocks.SOUL_STEEL_BLOCK.getBlock(),
                ModBlocks.SOUL_CRYSTAL_BLOCK.getBlock(),
                ModBlocks.WATCHER_BRICKS.getBlock()
        );

        tag(BlockTags.BEACON_BASE_BLOCKS).add(ModBlocks.SOUL_STEEL_BLOCK.getBlock(), ModBlocks.SOUL_CRYSTAL_BLOCK.getBlock());

        tag(BlockTags.NEEDS_DIAMOND_TOOL).add(ModBlocks.WITHER_WARD.getBlock());
        tag(BlockTags.WITHER_IMMUNE).add(ModBlocks.WITHER_WARD.getBlock());
        tag(BlockTags.DRAGON_IMMUNE).add(ModBlocks.WITHER_WARD.getBlock());

        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.SOUL_ASH.getBlock(), ModBlocks.SOUL_BLOOM.getBlock());

        tag(ModTags.SOUL_ASH_CONVERTIBLE).addTag(BlockTags.SUBSTRATE_OVERWORLD);
    }
}
