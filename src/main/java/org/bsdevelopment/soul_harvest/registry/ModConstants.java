package org.bsdevelopment.soul_harvest.registry;

import net.neoforged.fml.loading.FMLPaths;
import org.bsdevelopment.soul_harvest.SoulHarvest;

import java.nio.file.Path;

public class ModConstants {
    public static final String MOD_ID = SoulHarvest.MOD_ID;
    public static final Path CONFIG_DIR = FMLPaths.CONFIGDIR.get().resolve(MOD_ID);
}
