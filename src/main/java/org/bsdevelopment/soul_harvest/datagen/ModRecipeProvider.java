package org.bsdevelopment.soul_harvest.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModConstants;
import org.bsdevelopment.soul_harvest.registry.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        buildItemRecipes();
        buildBlockRecipes();
        buildStorageRecipes();
        buildUtilityRecipes();
        buildRuneRecipes();
    }

    private void buildItemRecipes() {
        shaped(RecipeCategory.MISC, ModItems.SOUL_CRYSTAL.get())
                .pattern("SLS")
                .pattern("LAL")
                .pattern("SLS")
                .define('S', Items.SOUL_SOIL)
                .define('L', Items.LAPIS_LAZULI)
                .define('A', Items.AMETHYST_SHARD)
                .unlockedBy("has_soul_soil", has(Items.SOUL_SOIL))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.SOUL_STEEL_INGOT.get(), 4)
                .pattern("CIC")
                .pattern("IXI")
                .pattern("CIC")
                .define('C', Items.COAL)
                .define('I', Items.IRON_INGOT)
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .unlockedBy("has_soul_crystal", has(ModItems.SOUL_CRYSTAL.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.SOUL_SAWBLADE.get())
                .pattern("FIF")
                .pattern("IXI")
                .pattern("FIF")
                .define('F', Items.FLINT)
                .define('I', ModItems.SOUL_STEEL_INGOT.get())
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .unlockedBy("has_soul_steel_ingot", has(ModItems.SOUL_STEEL_INGOT.get()))
                .save(output);

        shaped(RecipeCategory.TOOLS, ModItems.SOUL_WAND.get())
                .pattern(" EX")
                .pattern(" IE")
                .pattern("I  ")
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .define('E', Items.ENDER_PEARL)
                .define('I', ModItems.SOUL_STEEL_INGOT.get())
                .unlockedBy("has_soul_steel_ingot", has(ModItems.SOUL_STEEL_INGOT.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.SOUL_ASH_SEED.get())
                .pattern("RBR")
                .pattern("BXB")
                .pattern("RSR")
                .define('R', Items.ROTTEN_FLESH)
                .define('B', Items.BONE)
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .define('S', Items.SOUL_SOIL)
                .unlockedBy("has_soul_crystal", has(ModItems.SOUL_CRYSTAL.get()))
                .save(output);
    }

    private void buildBlockRecipes() {
        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ALTAR_BRICKS.getBlock(), 8)
                .pattern("BBB")
                .pattern("BSB")
                .pattern("BBB")
                .define('B', Items.POLISHED_BLACKSTONE_BRICKS)
                .define('S', Items.SOUL_SOIL)
                .unlockedBy("has_soul_soil", has(Items.SOUL_SOIL))
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SOUL_ALTAR_BRICKS.getBlock(), 8)
                .pattern("BBB")
                .pattern("BXB")
                .pattern("BBB")
                .define('B', ModBlocks.ALTAR_BRICKS.getBlock())
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .unlockedBy("has_soul_crystal", has(ModItems.SOUL_CRYSTAL.get()))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModBlocks.SOUL_HARVEST_ALTAR.getBlock())
                .pattern("XSX")
                .pattern("RIR")
                .pattern("AAA")
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .define('S', ModItems.SOUL_SAWBLADE.get())
                .define('I', ModItems.SOUL_STEEL_INGOT.get())
                .define('R', ModBlocks.SOUL_ALTAR_BRICKS.getBlock())
                .define('A', ModBlocks.ALTAR_BRICKS.getBlock())
                .unlockedBy("has_soul_sawblade", has(ModItems.SOUL_SAWBLADE.get()))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModBlocks.LOOT_EXTRACTOR.getBlock())
                .pattern(" C ")
                .pattern("IHI")
                .pattern("ARA")
                .define('C', ModItems.SOUL_CRYSTAL.get())
                .define('I', ModItems.SOUL_STEEL_INGOT.get())
                .define('H', Items.HOPPER)
                .define('A', ModBlocks.ALTAR_BRICKS.getBlock())
                .define('R', ModBlocks.SOUL_ALTAR_BRICKS.getBlock())
                .unlockedBy("has_soul_harvest_altar", has(ModBlocks.SOUL_HARVEST_ALTAR.getBlock()))
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WARDSTONE_TILES.getBlock(), 8)
                .pattern("PPP")
                .pattern("PXP")
                .pattern("PPP")
                .define('P', Items.POLISHED_BLACKSTONE)
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .unlockedBy("has_soul_crystal", has(ModItems.SOUL_CRYSTAL.get()))
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WITHER_WARD.getBlock(), 4)
                .pattern("IAI")
                .pattern("AOA")
                .pattern("IAI")
                .define('I', ModItems.SOUL_STEEL_INGOT.get())
                .define('A', ModBlocks.ALTAR_BRICKS.getBlock())
                .define('O', Blocks.OBSIDIAN)
                .unlockedBy("has_soul_crystal", has(ModItems.SOUL_CRYSTAL.get()))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModBlocks.SOULSTREAM_PAD.getBlock(), 2)
                .pattern("ICI")
                .pattern("APA")
                .define('I', ModItems.SOUL_STEEL_INGOT.get())
                .define('C', ModItems.SOUL_CRYSTAL.get())
                .define('A', ModBlocks.ALTAR_BRICKS.getBlock())
                .define('P', Items.PISTON)
                .unlockedBy("has_soul_steel_ingot", has(ModItems.SOUL_STEEL_INGOT.get()))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModBlocks.RIFT_PAD.getBlock())
                .pattern("ECE")
                .pattern("ARA")
                .define('E', Items.ENDER_PEARL)
                .define('C', ModItems.SOUL_CRYSTAL.get())
                .define('A', ModBlocks.ALTAR_BRICKS.getBlock())
                .define('R', ModBlocks.SOUL_ALTAR_BRICKS.getBlock())
                .unlockedBy("has_soul_crystal", has(ModItems.SOUL_CRYSTAL.get()))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModBlocks.RIFT_ANCHOR.getBlock())
                .pattern("ACA")
                .pattern("IEI")
                .pattern("ARA")
                .define('A', ModBlocks.ALTAR_BRICKS.getBlock())
                .define('C', ModItems.SOUL_CRYSTAL.get())
                .define('I', ModItems.SOUL_STEEL_INGOT.get())
                .define('E', Items.ENDER_PEARL)
                .define('R', ModBlocks.SOUL_ALTAR_BRICKS.getBlock())
                .unlockedBy("has_soul_crystal", has(ModItems.SOUL_CRYSTAL.get()))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModBlocks.GRAVITY_ANCHOR.getBlock())
                .pattern("PCP")
                .pattern("IFI")
                .pattern("ARA")
                .define('P', Items.PHANTOM_MEMBRANE)
                .define('A', ModBlocks.ALTAR_BRICKS.getBlock())
                .define('C', ModItems.SOUL_CRYSTAL.get())
                .define('I', ModItems.SOUL_STEEL_INGOT.get())
                .define('F', Items.FERMENTED_SPIDER_EYE)
                .define('R', ModBlocks.SOUL_ALTAR_BRICKS.getBlock())
                .unlockedBy("has_soul_crystal", has(ModItems.SOUL_CRYSTAL.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModBlocks.WISDOM_VESSEL.getBlock())
                .pattern("ACA")
                .pattern("GBG")
                .pattern("ARA")
                .define('A', ModBlocks.ALTAR_BRICKS.getBlock())
                .define('C', ModItems.SOUL_CRYSTAL.get())
                .define('G', Items.GLASS)
                .define('B', Items.BUCKET)
                .define('R', ModBlocks.SOUL_ALTAR_BRICKS.getBlock())
                .unlockedBy("has_soul_crystal", has(ModItems.SOUL_CRYSTAL.get()))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModBlocks.DESTINATION_PAD.getBlock())
                .pattern("ICI")
                .pattern("AOA")
                .define('I', ModItems.SOUL_STEEL_INGOT.get())
                .define('C', ModItems.SOUL_CRYSTAL.get())
                .define('A', ModBlocks.ALTAR_BRICKS.getBlock())
                .define('O', Items.CRYING_OBSIDIAN)
                .unlockedBy("has_soul_steel_ingot", has(ModItems.SOUL_STEEL_INGOT.get()))
                .save(output);
    }

    private void buildStorageRecipes() {
        String prefix = ModConstants.MOD_ID + ":";

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SOUL_STEEL_BLOCK.getBlock())
                .pattern("III")
                .pattern("III")
                .pattern("III")
                .define('I', ModItems.SOUL_STEEL_INGOT.get())
                .unlockedBy("has_soul_steel_ingot", has(ModItems.SOUL_STEEL_INGOT.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, ModItems.SOUL_STEEL_INGOT.get(), 9)
                .requires(ModBlocks.SOUL_STEEL_BLOCK.getBlock())
                .unlockedBy("has_soul_steel_block", has(ModBlocks.SOUL_STEEL_BLOCK.getBlock()))
                .save(output, prefix + "soul_steel_ingot_from_block");

        shapeless(RecipeCategory.MISC, ModItems.SOUL_STEEL_NUGGET.get(), 9)
                .requires(ModItems.SOUL_STEEL_INGOT.get())
                .unlockedBy("has_soul_steel_ingot", has(ModItems.SOUL_STEEL_INGOT.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.SOUL_STEEL_INGOT.get())
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .define('N', ModItems.SOUL_STEEL_NUGGET.get())
                .unlockedBy("has_soul_steel_nugget", has(ModItems.SOUL_STEEL_NUGGET.get()))
                .save(output, prefix + "soul_steel_ingot_from_nuggets");

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SOUL_CRYSTAL_BLOCK.getBlock())
                .pattern("XX")
                .pattern("XX")
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .unlockedBy("has_soul_crystal", has(ModItems.SOUL_CRYSTAL.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, ModItems.SOUL_CRYSTAL.get(), 4)
                .requires(ModBlocks.SOUL_CRYSTAL_BLOCK.getBlock())
                .unlockedBy("has_soul_crystal_block", has(ModBlocks.SOUL_CRYSTAL_BLOCK.getBlock()))
                .save(output, prefix + "soul_crystal_from_block");
    }

    private void buildUtilityRecipes() {
        shaped(RecipeCategory.REDSTONE, ModBlocks.WATCHER_BRICKS.getBlock())
                .pattern("AAA")
                .pattern("AKA")
                .pattern("AAA")
                .define('A', ModBlocks.ALTAR_BRICKS.getBlock())
                .define('K', Items.SCULK_SENSOR)
                .unlockedBy("has_sculk_sensor", has(Items.SCULK_SENSOR))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.FILTER.get())
                .pattern("NPN")
                .pattern("PXP")
                .pattern("NPN")
                .define('N', ModItems.SOUL_STEEL_NUGGET.get())
                .define('P', Items.PAPER)
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .unlockedBy("has_soul_steel_nugget", has(ModItems.SOUL_STEEL_NUGGET.get()))
                .save(output);
    }

    private void buildRuneRecipes() {
        shaped(RecipeCategory.MISC, ModItems.BLANK_SOUL_RUNE.get(), 2)
                .pattern(" I ")
                .pattern("IAI")
                .pattern(" X ")
                .define('I', ModItems.SOUL_STEEL_INGOT.get())
                .define('A', ModBlocks.ALTAR_BRICKS.getBlock())
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .unlockedBy("has_soul_steel_ingot", has(ModItems.SOUL_STEEL_INGOT.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.LOOTING_SOUL_RUNE.get())
                .pattern("EGE")
                .pattern("XBX")
                .pattern("ENE")
                .define('E', Items.EMERALD)
                .define('G', Items.GOLD_BLOCK)
                .define('N', Items.NETHERITE_INGOT)
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .define('B', ModItems.BLANK_SOUL_RUNE.get())
                .unlockedBy("has_blank_soul_rune", has(ModItems.BLANK_SOUL_RUNE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.SMITE_SOUL_RUNE.get())
                .pattern("FGF")
                .pattern("GBG")
                .pattern("FGF")
                .define('F', Items.ROTTEN_FLESH)
                .define('G', Items.GOLD_INGOT)
                .define('B', ModItems.BLANK_SOUL_RUNE.get())
                .unlockedBy("has_blank_soul_rune", has(ModItems.BLANK_SOUL_RUNE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.SHARPNESS_SOUL_RUNE.get())
                .pattern("FDF")
                .pattern("XBX")
                .pattern("FDF")
                .define('F', Items.FLINT)
                .define('D', Items.DIAMOND_SWORD)
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .define('B', ModItems.BLANK_SOUL_RUNE.get())
                .unlockedBy("has_blank_soul_rune", has(ModItems.BLANK_SOUL_RUNE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.SOUL_FLAME_RUNE.get())
                .pattern("RCR")
                .pattern("XBX")
                .pattern("RCR")
                .define('R', Items.BLAZE_ROD)
                .define('C', Items.SOUL_CAMPFIRE)
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .define('B', ModItems.BLANK_SOUL_RUNE.get())
                .unlockedBy("has_blank_soul_rune", has(ModItems.BLANK_SOUL_RUNE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.BEHEADING_SOUL_RUNE.get())
                .pattern("OWO")
                .pattern("XBX")
                .pattern("ONO")
                .define('O', Items.BONE_BLOCK)
                .define('W', Items.WITHER_SKELETON_SKULL)
                .define('N', Items.NETHERITE_INGOT)
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .define('B', ModItems.BLANK_SOUL_RUNE.get())
                .unlockedBy("has_blank_soul_rune", has(ModItems.BLANK_SOUL_RUNE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.BANE_SOUL_RUNE.get())
                .pattern("IFI")
                .pattern("FBF")
                .pattern("IFI")
                .define('I', Items.IRON_INGOT)
                .define('F', Items.FERMENTED_SPIDER_EYE)
                .define('B', ModItems.BLANK_SOUL_RUNE.get())
                .unlockedBy("has_blank_soul_rune", has(ModItems.BLANK_SOUL_RUNE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.SPEED_SOUL_RUNE.get())
                .pattern("RDR")
                .pattern("XBX")
                .pattern("RKR")
                .define('R', Items.REDSTONE_BLOCK)
                .define('D', Items.NETHER_STAR)
                .define('K', Items.CLOCK)
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .define('B', ModItems.BLANK_SOUL_RUNE.get())
                .unlockedBy("has_blank_soul_rune", has(ModItems.BLANK_SOUL_RUNE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.WISDOM_SOUL_RUNE.get())
                .pattern("LDL")
                .pattern("XBX")
                .pattern("LEL")
                .define('L', Items.LAPIS_BLOCK)
                .define('D', Items.DIAMOND_BLOCK)
                .define('E', Items.EXPERIENCE_BOTTLE)
                .define('X', ModItems.SOUL_CRYSTAL.get())
                .define('B', ModItems.BLANK_SOUL_RUNE.get())
                .unlockedBy("has_blank_soul_rune", has(ModItems.BLANK_SOUL_RUNE.get()))
                .save(output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        public String getName() {
            return "Soul Harvest Recipes";
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }
    }
}
