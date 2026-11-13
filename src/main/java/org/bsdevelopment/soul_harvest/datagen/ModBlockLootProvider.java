package org.bsdevelopment.soul_harvest.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModComponents;

import java.util.List;
import java.util.Set;

public class ModBlockLootProvider extends BlockLootSubProvider {
    public ModBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        List.of(ModBlocks.ALTAR_BRICKS.getBlock(), ModBlocks.SOUL_ALTAR_BRICKS.getBlock(), ModBlocks.SOUL_HARVEST_ALTAR.getBlock(), ModBlocks.LOOT_EXTRACTOR.getBlock(),
                ModBlocks.SOUL_ASH.getBlock(), ModBlocks.SOUL_BLOOM.getBlock(), ModBlocks.WARDSTONE_TILES.getBlock(), ModBlocks.WITHER_WARD.getBlock(), ModBlocks.SOULSTREAM_PAD.getBlock(), ModBlocks.RIFT_PAD.getBlock(),
                ModBlocks.DESTINATION_PAD.getBlock(), ModBlocks.RIFT_ANCHOR.getBlock(), ModBlocks.GRAVITY_ANCHOR.getBlock(), ModBlocks.SOUL_RITUAL_SHRINE.getBlock(), ModBlocks.SOUL_STEEL_BLOCK.getBlock(),
                ModBlocks.SOUL_CRYSTAL_BLOCK.getBlock(), ModBlocks.WATCHER_BRICKS.getBlock()
        ).forEach(this::dropSelf);

        add(ModBlocks.WISDOM_VESSEL.getBlock(), block -> LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(block).apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                        .include(ModComponents.FLUID_CONTENTS.get())
                ))))
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value).toList();
    }
}
