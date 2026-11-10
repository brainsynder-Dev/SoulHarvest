package org.bsdevelopment.soul_harvest.fluids;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

public class LiquidExperienceFluidType extends FluidType {
    public LiquidExperienceFluidType(Properties properties) {
        super(properties);
    }

    @Override
    public boolean move(LivingEntity entity, Vec3 movementVector, double gravity) {
        boolean isFalling = entity.getDeltaMovement().y <= 0;
        double oldY = entity.getY();
        float slowDown = entity.isSprinting() ? 0.9f : 0.8f;
        float speed = 0.02f * (float) entity.getAttributeValue(NeoForgeMod.SWIM_SPEED);

        entity.moveRelative(speed, movementVector);
        entity.move(MoverType.SELF, entity.getDeltaMovement());

        Vec3 movement = entity.getDeltaMovement();
        if (entity.horizontalCollision && entity.onClimbable()) movement = new Vec3(movement.x, 0.2, movement.z);
        entity.setDeltaMovement(entity.getFluidFallingAdjustedMovement(gravity, isFalling, movement.multiply(slowDown, 0.8f, slowDown)));

        movement = entity.getDeltaMovement();
        if (entity.horizontalCollision && entity.isFree(movement.x, movement.y + 0.6f - entity.getY() + oldY, movement.z)) entity.setDeltaMovement(movement.x, 0.3f, movement.z);

        return true;
    }
}
