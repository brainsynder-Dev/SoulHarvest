package org.bsdevelopment.soul_harvest.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bsdevelopment.soul_harvest.registry.ModItems;
import org.bsdevelopment.soul_harvest.utilities.Wraithfire;

public class WraithfireBlock extends BaseFireBlock {
    public static final MapCodec<WraithfireBlock> CODEC = simpleCodec(WraithfireBlock::new);

    public WraithfireBlock(Properties properties) {
        super(properties, 2f);
    }

    @Override
    protected MapCodec<WraithfireBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean canBurn(BlockState state) {
        return true;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        return level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbor, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        return canSurvive(state, level, pos) ? defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (!level.isClientSide() && (entity instanceof ItemEntity itemEntity)) convertCoal(level, itemEntity);

        super.entityInside(state, level, pos, entity, effectApplier, isPrecise);
        effectApplier.runAfter(InsideBlockEffectType.FIRE_IGNITE, Wraithfire::markBurning);
    }

    private void convertCoal(Level level, ItemEntity itemEntity) {
        ItemStack coal = itemEntity.getItem();
        int emberCount = coal.getCount() / 4;
        if (!coal.is(ItemTags.COALS) || (emberCount == 0)) return;

        itemEntity.setItem(coal.copyWithCount(coal.getCount() - emberCount * 4));
        level.addFreshEntity(new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), new ItemStack(ModItems.SOUL_EMBER.get(), emberCount)));
    }
}
