package org.bsdevelopment.soul_harvest.registry;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.bsdevelopment.soul_harvest.items.FilterItem;
import org.bsdevelopment.soul_harvest.items.SoulCrystalItem;
import org.bsdevelopment.soul_harvest.items.SoulWandItem;
import org.bsdevelopment.soul_harvest.items.SpawnPadSeedItem;
import org.bsdevelopment.soul_harvest.items.base.BaseItem;
import org.bsdevelopment.soul_harvest.items.base.UpgradeItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ModConstants.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModConstants.MOD_ID);

    public static final DeferredItem<SoulCrystalItem> SOUL_CRYSTAL = ITEMS.registerItem("soul_crystal", SoulCrystalItem::new);
    public static final DeferredItem<BaseItem> SOUL_STEEL_INGOT = ITEMS.registerItem("soul_steel_ingot", BaseItem::new);
    public static final DeferredItem<BaseItem> SOUL_SAWBLADE = ITEMS.registerItem("soul_sawblade", BaseItem::new);
    public static final DeferredItem<SoulWandItem> SOUL_WAND = ITEMS.registerItem("soul_wand", SoulWandItem::new, props -> props.stacksTo(1));
    public static final DeferredItem<BaseItem> SOUL_PROBE = ITEMS.registerItem("soul_probe", BaseItem::new, props -> props.stacksTo(1));
    public static final DeferredItem<SpawnPadSeedItem> SOUL_ASH_SEED = ITEMS.registerItem("soul_ash_seed", props -> new SpawnPadSeedItem(ModBlocks.SOUL_ASH, props));
    public static final DeferredItem<SpawnPadSeedItem> SOUL_BLOOM_SEED = ITEMS.registerItem("soul_bloom_seed", props -> new SpawnPadSeedItem(ModBlocks.SOUL_BLOOM, props));
    public static final DeferredItem<BaseItem> SOUL_STEEL_NUGGET = ITEMS.registerItem("soul_steel_nugget", BaseItem::new);
    public static final DeferredItem<FilterItem> FILTER = ITEMS.registerItem("filter", FilterItem::new);

    public static final DeferredItem<BaseItem> BLANK_SOUL_RUNE = ITEMS.registerItem("blank_soul_rune", BaseItem::new);
    public static final DeferredItem<UpgradeItem> LOOTING_SOUL_RUNE = ITEMS.registerItem("looting_soul_rune", UpgradeItem::new);
    public static final DeferredItem<UpgradeItem> SMITE_SOUL_RUNE = ITEMS.registerItem("smite_soul_rune", UpgradeItem::new);
    public static final DeferredItem<UpgradeItem> SHARPNESS_SOUL_RUNE = ITEMS.registerItem("sharpness_soul_rune", UpgradeItem::new);
    public static final DeferredItem<UpgradeItem> SOUL_FLAME_RUNE = ITEMS.registerItem("soul_flame_rune", UpgradeItem::new);
    public static final DeferredItem<UpgradeItem> BEHEADING_SOUL_RUNE = ITEMS.registerItem("beheading_soul_rune", UpgradeItem::new);
    public static final DeferredItem<UpgradeItem> BANE_SOUL_RUNE = ITEMS.registerItem("bane_soul_rune", UpgradeItem::new);
    public static final DeferredItem<UpgradeItem> SPEED_SOUL_RUNE = ITEMS.registerItem("speed_soul_rune", UpgradeItem::new);
    public static final DeferredItem<UpgradeItem> WISDOM_SOUL_RUNE = ITEMS.registerItem("wisdom_soul_rune", UpgradeItem::new);

    public static final DeferredItem<BucketItem> LIQUID_EXPERIENCE_BUCKET = ITEMS.registerItem("liquid_experience_bucket", props -> new BucketItem(ModFluids.LIQUID_EXPERIENCE.get(), props), props -> props.craftRemainder(Items.BUCKET).stacksTo(1));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD_CREATIVE_TAB = CREATIVE_MODE_TABS.register(ModConstants.MOD_ID,
            () -> CreativeModeTab.builder().title(gradientTitle())
                    .withTabsBefore(CreativeModeTabs.OP_BLOCKS)
                    .icon(() -> new ItemStack(SOUL_SAWBLADE.get()))
                    .displayItems((_, output) -> {
                        output.accept(ModBlocks.SOUL_HARVEST_ALTAR.getItem());
                        output.accept(ModBlocks.LOOT_EXTRACTOR.getItem());
                        output.accept(ModBlocks.WISDOM_VESSEL.getItem());
                        output.accept(ModBlocks.SOULSTREAM_PAD.getItem());
                        output.accept(ModBlocks.RIFT_PAD.getItem());
                        output.accept(ModBlocks.DESTINATION_PAD.getItem());
                        output.accept(ModBlocks.RIFT_ANCHOR.getItem());
                        output.accept(ModBlocks.GRAVITY_ANCHOR.getItem());
                        output.accept(ModBlocks.SOUL_RITUAL_SHRINE.getItem());
                        output.accept(ModBlocks.WATCHER_BRICKS.getItem());
                        output.accept(ModBlocks.SOUL_STEEL_BLOCK.getItem());
                        output.accept(ModBlocks.SOUL_CRYSTAL_BLOCK.getItem());
                        output.accept(ModBlocks.ALTAR_BRICKS.getItem());
                        output.accept(ModBlocks.SOUL_ALTAR_BRICKS.getItem());
                        output.accept(ModBlocks.SOUL_ASH.getItem());
                        output.accept(ModBlocks.SOUL_BLOOM.getItem());
                        output.accept(ModBlocks.WARDSTONE_TILES.getItem());
                        output.accept(ModBlocks.WITHER_WARD.getItem());

                        output.accept(SOUL_WAND.get());
                        output.accept(SOUL_PROBE.get());
                        output.accept(SOUL_ASH_SEED.get());
                        output.accept(SOUL_BLOOM_SEED.get());
                        output.accept(FILTER.get());
                        output.accept(SOUL_CRYSTAL.get());
                        output.accept(SOUL_STEEL_INGOT.get());
                        output.accept(SOUL_STEEL_NUGGET.get());
                        output.accept(SOUL_SAWBLADE.get());
                        output.accept(LIQUID_EXPERIENCE_BUCKET.get());

                        output.accept(BLANK_SOUL_RUNE.get());
                        output.accept(LOOTING_SOUL_RUNE.get());
                        output.accept(SMITE_SOUL_RUNE.get());
                        output.accept(SHARPNESS_SOUL_RUNE.get());
                        output.accept(SOUL_FLAME_RUNE.get());
                        output.accept(BEHEADING_SOUL_RUNE.get());
                        output.accept(BANE_SOUL_RUNE.get());
                        output.accept(SPEED_SOUL_RUNE.get());
                        output.accept(WISDOM_SOUL_RUNE.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        CREATIVE_MODE_TABS.register(eventBus);
    }

    private static Component gradientTitle() {
        String text = "Soul Harvest";
        int startColor = TextColor.parseColor("#0A2A3F").getOrThrow().getValue();
        int endColor = TextColor.parseColor("#19B8FF").getOrThrow().getValue();
        MutableComponent title = Component.empty();

        for (int index = 0; index < text.length(); index++) {
            int color = ARGB.srgbLerp((float) index / (text.length() - 1), startColor, endColor);
            title.append(Component.literal(String.valueOf(text.charAt(index))).withColor(color).withStyle(ChatFormatting.BOLD));
        }
        return title;
    }
}
