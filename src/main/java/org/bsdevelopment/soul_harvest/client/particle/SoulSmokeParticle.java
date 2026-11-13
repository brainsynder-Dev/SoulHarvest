package org.bsdevelopment.soul_harvest.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;

public class SoulSmokeParticle extends SingleQuadParticle {
    private final SpriteSet sprites;
    private final float swayPhase;

    public SoulSmokeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;
        xd = xSpeed;
        yd = 0.03;
        zd = zSpeed;
        friction = 0.96f;
        hasPhysics = false;
        lifetime = 34 + random.nextInt(11);
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
}
