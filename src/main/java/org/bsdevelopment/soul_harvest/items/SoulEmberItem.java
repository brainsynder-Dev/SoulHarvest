package org.bsdevelopment.soul_harvest.items;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bsdevelopment.soul_harvest.items.base.BaseItem;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.utilities.Wraithfire;

public class SoulEmberItem extends BaseItem {
    public SoulEmberItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos firePos = context.getClickedPos().relative(context.getClickedFace());
        BlockState wraithfire = ModBlocks.WRAITHFIRE.get().defaultBlockState();
        if (!level.getBlockState(firePos).canBeReplaced() || !wraithfire.canSurvive(level, firePos))
            return InteractionResult.FAIL;

        if (!level.isClientSide()) {
            level.setBlock(firePos, wraithfire, Block.UPDATE_ALL_IMMEDIATE);
            level.playSound(null, firePos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1f, 0.8f);
            context.getItemInHand().consume(1, context.getPlayer());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity target, InteractionHand hand) {
        if (target.fireImmune()) return InteractionResult.PASS;

        if (!player.level().isClientSide()) {
            Wraithfire.ignite(target, 8f);
            player.level().playSound(null, target.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1f, 0.8f);
            itemStack.consume(1, player);
        }
        return InteractionResult.SUCCESS;
    }
}
