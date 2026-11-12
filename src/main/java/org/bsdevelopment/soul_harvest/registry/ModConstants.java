package org.bsdevelopment.soul_harvest.registry;

import com.mojang.authlib.GameProfile;
import net.neoforged.fml.loading.FMLPaths;
import org.bsdevelopment.soul_harvest.SoulHarvest;
import org.bsdevelopment.soul_harvest.config.MobSpawnConfig;
import org.bsdevelopment.soul_harvest.config.MobSpawnConfig.SpawnEntry;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

public class ModConstants {
    public static final String MOD_ID = SoulHarvest.MOD_ID;
    public static final Path CONFIG_DIR = FMLPaths.CONFIGDIR.get().resolve(MOD_ID);
    public static final GameProfile FAKE_PROFILE = new GameProfile(UUID.nameUUIDFromBytes((MOD_ID + ":fake_player").getBytes(StandardCharsets.UTF_8)), "[SoulHarvest]");

    // TODO: I need to figure out good default values for the count and weight for both passive and hostile mobs
    public static final MobSpawnConfig SOUL_ASH_SPAWNS = MobSpawnConfig.loadConfig(CONFIG_DIR.resolve("soul-ash-mobs.json"),
            new SpawnEntry("minecraft:spider", 1, 4, 100).setDimensions("minecraft:overworld"),
            new SpawnEntry("minecraft:zombie", 1, 4, 95).setDimensions("minecraft:overworld"),
            new SpawnEntry("minecraft:zombie_villager", 1, 1, 5).setDimensions("minecraft:overworld"),
            new SpawnEntry("minecraft:skeleton", 1, 4, 100).setDimensions("minecraft:overworld", "minecraft:the_nether"),
            new SpawnEntry("minecraft:creeper", 1, 4, 100).setDimensions("minecraft:overworld"),
            new SpawnEntry("minecraft:slime", 1, 4, 100).setDimensions("minecraft:overworld"),
            new SpawnEntry("minecraft:enderman", 1, 4, 10).setDimensions("ANY"),
            new SpawnEntry("minecraft:witch", 1, 1, 10).setDimensions("minecraft:overworld"),
            new SpawnEntry("minecraft:blaze", 1, 1, 50).setDimensions("minecraft:the_nether"),
            new SpawnEntry("minecraft:wither_skeleton", 1, 1, 20).setDimensions("minecraft:the_nether"),
            new SpawnEntry("minecraft:magma_cube", 1, 1, 70).setDimensions("minecraft:the_nether"),
            new SpawnEntry("minecraft:zombified_piglin", 1, 3, 100).setDimensions("minecraft:the_nether")
    );

    public static final MobSpawnConfig SOUL_BLOOM_SPAWNS = MobSpawnConfig.loadConfig(CONFIG_DIR.resolve("soul-bloom-mobs.json"),
            new SpawnEntry("minecraft:sheep", 2, 3, 80).setDimensions("minecraft:overworld"),
            new SpawnEntry("minecraft:pig", 2, 3, 80).setDimensions("minecraft:overworld"),
            new SpawnEntry("minecraft:chicken", 2, 3, 80).setDimensions("minecraft:overworld"),
            new SpawnEntry("minecraft:cow", 2, 3, 80).setDimensions("minecraft:overworld"),
            new SpawnEntry("minecraft:rabbit", 1, 2, 5).setDimensions("minecraft:overworld"),
            new SpawnEntry("minecraft:horse", 1, 2, 10).setDimensions("minecraft:overworld"),
            new SpawnEntry("minecraft:donkey", 1, 1, 5).setDimensions("minecraft:overworld")
    );
}
