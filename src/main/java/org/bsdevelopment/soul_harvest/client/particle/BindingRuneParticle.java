package org.bsdevelopment.soul_harvest.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;

public class BindingRuneParticle extends SingleQuadParticle {
    private final double startX;
    private final double startY;
    private final double startZ;
    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public BindingRuneParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, sprites.first());
        startX = x;
        startY = y;
        startZ = z;
        targetX = x + xSpeed;
        targetY = y + ySpeed;
        targetZ = z + zSpeed;
        gravity = 0;
        hasPhysics = false;
        lifetime = 26 + random.nextInt(10);
        quadSize = 0.1f;

        setSprite(sprites.get(random));
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime) {
            remove();
            return;
        }

        float progress = (float) age / lifetime;
        setPos(Mth.lerp(progress, startX, targetX), Mth.lerp(progress, startY, targetY), Mth.lerp(progress, startZ, targetZ));
        setAlpha(1 - progress * progress * progress * progress);
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
