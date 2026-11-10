package org.bsdevelopment.soul_harvest.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bsdevelopment.soul_harvest.blocks.base.DirectionalPlateBlock;

public class SoulstreamPadBlock extends DirectionalPlateBlock {
    public SoulstreamPadBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (entity.isCrouching() || entity.getY() > pos.getY() + 0.4) return;

        Direction direction = state.getValue(FACING);
        Vec3 movement = entity.getDeltaMovement().add(direction.getStepX() * 0.12, 0, direction.getStepZ() * 0.12);
        double horizontalSpeed = movement.horizontalDistance();
        if (horizontalSpeed > 0.6) movement = movement.multiply(0.6 / horizontalSpeed, 1, 0.6 / horizontalSpeed);
        entity.setDeltaMovement(movement);
    }
}
