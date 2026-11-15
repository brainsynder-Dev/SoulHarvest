package org.bsdevelopment.soul_harvest.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.bsdevelopment.soul_harvest.registry.ModItems;
import org.jspecify.annotations.Nullable;

public class SoulBrazierBlock extends Block {
    private static final VoxelShape SHAPE = Shapes.or(Block.box(3, 0, 3, 13, 2, 13), Block.box(1, 2, 1, 15, 4, 15), Block.box(0, 4, 0, 16, 9, 16));

    public SoulBrazierBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.LIT, false));
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if ((itemAbility == ItemAbilities.SHOVEL_DOUSE) && state.getValue(BlockStateProperties.LIT)) return state.setValue(BlockStateProperties.LIT, false);

        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.LIT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!itemStack.is(ModItems.SOUL_EMBER.get()) || state.getValue(BlockStateProperties.LIT)) return InteractionResult.TRY_WITH_EMPTY_HAND;

        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, true), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1f, 0.8f);
            itemStack.consume(1, player);
        }
        return InteractionResult.SUCCESS;
    }
}
