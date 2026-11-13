package org.bsdevelopment.soul_harvest.datagen;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModTextureMetaProvider implements DataProvider {
    private final Map<String, JsonObject> animations = new LinkedHashMap<>();
    private final PackOutput output;

    public ModTextureMetaProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        animations.clear();
        addAnimations();

        Path texturesFolder = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(ModConstants.MOD_ID).resolve("textures");
        return CompletableFuture.allOf(animations.entrySet().stream().map(entry -> DataProvider.saveStable(cache, entry.getValue(), texturesFolder.resolve(entry.getKey() + ".png.mcmeta"))).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Soul Harvest Texture Meta";
    }

    private void addAnimations() {
        animate("block/soul_altar_bricks", 2, true);
        animate("block/soul_harvest_altar_atlas", 2, true);
        animate("block/loot_extractor", 3, true);
        animate("block/wisdom_vessel", 2, true);
        animate("block/soul_crystal_block", 3, true);
        animate("block/wardstone_tiles", 4, true);

        animate("block/soul_ash", 4, true);

        for (int size = 1; size <= 9; size += 2) animate("block/soul_ash_sigil_%dx%d".formatted(size, size), 4, true);

        animate("block/soul_bloom_top", 4, true);

        for (int size = 1; size <= 9; size += 2) animate("block/soul_bloom_sigil_%dx%d".formatted(size, size), 4, true);

        animate("block/soulstream_pad", 2, true);
        animate("block/destination_pad", 3, true);
        animate("block/rift_pad", 2, true);
        animate("block/rift_anchor_emblem", 3, true);
        animate("block/rift_anchor_pearl", 3, true);
        animate("block/gravity_anchor_emblem", 3, true);
        animate("block/soul_ritual_shrine_circle", 4, true);
        animate("block/soul_ritual_shrine_circle_active", 1, true);
        animate("block/soul_ritual_shrine_flame", 3, false);
        animate("block/soul_ritual_shrine_flame_active", 2, false);

        for (int index = 0; index < 16; index++) animate("block/wither_ward/wither_ward_%02d".formatted(index), 2, true);

        animate("block/liquid_experience_still", 2, true);
        animate("block/liquid_experience_flow", 3, true);

        animate("block/wraithfire_0", 1, false);
        animate("block/wraithfire_1", 1, false);
        animate("block/wraithfire_embers", 3, true);
        animate("block/wraithfire_plume", 1, 12, 24);
        animate("block/wraithfire_tongue", 1, 10, 10);
        animate("block/wraithfire_wisp", 1, 6, 8);

        animate("item/blank_soul_rune", 3, true);
        animate("item/smite_soul_rune", 3, true);
        animate("item/bane_soul_rune", 3, true);
        animate("item/sharpness_soul_rune", 3, true);
        animate("item/soul_flame_rune", 3, true);
        animate("item/speed_soul_rune", 3, true);
        animate("item/wisdom_soul_rune", 3, true);
        animate("item/looting_soul_rune", 3, true);
        animate("item/beheading_soul_rune", 3, true);
        animate("item/soul_wand_linked", 2, true);
    }

    private void animate(String texture, int frameTime, boolean interpolate) {
        JsonObject animation = new JsonObject();
        animation.addProperty("frametime", frameTime);
        if (interpolate) animation.addProperty("interpolate", true);

        addAnimation(texture, animation);
    }

    private void animate(String texture, int frameTime, int frameWidth, int frameHeight) {
        JsonObject animation = new JsonObject();
        animation.addProperty("frametime", frameTime);
        animation.addProperty("width", frameWidth);
        animation.addProperty("height", frameHeight);

        addAnimation(texture, animation);
    }

    private void addAnimation(String texture, JsonObject animation) {
        JsonObject meta = new JsonObject();
        meta.add("animation", animation);
        animations.put(texture, meta);
    }
}
