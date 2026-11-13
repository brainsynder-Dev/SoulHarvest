package org.bsdevelopment.soul_harvest.client;

import com.google.common.reflect.TypeToken;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import org.bsdevelopment.soul_harvest.client.particle.ArrivalRingParticle;
import org.bsdevelopment.soul_harvest.client.particle.BloomBurstParticle;
import org.bsdevelopment.soul_harvest.client.particle.BloomPetalParticle;
import org.bsdevelopment.soul_harvest.client.particle.RiftWarpParticle;
import org.bsdevelopment.soul_harvest.client.particle.SpriteParticleFactory;
import org.bsdevelopment.soul_harvest.client.particle.XpSparkleParticle;
import org.bsdevelopment.soul_harvest.client.render.SoulHarvestAltarRenderer;
import org.bsdevelopment.soul_harvest.client.render.WisdomVesselItemRenderer;
import org.bsdevelopment.soul_harvest.client.render.WisdomVesselRenderer;
import org.bsdevelopment.soul_harvest.client.render.WraithfireSprites;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModConstants;
import org.bsdevelopment.soul_harvest.registry.ModFluids;
import org.bsdevelopment.soul_harvest.registry.ModParticles;
import org.bsdevelopment.soul_harvest.utilities.Wraithfire;

public class ModelHandler {
    public static void register(IEventBus eventBus) {
        eventBus.<EntityRenderersEvent.RegisterRenderers>addListener(event -> {
            event.registerBlockEntityRenderer(ModBlocks.SOUL_HARVEST_ALTAR.getTileEntityType(), SoulHarvestAltarRenderer::new);
            event.registerBlockEntityRenderer(ModBlocks.WISDOM_VESSEL.getTileEntityType(), context -> new WisdomVesselRenderer());
        });

        eventBus.<RegisterFluidModelsEvent>addListener(event -> event.register(new FluidModel.Unbaked(
                new Material(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "block/liquid_experience_still")),
                new Material(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "block/liquid_experience_flow")),
                null,
                null), ModFluids.LIQUID_EXPERIENCE, ModFluids.FLOWING_LIQUID_EXPERIENCE));

        eventBus.<RegisterSpecialModelRendererEvent>addListener(event -> event.register(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "wisdom_vessel"), WisdomVesselItemRenderer.Unbaked.MAP_CODEC));

        eventBus.<RegisterClientExtensionsEvent>addListener(event -> {
            event.registerFluidType(new LiquidExperienceClientExtensions(), ModFluids.LIQUID_EXPERIENCE_TYPE.get());
        });

        eventBus.<RegisterRenderStateModifiersEvent>addListener(event -> {
            event.registerEntityModifier(new TypeToken<EntityRenderer<Entity, EntityRenderState>>() {}, (entity, renderState) -> {
                if (Wraithfire.isBurning(entity)) renderState.setRenderData(WraithfireSprites.BURNING, true);
            });
        });

        eventBus.<RegisterParticleProvidersEvent>addListener(event -> {
            registerParticle(event, ModParticles.XP_SPARKLE.get(), XpSparkleParticle::new);
            registerParticle(event, ModParticles.BLOOM_BURST.get(), BloomBurstParticle::new);
            registerParticle(event, ModParticles.BLOOM_PETAL.get(), BloomPetalParticle::new);
            registerParticle(event, ModParticles.RIFT_WARP.get(), RiftWarpParticle::new);
            registerParticle(event, ModParticles.ARRIVAL_RING.get(), ArrivalRingParticle::new);
        });
    }

    private static void registerParticle(RegisterParticleProvidersEvent event, SimpleParticleType particleType, SpriteParticleFactory factory) {
        event.registerSpriteSet(particleType, sprites -> (options, level, x, y, z, xSpeed, ySpeed, zSpeed, random) -> factory.create(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites));
    }
}
