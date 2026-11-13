package org.bsdevelopment.soul_harvest.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, ModConstants.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> XP_SPARKLE = PARTICLE_TYPES.register("xp_sparkle", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOM_BURST = PARTICLE_TYPES.register("bloom_burst", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOM_PETAL = PARTICLE_TYPES.register("bloom_petal", () -> new SimpleParticleType(false));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
