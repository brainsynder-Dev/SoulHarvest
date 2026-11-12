package org.bsdevelopment.soul_harvest.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.DeferredItem;
import org.bsdevelopment.soul_harvest.registry.ModConstants;
import org.bsdevelopment.soul_harvest.registry.ModItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@EventBusSubscriber(modid = ModConstants.MOD_ID)
public class ModConfig {
    public static final ModConfigSpec SPEC;
    private static final List<Runnable> BAKE_ACTIONS;
    private static final Map<Identifier, Integer> RUNE_MAX_STACKS;

    public static boolean soulAshBiomeSpawns;
    public static int soulAshSpawnInterval;
    public static int soulAshMaxEntities;
    public static boolean soulAshRedstoneUpdates;
    public static boolean soulBloomBiomeSpawns;
    public static int soulBloomSpawnInterval;
    public static int soulBloomMaxEntities;
    public static boolean soulBloomRedstoneUpdates;
    public static float riftPadSoundVolume;
    public static int riftPadSoundCooldown;
    public static int riftAnchorRange;
    public static int gravityAnchorRange;
    public static int wisdomVesselMaxLevel;
    public static boolean altarAbsorbItems;
    public static int maxItemsPerLine;

    static {
        BAKE_ACTIONS = new ArrayList<>();
        RUNE_MAX_STACKS = new HashMap<>();
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("soul_ash");
        bind(builder
                .comment("Should Soul Ash spawn the monsters of its biome? When false, or when the biome has no monsters, the list in soul-ash-mobs.json is used.")
                .define("use_biome_spawns", true), value -> soulAshBiomeSpawns = value);
        bind(builder
                .comment("Ticks between each spawn cycle of an awakened 5x5 Soul Ash pad (20 ticks = 1 second). Other sizes scale with their area.")
                .defineInRange("spawn_interval", 60, 20, 6000), value -> soulAshSpawnInterval = value);
        bind(builder
                .comment("Maximum number of monsters that can stand on an awakened 5x5 Soul Ash pad before it stops spawning. Other sizes scale with their area.")
                .defineInRange("max_entities", 6, 1, 64), value -> soulAshMaxEntities = value);
        bind(builder
                .comment("Should neighbor updates (such as redstone) next to an awakened Soul Ash pad trigger an extra spawn cycle?")
                .define("redstone_updates", true), value -> soulAshRedstoneUpdates = value);
        builder.pop();

        builder.push("soul_bloom");
        bind(builder
                .comment("Should Soul Bloom spawn the animals of its biome? When false, or when the biome has no animals, the list in soul-bloom-mobs.json is used.")
                .define("use_biome_spawns", true), value -> soulBloomBiomeSpawns = value);
        bind(builder
                .comment("Ticks between each spawn cycle of an awakened 5x5 Soul Bloom pad (20 ticks = 1 second). Other sizes scale with their area.")
                .defineInRange("spawn_interval", 200, 20, 6000), value -> soulBloomSpawnInterval = value);
        bind(builder
                .comment("Maximum number of animals that can stand on an awakened 5x5 Soul Bloom pad before it stops spawning. Other sizes scale with their area.")
                .defineInRange("max_entities", 4, 1, 64), value -> soulBloomMaxEntities = value);
        bind(builder
                .comment("Should neighbor updates (such as redstone) next to an awakened Soul Bloom pad trigger an extra spawn cycle?")
                .define("redstone_updates", true), value -> soulBloomRedstoneUpdates = value);
        builder.pop();

        builder.push("rift_pad");
        bind(builder
                .comment("Volume of the Rift Pad teleport sound. Set to 0 to disable it.")
                .defineInRange("sound_volume", 0.3, 0.0, 1.0), value -> riftPadSoundVolume = value.floatValue());
        bind(builder
                .comment("Minimum ticks between teleport sounds at the same Destination Pad.")
                .defineInRange("sound_cooldown", 20, 0, 1200), value -> riftPadSoundCooldown = value);
        builder.pop();

        builder.push("rift_anchor");
        bind(builder
                .comment("Range in blocks (in every direction) in which a Rift Anchor stops enderman and shulkers from teleporting on their own.")
                .defineInRange("range", 16, 1, 64), value -> riftAnchorRange = value);
        builder.pop();

        builder.push("gravity_anchor");
        bind(builder
                .comment("Range in blocks (in every direction) in which a Gravity Anchor stops spiders (#soul_harvest:climb_inhibited) from climbing walls and flying mobs (#soul_harvest:flight_inhibited) from flying.")
                .defineInRange("range", 12, 1, 64), value -> gravityAnchorRange = value);
        builder.pop();

        builder.push("wisdom_vessel");
        bind(builder
                .comment("Maximum experience a Wisdom Vessel can hold, in levels (counted from level 0).")
                .defineInRange("max_level", 128, 1, 1000), value -> wisdomVesselMaxLevel = value);
        builder.pop();

        builder.push("altar");
        bind(builder
                .comment("Should the Soul Harvest Altar absorb dropped items within 4 blocks into its internal storage?")
                .define("absorb_items", true), value -> altarAbsorbItems = value);
        builder.pop();

        builder.push("jade_support");
        bind(builder
                .comment("Maximum items displayed per line in Jade tooltips.")
                .defineInRange("items_per_line", 10, 0, 20), value -> maxItemsPerLine = value);
        builder.pop();

        builder.push("altar_runes");
        defineRune(builder, "smite", ModItems.SMITE_SOUL_RUNE, 10);
        defineRune(builder, "bane", ModItems.BANE_SOUL_RUNE, 10);
        defineRune(builder, "sharpness", ModItems.SHARPNESS_SOUL_RUNE, 10);
        defineRune(builder, "flame", ModItems.SOUL_FLAME_RUNE, 10);
        defineRune(builder, "speed", ModItems.SPEED_SOUL_RUNE, 5);
        defineRune(builder, "wisdom", ModItems.WISDOM_SOUL_RUNE, 5);
        defineRune(builder, "looting", ModItems.LOOTING_SOUL_RUNE, 10);
        defineRune(builder, "beheading", ModItems.BEHEADING_SOUL_RUNE, 10);
        builder.pop();

        SPEC = builder.build();
    }

    public static void bakeConfig() {
        BAKE_ACTIONS.forEach(Runnable::run);
    }

    public static int getRuneMaxStack(Item rune) {
        return RUNE_MAX_STACKS.getOrDefault(BuiltInRegistries.ITEM.getKey(rune), 64);
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        if (!event.getConfig().getModId().equals(ModConstants.MOD_ID)) return;
        bakeConfig();
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        if (!event.getConfig().getModId().equals(ModConstants.MOD_ID)) return;
        bakeConfig();
    }

    private static <T> void bind(ModConfigSpec.ConfigValue<T> configValue, Consumer<T> setter) {
        BAKE_ACTIONS.add(() -> setter.accept(configValue.get()));
    }

    private static void defineRune(ModConfigSpec.Builder builder, String name, DeferredItem<?> rune, int defaultMaxStack) {
        Identifier runeId = rune.getId();
        bind(builder
                .comment("Maximum number of " + name + " runes that can be placed in a Soul Harvest Altar slot.")
                .defineInRange(name + ".max_stack", defaultMaxStack, 1, 64), value -> RUNE_MAX_STACKS.put(runeId, value));
    }
}
