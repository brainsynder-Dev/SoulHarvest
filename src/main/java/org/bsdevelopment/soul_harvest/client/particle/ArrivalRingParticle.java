package org.bsdevelopment.soul_harvest.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.LightCoordsUtil;

public class ArrivalRingParticle extends SingleQuadParticle {
    private final SpriteSet sprites;
    private final int delay;

    public ArrivalRingParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;
        delay = (int) xSpeed;
        xd = 0;
        yd = 0;
        zd = 0;
        gravity = 0;
        hasPhysics = false;
        lifetime = 12 + delay;
        quadSize = 0.35f;

        updateSprite();
    }

    @Override
    public void tick() {
        super.tick();
        updateSprite();
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    protected int getLightCoords(float partialTick) {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    private void updateSprite() {
        setAlpha(age < delay ? 0 : 1);
        setSprite(sprites.get(Math.max(0, age - delay), lifetime - delay));
    }
}
