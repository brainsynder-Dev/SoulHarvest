package org.bsdevelopment.soul_harvest.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

public class AltarPillarBlock extends Block {
    public static final MapCodec<AltarPillarBlock> CODEC = simpleCodec(AltarPillarBlock::new);
    private final Function<BlockState, VoxelShape> shapes;

    public AltarPillarBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.UP, false).setValue(BlockStateProperties.DOWN, false));
        shapes = getShapeForEachState(this::calculateShape);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        return defaultBlockState().setValue(BlockStateProperties.UP, level.getBlockState(pos.above()).is(this)).setValue(BlockStateProperties.DOWN, level.getBlockState(pos.below()).is(this));
    }

    @Override
    protected MapCodec<AltarPillarBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.UP, BlockStateProperties.DOWN);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.apply(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbor, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (directionToNeighbor == Direction.UP) return state.setValue(BlockStateProperties.UP, neighborState.is(this));
        if (directionToNeighbor == Direction.DOWN) return state.setValue(BlockStateProperties.DOWN, neighborState.is(this));

        return state;
    }

    private VoxelShape calculateShape(BlockState state) {
        VoxelShape shape = Block.box(2, 0, 2, 14, 16, 14);
        if (!state.getValue(BlockStateProperties.DOWN)) shape = Shapes.or(shape, Block.box(0, 0, 0, 16, 2, 16), Block.box(1, 2, 1, 15, 3, 15));
        if (!state.getValue(BlockStateProperties.UP)) shape = Shapes.or(shape, Block.box(1, 13, 1, 15, 14, 15), Block.box(0, 14, 0, 16, 16, 16));

        return shape;
    }
}
