package org.bsdevelopment.soul_harvest.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;

public class BloomPetalParticle extends SingleQuadParticle {
    private final SpriteSet sprites;
    private final float swayPhase;

    public BloomPetalParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites.first());
        this.sprites = sprites;
        xd = xSpeed;
        yd = ySpeed;
        zd = zSpeed;
        friction = 0.9f;
        gravity = 0.15f;
        hasPhysics = true;
        lifetime = 30 + random.nextInt(14);
        quadSize = 0.13f;
        swayPhase = random.nextFloat() * Mth.TWO_PI;

        setSprite(sprites.get(0, 3));
    }

    @Override
    public void tick() {
        super.tick();
        if (yd < -0.06) yd = -0.06;
        xd += Mth.sin(swayPhase + age * 0.3f) * 0.006;
        setSprite(sprites.get((age / 3) % 4, 3));
        setAlpha(Math.min(1f, (lifetime - age) / 8f));
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    protected int getLightCoords(float partialTick) {
        return LightCoordsUtil.FULL_BRIGHT;
    }
}
