package org.bsdevelopment.soul_harvest.utilities;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.bsdevelopment.soul_harvest.config.MobSpawnConfig;

import java.util.List;

public class WeightedRandomSelector {
    public static MobSpawnConfig.SpawnEntry selectRandomEntry(List<MobSpawnConfig.SpawnEntry> validEntries, RandomSource random) {
        int totalWeight = validEntries.stream().mapToInt(entry -> Math.max(0, entry.weight)).sum();
        if (totalWeight <= 0) return null;

        int randomWeight = random.nextInt(totalWeight);
        int cumulativeWeight = 0;
        for (MobSpawnConfig.SpawnEntry entry : validEntries) {
            cumulativeWeight += Math.max(0, entry.weight);
            if (randomWeight < cumulativeWeight) return entry;
        }

        return null;
    }

    public static int getRandomSpawnCount(MobSpawnConfig.SpawnEntry entry, RandomSource random) {
        return Mth.randomBetweenInclusive(random, entry.minCount, Math.max(entry.minCount, entry.maxCount));
    }
}
