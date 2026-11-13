package org.bsdevelopment.soul_harvest.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bsdevelopment.soul_harvest.blocks.entity.SoulRitualShrineBlockEntity;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModItems;
import org.bsdevelopment.soul_harvest.registry.ModParticles;
import org.bsdevelopment.soul_harvest.registry.ModTags;
import org.jspecify.annotations.Nullable;

public class SoulRitualShrineBlock extends Block implements EntityBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    private static final double[][] WICKS = {{0.0625, 0.8125, 0.0625}, {0.9375, 0.8125, 0.0625}, {0.0625, 0.8125, 0.9375}, {0.9375, 0.8125, 0.9375}};
    private static final VoxelShape SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 4, 16), Block.box(2, 4, 2, 14, 8, 14));

    public SoulRitualShrineBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }

    public static boolean isOnShrine(Mob mob, BlockPos pos) {
        return mob.isAlive() && !mob.isPassenger() && mob.blockPosition().equals(pos);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SoulRitualShrineBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide() || type != ModBlocks.SOUL_RITUAL_SHRINE.getTileEntityType()) return null;

        return (tickLevel, pos, tickState, blockEntity) -> ((SoulRitualShrineBlockEntity) blockEntity).tickRitual((ServerLevel) tickLevel);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) return;

        for (double[] wick : WICKS) {
            if (random.nextInt(3) == 0) level.addParticle(ModParticles.SOUL_SMOKE.get(), pos.getX() + wick[0], pos.getY() + wick[1], pos.getZ() + wick[2], 0, 0, 0);
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!itemStack.is(ModItems.SOUL_CRYSTAL.get())) return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof SoulRitualShrineBlockEntity shrine)) return InteractionResult.FAIL;

        if (state.getValue(ACTIVE)) {
            player.sendSystemMessage(Component.translatable("message.soul_harvest.soul_ritual_shrine.busy"));
            return InteractionResult.FAIL;
        }

        Mob mob = level.getEntitiesOfClass(Mob.class, new AABB(pos), target -> isOnShrine(target, pos)).stream().findFirst().orElse(null);
        if (mob == null) {
            player.sendSystemMessage(Component.translatable("message.soul_harvest.soul_ritual_shrine.no_mob"));
            return InteractionResult.FAIL;
        }

        if (mob.typeHolder().is(ModTags.RITUAL_BLACKLIST) || SpawnEggItem.byId(mob.getType()).isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.soul_harvest.soul_ritual_shrine.no_egg"));
            return InteractionResult.FAIL;
        }

        itemStack.consume(1, player);
        level.setBlockAndUpdate(pos, state.setValue(ACTIVE, true));
        shrine.startRitual(mob);
        serverLevel.playSound(null, pos, SoundEvents.SOUL_ESCAPE.value(), SoundSource.BLOCKS, 1f, 0.6f);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
