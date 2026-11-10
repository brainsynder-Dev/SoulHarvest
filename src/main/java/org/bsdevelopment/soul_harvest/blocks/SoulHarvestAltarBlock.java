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
import org.bsdevelopment.soul_harvest.blocks.base.RedstonePoweredBlock;
import org.bsdevelopment.soul_harvest.blocks.entity.SoulHarvestAltarBlockEntity;
import org.bsdevelopment.soul_harvest.container.SoulHarvestAltarContainer;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModItems;
import org.jspecify.annotations.Nullable;

public class SoulHarvestAltarBlock extends RedstonePoweredBlock implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 11.5, 16),
            Block.box(0, 11.5, 0, 3.5, 16, 3.5),
            Block.box(12.5, 11.5, 0, 16, 16, 3.5),
            Block.box(0, 11.5, 12.5, 3.5, 16, 16),
            Block.box(12.5, 11.5, 12.5, 16, 16, 16),
            Block.box(6.25, 11.5, 6.25, 9.75, 15, 9.75)
    );

    public SoulHarvestAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SoulHarvestAltarBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (itemStack.is(ModItems.SOUL_WAND.get())) return InteractionResult.PASS;
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        player.openMenu(new SimpleMenuProvider((windowId, playerInventory, menuPlayer) -> new SoulHarvestAltarContainer(windowId, playerInventory, pos), getName()), buffer -> buffer.writeBlockPos(pos));
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlocks.SOUL_HARVEST_ALTAR.getTileEntityType()) return null;
        if (level.isClientSide()) return (tickLevel, pos, tickState, blockEntity) -> ((SoulHarvestAltarBlockEntity) blockEntity).tickSaw(isPowered(tickState));

        return (tickLevel, pos, tickState, blockEntity) -> {
            SoulHarvestAltarBlockEntity altar = (SoulHarvestAltarBlockEntity) blockEntity;
            altar.tickAttack((ServerLevel) tickLevel, isPowered(tickState));
            altar.absorbItems((ServerLevel) tickLevel);
            altar.distributeDrops((ServerLevel) tickLevel);
        };
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
