package org.bsdevelopment.soul_harvest.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.bsdevelopment.soul_harvest.blocks.entity.WisdomVesselBlockEntity;
import org.bsdevelopment.soul_harvest.container.WisdomVesselContainer;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModItems;
import org.bsdevelopment.soul_harvest.registry.ModParticles;
import org.jspecify.annotations.Nullable;

public class WisdomVesselBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(2, 0, 2, 14, 2, 14),
            Block.box(2, 2, 2, 14, 16, 14),
            Block.box(6, 16, 6, 10, 19, 10)
    );

    public WisdomVesselBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WisdomVesselBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (itemStack.is(ModItems.SOUL_WAND.get())) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof WisdomVesselBlockEntity vessel)) return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (!FluidUtil.interactWithFluidHandler(player, hand, pos, vessel.getTank(), null)) return InteractionResult.TRY_WITH_EMPTY_HAND;

        if (level instanceof ServerLevel serverLevel)
            serverLevel.sendParticles(ModParticles.XP_SPARKLE.get(), pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 8, 0.2, 0.1, 0.2, 0.02);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof WisdomVesselBlockEntity vessel)) return InteractionResult.PASS;

        player.openMenu(new SimpleMenuProvider((windowId, playerInventory, menuPlayer) -> new WisdomVesselContainer(windowId, playerInventory, pos, vessel.getDataAccess()), getName()), buffer -> buffer.writeBlockPos(pos));
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide() || type != ModBlocks.WISDOM_VESSEL.getTileEntityType()) return null;
        return (tickLevel, pos, tickState, blockEntity) -> {
            WisdomVesselBlockEntity vessel = (WisdomVesselBlockEntity) blockEntity;
            vessel.processBuckets();
            vessel.drawFromAltar((ServerLevel) tickLevel);
            vessel.drainIntoVesselsBelow((ServerLevel) tickLevel);
        };
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
