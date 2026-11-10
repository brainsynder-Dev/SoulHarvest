package org.bsdevelopment.soul_harvest.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import org.bsdevelopment.soul_harvest.SoulHarvest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MobSpawnConfig {
    @SerializedName("spawns")
    public List<SpawnEntry> spawns = new ArrayList<>();

    public static MobSpawnConfig loadConfig(Path path, SpawnEntry... defaultEntries) {
        if (!Files.exists(path)) return saveDefaultConfig(path, defaultEntries);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            MobSpawnConfig config = new Gson().fromJson(reader, MobSpawnConfig.class);
            if (config != null && config.spawns != null) return config;
        } catch (IOException | JsonParseException exception) {
            SoulHarvest.LOGGER.error("Failed to load mob spawn config from {}, using the defaults", path, exception);
        }

        MobSpawnConfig config = new MobSpawnConfig();
        config.spawns = List.of(defaultEntries);
        return config;
    }

    private static MobSpawnConfig saveDefaultConfig(Path path, SpawnEntry... entries) {
        MobSpawnConfig config = new MobSpawnConfig();
        config.spawns = List.of(entries);

        try {
            Files.createDirectories(path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(config, writer);
            }
        } catch (IOException exception) {
            SoulHarvest.LOGGER.error("Failed to save default mob spawn config to {}", path, exception);
        }
        return config;
    }

    public List<SpawnEntry> getValidEntries(Level level, BlockPos pos) {
        String biome = level.getBiome(pos).getRegisteredName();
        String dimension = level.dimension().identifier().toString();
        return spawns.stream().filter(entry -> entry.isBiomeValid(biome) && entry.isDimensionValid(dimension)).toList();
    }

    public static class SpawnEntry {
        @SerializedName("entity-type")
        public String entityType;

        @SerializedName("weight")
        public int weight;

        @SerializedName("minimum-spawn-count")
        public int minCount;

        @SerializedName("maximum-spawn-count")
        public int maxCount;

        @SerializedName("allowed-biomes")
        public List<String> biomes = new ArrayList<>();

        @SerializedName("allowed-dimensions")
        public List<String> dimensions = new ArrayList<>();

        public SpawnEntry(String entityType, int minCount, int maxCount, int weight) {
            this.entityType = entityType;
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.weight = weight;
        }

        public SpawnEntry setBiomes(String... biomes) {
            this.biomes.addAll(List.of(biomes));
            return this;
        }

        public SpawnEntry setDimensions(String... dimensions) {
            this.dimensions.addAll(List.of(dimensions));
            return this;
        }

        public Identifier getMobResource() {
            return Identifier.parse(entityType);
        }

        public boolean isBiomeValid(String biome) {
            if (biomes.isEmpty()) return true;
            return biomes.contains("ANY") || biomes.contains(biome);
        }

        public boolean isDimensionValid(String dimension) {
            if (dimensions.isEmpty()) return true;
            return dimensions.contains("ANY") || dimensions.contains(dimension);
        }
    }
}
