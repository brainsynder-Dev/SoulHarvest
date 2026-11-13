package org.bsdevelopment.soul_harvest.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.data.ParticleDescriptionProvider;
import org.bsdevelopment.soul_harvest.registry.ModConstants;
import org.bsdevelopment.soul_harvest.registry.ModParticles;

public class ModParticleProvider extends ParticleDescriptionProvider {
    public ModParticleProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void addDescriptions() {
        spriteSet(ModParticles.XP_SPARKLE.get(), Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "xp_sparkle"), 8, false);
        spriteSet(ModParticles.BLOOM_BURST.get(), Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "bloom_burst"), 8, false);
        spriteSet(ModParticles.BLOOM_PETAL.get(), Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "bloom_petal"), 4, false);
        spriteSet(ModParticles.RIFT_WARP.get(), Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "rift_warp"), 6, false);
    }
}
