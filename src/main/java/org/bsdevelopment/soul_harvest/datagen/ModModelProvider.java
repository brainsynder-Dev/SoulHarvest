package org.bsdevelopment.soul_harvest.datagen;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.HasComponent;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;
import org.bsdevelopment.soul_harvest.blocks.RiftPadBlock;
import org.bsdevelopment.soul_harvest.blocks.SoulAshBlock;
import org.bsdevelopment.soul_harvest.blocks.SoulBloomBlock;
import org.bsdevelopment.soul_harvest.blocks.WatcherBricksBlock;
import org.bsdevelopment.soul_harvest.blocks.base.RedstonePoweredBlock;
import org.bsdevelopment.soul_harvest.blocks.base.SpawnPadBlock;
import org.bsdevelopment.soul_harvest.client.render.WisdomVesselItemRenderer;
import org.bsdevelopment.soul_harvest.registry.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, ModConstants.MOD_ID);
    }

    private static Identifier blockModel(String name) {
        return Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "block/" + name);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return ModItems.ITEMS.getEntries().stream();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        List.of(ModBlocks.ALTAR_BRICKS.getBlock(), ModBlocks.SOUL_ALTAR_BRICKS.getBlock(), ModBlocks.WARDSTONE_TILES.getBlock(),
                ModBlocks.SOUL_STEEL_BLOCK.getBlock(), ModBlocks.SOUL_CRYSTAL_BLOCK.getBlock()).forEach(blockModels::createTrivialCube);

        SoulAshBlock soulAsh = ModBlocks.SOUL_ASH.getBlock();
        createSpawnPad(blockModels, soulAsh, ModelTemplates.CUBE_ALL.create(soulAsh, TextureMapping.cube(soulAsh), blockModels.modelOutput));

        SoulBloomBlock soulBloom = ModBlocks.SOUL_BLOOM.getBlock();
        createSpawnPad(blockModels, soulBloom, ModelTemplates.CUBE_BOTTOM_TOP.create(soulBloom, TextureMapping.cubeBottomTop(soulBloom).put(TextureSlot.BOTTOM, new Material(Identifier.withDefaultNamespace("block/dirt"))), blockModels.modelOutput));

        Block wraithfire = ModBlocks.WRAITHFIRE.get();
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(wraithfire, BlockModelGenerators.variants(
                BlockModelGenerators.plainModel(blockModel("wraithfire_floor0")),
                BlockModelGenerators.plainModel(blockModel("wraithfire_floor0")).with(BlockModelGenerators.Y_ROT_90),
                BlockModelGenerators.plainModel(blockModel("wraithfire_floor1")).with(BlockModelGenerators.Y_ROT_180),
                BlockModelGenerators.plainModel(blockModel("wraithfire_floor1")).with(BlockModelGenerators.Y_ROT_270)))
        );

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ModBlocks.SOUL_HARVEST_ALTAR.getBlock(), BlockModelGenerators.plainVariant(blockModel("soul_harvest_altar"))));
        blockModels.registerSimpleItemModel(ModBlocks.SOUL_HARVEST_ALTAR.getBlock(), blockModel("soul_harvest_altar_static"));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.LOOT_EXTRACTOR.getBlock(), BlockModelGenerators.plainVariant(blockModel("loot_extractor"))).with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING));

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ModBlocks.WISDOM_VESSEL.getBlock(), BlockModelGenerators.plainVariant(blockModel("wisdom_vessel"))));
        itemModels.itemModelOutput.accept(ModBlocks.WISDOM_VESSEL.getItem(), ItemModelUtils.composite(
                ItemModelUtils.plainModel(blockModel("wisdom_vessel")),
                ItemModelUtils.specialModel(blockModel("wisdom_vessel"), new WisdomVesselItemRenderer.Unbaked()))
        );

        createConnectedBlock(blockModels, ModBlocks.WITHER_WARD.getBlock(), "wither_ward");

        createHorizontalBlock(blockModels, ModBlocks.SOULSTREAM_PAD.getBlock(), "soulstream_pad");
        createHorizontalBlock(blockModels, ModBlocks.DESTINATION_PAD.getBlock(), "destination_pad");
        createToggleBlock(blockModels, ModBlocks.RIFT_PAD.getBlock(), RiftPadBlock.LINKED, "rift_pad_dormant", "rift_pad", "rift_pad");
        createToggleBlock(blockModels, ModBlocks.RIFT_ANCHOR.getBlock(), RedstonePoweredBlock.POWERED, "rift_anchor", "rift_anchor_off", "rift_anchor");
        createToggleBlock(blockModels, ModBlocks.GRAVITY_ANCHOR.getBlock(), RedstonePoweredBlock.POWERED, "gravity_anchor", "gravity_anchor_disabled", "gravity_anchor");

        Block watcherBricks = ModBlocks.WATCHER_BRICKS.getBlock();
        Material altarBricksTexture = new Material(blockModel("altar_bricks"));
        Identifier watcherBricksModel = ModelTemplates.CUBE_COLUMN.create(watcherBricks, TextureMapping.column(new Material(blockModel("watcher_bricks")), altarBricksTexture), blockModels.modelOutput);
        Identifier watcherBricksLitModel = ModelTemplates.CUBE_COLUMN.createWithSuffix(watcherBricks, "_lit", TextureMapping.column(new Material(blockModel("watcher_bricks_lit")), altarBricksTexture), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(watcherBricks).with(PropertyDispatch.initial(WatcherBricksBlock.LIT)
                .select(false, BlockModelGenerators.plainVariant(watcherBricksModel))
                .select(true, BlockModelGenerators.plainVariant(watcherBricksLitModel))));
        blockModels.registerSimpleItemModel(watcherBricks, watcherBricksModel);

        Block liquidExperience = ModBlocks.LIQUID_EXPERIENCE.get();
        Identifier liquidExperienceModel = ModelTemplates.PARTICLE_ONLY.create(liquidExperience, TextureMapping.particle(new Material(blockModel("liquid_experience_still"))), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(liquidExperience, BlockModelGenerators.plainVariant(liquidExperienceModel)));

        List.of(ModItems.SOUL_CRYSTAL.get(), ModItems.SOUL_STEEL_INGOT.get(), ModItems.SOUL_STEEL_NUGGET.get(), ModItems.SOUL_SAWBLADE.get(),
                ModItems.SOUL_ASH_SEED.get(), ModItems.SOUL_BLOOM_SEED.get(), ModItems.BLANK_SOUL_RUNE.get(), ModItems.LOOTING_SOUL_RUNE.get(), ModItems.SMITE_SOUL_RUNE.get(), ModItems.SHARPNESS_SOUL_RUNE.get(),
                ModItems.SOUL_FLAME_RUNE.get(), ModItems.BEHEADING_SOUL_RUNE.get(), ModItems.BANE_SOUL_RUNE.get(), ModItems.SPEED_SOUL_RUNE.get(), ModItems.WISDOM_SOUL_RUNE.get()
        ).forEach(item -> itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM));

        itemModels.generateFlatItem(ModItems.SOUL_PROBE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        Item filter = ModItems.FILTER.get();
        itemModels.generateBooleanDispatch(filter, new HasComponent(ModComponents.FILTER_BLACKLIST.get(), false),
                ItemModelUtils.plainModel(itemModels.createFlatItemModel(filter, "_blacklist", ModelTemplates.FLAT_ITEM)),
                ItemModelUtils.plainModel(itemModels.createFlatItemModel(filter, ModelTemplates.FLAT_ITEM)));

        itemModels.itemModelOutput.accept(ModItems.LIQUID_EXPERIENCE_BUCKET.get(), new DynamicFluidContainerModel.Unbaked(new DynamicFluidContainerModel.Textures(
                Optional.empty(),
                Optional.of(new Material(Identifier.withDefaultNamespace("item/bucket"))),
                Optional.of(new Material(Identifier.fromNamespaceAndPath("neoforge", "item/mask/bucket_fluid"))),
                Optional.empty()
        ), ModFluids.LIQUID_EXPERIENCE.get(), false, true, true));

        Item soulWand = ModItems.SOUL_WAND.get();
        ItemModel.Unbaked linkedWandModel = ItemModelUtils.plainModel(itemModels.createFlatItemModel(soulWand, "_linked", ModelTemplates.FLAT_HANDHELD_ITEM));
        itemModels.generateBooleanDispatch(soulWand, new HasComponent(ModComponents.LINKED_ALTAR.get(), false), linkedWandModel,
                ItemModelUtils.conditional(new HasComponent(ModComponents.LINKED_DESTINATION.get(), false), linkedWandModel,
                        ItemModelUtils.plainModel(itemModels.createFlatItemModel(soulWand, ModelTemplates.FLAT_HANDHELD_ITEM))));
    }

    private void createSpawnPad(BlockModelGenerators blockModels, SpawnPadBlock spawnPad, Identifier sleepingModel) {
        String blockName = BuiltInRegistries.BLOCK.getKey(spawnPad).getPath();
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(spawnPad).with(PropertyDispatch.initial(SpawnPadBlock.SIGIL).generate(sigilIndex -> {
            if (sigilIndex == 0) return BlockModelGenerators.plainVariant(sleepingModel);

            int size = SpawnPadBlock.getSigilSize(sigilIndex);
            int piece = sigilIndex - SpawnPadBlock.getSigilOffset(size) - 1;

            return BlockModelGenerators.plainVariant(blockModel("%s_sigil_%dx%d_%d_%d".formatted(blockName, size, size, piece % size, piece / size)));
        })));
        blockModels.registerSimpleItemModel(spawnPad, sleepingModel);
    }

    private void createConnectedBlock(BlockModelGenerators blockModels, Block block, String modelName) {
        Map<Direction, List<Direction>> faceEdges = Map.of(
                Direction.NORTH, List.of(Direction.UP, Direction.WEST, Direction.DOWN, Direction.EAST),
                Direction.SOUTH, List.of(Direction.UP, Direction.EAST, Direction.DOWN, Direction.WEST),
                Direction.EAST, List.of(Direction.UP, Direction.NORTH, Direction.DOWN, Direction.SOUTH),
                Direction.WEST, List.of(Direction.UP, Direction.SOUTH, Direction.DOWN, Direction.NORTH),
                Direction.UP, List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST),
                Direction.DOWN, List.of(Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST)
        );

        MultiPartGenerator generator = MultiPartGenerator.multiPart(block);
        for (Direction face : Direction.values()) {
            List<Direction> edges = faceEdges.get(face);
            for (int borders = 0; borders < 16; borders++) {
                ConditionBuilder condition = BlockModelGenerators.condition();
                for (int edge = 0; edge < edges.size(); edge++) condition.term(PipeBlock.PROPERTY_BY_DIRECTION.get(edges.get(edge)), (borders & (1 << edge)) == 0);

                generator.with(condition, BlockModelGenerators.plainVariant(blockModel("%s/%s_%02d".formatted(modelName, face.getSerializedName(), borders))));
            }
        }
        blockModels.blockStateOutput.accept(generator);
        blockModels.registerSimpleItemModel(block, blockModel(modelName + "_inventory"));
    }

    private void createHorizontalBlock(BlockModelGenerators blockModels, Block block, String modelName) {
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(blockModel(modelName))).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
        blockModels.registerSimpleItemModel(block, blockModel(modelName));
    }

    private void createToggleBlock(BlockModelGenerators blockModels, Block block, BooleanProperty property, String falseModelName, String trueModelName, String itemModelName) {
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(property)
                .select(false, BlockModelGenerators.plainVariant(blockModel(falseModelName)))
                .select(true, BlockModelGenerators.plainVariant(blockModel(trueModelName)))));
        blockModels.registerSimpleItemModel(block, blockModel(itemModelName));
    }
}
