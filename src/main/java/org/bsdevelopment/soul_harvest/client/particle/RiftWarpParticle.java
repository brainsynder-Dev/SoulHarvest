package org.bsdevelopment.soul_harvest.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class RiftWarpParticle extends SingleQuadParticle {
    private final SpriteSet sprites;
    private final float swayPhase;

    public RiftWarpParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;
        xd = 0;
        yd = 0.06;
        zd = 0;
        friction = 0.96f;
        hasPhysics = false;
        lifetime = 12 + random.nextInt(4);
        quadSize = 0.12f * (0.8f + random.nextFloat() * 0.4f);
        swayPhase = random.nextFloat() * Mth.TWO_PI;

        setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        xd += Mth.sin(swayPhase + age * 0.3f) * 0.004;
        setSpriteFromAge(sprites);
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    protected int getLightCoords(float partialTick) {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new RiftWarpParticle(level, x, y, z, sprites);
        }
    }
}
