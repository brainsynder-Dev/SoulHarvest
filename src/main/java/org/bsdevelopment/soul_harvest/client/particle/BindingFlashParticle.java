package org.bsdevelopment.soul_harvest.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.LightCoordsUtil;

public class BindingFlashParticle extends SingleQuadParticle {
    private final SpriteSet sprites;

    public BindingFlashParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;
        xd = 0;
        yd = 0;
        zd = 0;
        gravity = 0;
        hasPhysics = false;
        lifetime = 12;
        quadSize = (float) xSpeed * 0.6f;

        setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
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
}
