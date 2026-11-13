package org.bsdevelopment.soul_harvest.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.bsdevelopment.soul_harvest.blocks.*;
import org.bsdevelopment.soul_harvest.blocks.base.RedstonePoweredBlock;
import org.bsdevelopment.soul_harvest.blocks.entity.*;
import org.bsdevelopment.soul_harvest.items.base.BlockItemHandler;
import org.bsdevelopment.soul_harvest.utilities.BlockRegistration;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ModConstants.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModConstants.MOD_ID);

    public static final BlockRegistration<Block, BlockItemHandler, ?> ALTAR_BRICKS = new BlockRegistration<>(
            "altar_bricks",
            props -> new Block(props.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1.5f, 6f).sound(SoundType.POLISHED_DEEPSLATE)),
            BlockItemHandler::new
    );

    public static final BlockRegistration<Block, BlockItemHandler, ?> SOUL_ALTAR_BRICKS = new BlockRegistration<>(
            "soul_altar_bricks",
            props -> new Block(props.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1.5f, 6f).sound(SoundType.POLISHED_DEEPSLATE)),
            BlockItemHandler::new
    );

    public static final BlockRegistration<SoulHarvestAltarBlock, BlockItemHandler, SoulHarvestAltarBlockEntity> SOUL_HARVEST_ALTAR = new BlockRegistration<>(
            "soul_harvest_altar",
            props -> new SoulHarvestAltarBlock(props.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(3.5f, 6f).noOcclusion().sound(SoundType.POLISHED_DEEPSLATE)),
            BlockItemHandler::new,
            SoulHarvestAltarBlockEntity::new
    );

    public static final BlockRegistration<LootExtractorBlock, BlockItemHandler, LootExtractorBlockEntity> LOOT_EXTRACTOR = new BlockRegistration<>(
            "loot_extractor",
            props -> new LootExtractorBlock(props.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(3f, 6f).noOcclusion().sound(SoundType.POLISHED_DEEPSLATE)),
            BlockItemHandler::new,
            LootExtractorBlockEntity::new
    );

    public static final BlockRegistration<WisdomVesselBlock, BlockItemHandler, WisdomVesselBlockEntity> WISDOM_VESSEL = new BlockRegistration<>(
            "wisdom_vessel",
            props -> new WisdomVesselBlock(props.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(3f, 6f).noOcclusion().sound(SoundType.GLASS)),
            BlockItemHandler::new,
            WisdomVesselBlockEntity::new
    );

    public static final BlockRegistration<SoulAshBlock, BlockItemHandler, ?> SOUL_ASH = new BlockRegistration<>(
            "soul_ash",
            props -> new SoulAshBlock(props.mapColor(MapColor.COLOR_BLACK).strength(0.8f).sound(SoundType.SOUL_SOIL).isValidSpawn(SoulAshBlock::onlyHostiles)),
            BlockItemHandler::new
    );

    public static final BlockRegistration<SoulBloomBlock, BlockItemHandler, ?> SOUL_BLOOM = new BlockRegistration<>(
            "soul_bloom",
            props -> new SoulBloomBlock(props.mapColor(MapColor.COLOR_CYAN).strength(0.6f).sound(SoundType.MOSS).isValidSpawn(SoulBloomBlock::onlyPassives)),
            BlockItemHandler::new
    );

    public static final BlockRegistration<Block, BlockItemHandler, ?> WARDSTONE_TILES = new BlockRegistration<>(
            "wardstone_tiles",
            props -> new Block(props.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1.5f, 6f).sound(SoundType.POLISHED_DEEPSLATE)),
            BlockItemHandler::new
    );

    public static final BlockRegistration<WitherWardBlock, BlockItemHandler, ?> WITHER_WARD = new BlockRegistration<>(
            "wither_ward",
            props -> new WitherWardBlock(props.mapColor(MapColor.COLOR_CYAN).requiresCorrectToolForDrops().strength(40f, 3600000f).lightLevel(state -> 6)
                    .sound(SoundType.GLASS).pushReaction(PushReaction.BLOCK).noOcclusion().isValidSpawn(Blocks::never).isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false).isViewBlocking((state, level, pos) -> false)),
            BlockItemHandler::new
    );

    public static final BlockRegistration<SoulstreamPadBlock, BlockItemHandler, ?> SOULSTREAM_PAD = new BlockRegistration<>(
            "soulstream_pad",
            props -> new SoulstreamPadBlock(props.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1.5f, 6f).sound(SoundType.POLISHED_DEEPSLATE)),
            BlockItemHandler::new
    );

    public static final BlockRegistration<RiftPadBlock, BlockItemHandler, RiftPadBlockEntity> RIFT_PAD = new BlockRegistration<>(
            "rift_pad",
            props -> new RiftPadBlock(props.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1.5f, 6f).sound(SoundType.POLISHED_DEEPSLATE)),
            BlockItemHandler::new,
            RiftPadBlockEntity::new
    );

    public static final BlockRegistration<DestinationPadBlock, BlockItemHandler, ?> DESTINATION_PAD = new BlockRegistration<>(
            "destination_pad",
            props -> new DestinationPadBlock(props.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1.5f, 6f).sound(SoundType.POLISHED_DEEPSLATE)),
            BlockItemHandler::new
    );

    public static final BlockRegistration<RiftAnchorBlock, BlockItemHandler, RiftAnchorBlockEntity> RIFT_ANCHOR = new BlockRegistration<>(
            "rift_anchor",
            props -> new RiftAnchorBlock(props.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(3.5f, 6f).noOcclusion().sound(SoundType.POLISHED_DEEPSLATE)),
            BlockItemHandler::new,
            RiftAnchorBlockEntity::new
    );

    public static final BlockRegistration<GravityAnchorBlock, BlockItemHandler, GravityAnchorBlockEntity> GRAVITY_ANCHOR = new BlockRegistration<>(
            "gravity_anchor",
            props -> new GravityAnchorBlock(props.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(3.5f, 6f).noOcclusion().lightLevel(state -> state.getValue(RedstonePoweredBlock.POWERED) ? 0 : 4).sound(SoundType.POLISHED_DEEPSLATE)),
            BlockItemHandler::new,
            GravityAnchorBlockEntity::new
    );

    public static final BlockRegistration<SoulRitualShrineBlock, BlockItemHandler, SoulRitualShrineBlockEntity> SOUL_RITUAL_SHRINE = new BlockRegistration<>(
            "soul_ritual_shrine",
            props -> new SoulRitualShrineBlock(props.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(3.5f, 9f).noOcclusion().lightLevel(state -> state.getValue(SoulRitualShrineBlock.ACTIVE) ? 12 : 5).sound(SoundType.POLISHED_DEEPSLATE)),
            BlockItemHandler::new,
            SoulRitualShrineBlockEntity::new
    );

    public static final BlockRegistration<Block, BlockItemHandler, ?> SOUL_STEEL_BLOCK = new BlockRegistration<>(
            "soul_steel_block",
            props -> new Block(props.mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
            BlockItemHandler::new
    );

    public static final BlockRegistration<Block, BlockItemHandler, ?> SOUL_CRYSTAL_BLOCK = new BlockRegistration<>(
            "soul_crystal_block",
            props -> new Block(props.mapColor(MapColor.COLOR_CYAN).requiresCorrectToolForDrops().strength(1.5f).lightLevel(state -> 12).sound(SoundType.AMETHYST)),
            BlockItemHandler::new
    );

    public static final BlockRegistration<WatcherBricksBlock, BlockItemHandler, WatcherBricksBlockEntity> WATCHER_BRICKS = new BlockRegistration<>(
            "watcher_bricks",
            props -> new WatcherBricksBlock(props.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(2f, 6f).sound(SoundType.DEEPSLATE_BRICKS)),
            BlockItemHandler::new,
            WatcherBricksBlockEntity::new
    );

    public static final DeferredBlock<LiquidBlock> LIQUID_EXPERIENCE = BLOCKS.registerBlock("liquid_experience", props -> new LiquidBlock(ModFluids.LIQUID_EXPERIENCE.get(), props), props -> props.mapColor(MapColor.COLOR_LIGHT_GREEN).replaceable().noCollision().strength(100f).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY));
    public static final DeferredBlock<WraithfireBlock> WRAITHFIRE = BLOCKS.registerBlock("wraithfire", WraithfireBlock::new, props -> props.mapColor(MapColor.COLOR_CYAN).replaceable().noCollision().instabreak().lightLevel(state -> 10).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY).noLootTable());

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        TILE_ENTITIES.register(eventBus);
    }
}
