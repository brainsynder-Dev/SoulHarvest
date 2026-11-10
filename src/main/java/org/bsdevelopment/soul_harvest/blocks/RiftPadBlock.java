package org.bsdevelopment.soul_harvest.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bsdevelopment.soul_harvest.blocks.base.BasePlateBlock;
import org.bsdevelopment.soul_harvest.blocks.entity.RiftPadBlockEntity;

public class RiftPadBlock extends BasePlateBlock implements EntityBlock {
    public static final BooleanProperty LINKED = BooleanProperty.create("linked");

    public RiftPadBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(LINKED, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RiftPadBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LINKED);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (!(level instanceof ServerLevel serverLevel) || !state.getValue(LINKED)) return;
        if (entity.isCrouching() || entity.getY() > pos.getY() + 0.4) return;

        if (level.getBlockEntity(pos) instanceof RiftPadBlockEntity riftPad) riftPad.teleportEntity(serverLevel, entity);
    }
}
