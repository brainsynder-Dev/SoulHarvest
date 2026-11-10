package org.bsdevelopment.soul_harvest.items;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bsdevelopment.soul_harvest.blocks.SoulAshBlock;
import org.bsdevelopment.soul_harvest.items.base.BaseItem;

public class SoulCrystalItem extends BaseItem {
    public SoulCrystalItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos center = context.getClickedPos();
        BlockState state = level.getBlockState(center);
        if (player == null || !(state.getBlock() instanceof SoulAshBlock soulAsh)) return InteractionResult.PASS;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.SUCCESS;

        if (state.getValue(SoulAshBlock.SIGIL) != 0) {
            player.sendSystemMessage(Component.translatable("message.soul_harvest.sigil.already_awakened"));
            return InteractionResult.FAIL;
        }

        int size = soulAsh.findPadSize(level, center);
        if (size == 0) {
            player.sendSystemMessage(Component.translatable("message.soul_harvest.sigil.incomplete"));
            return InteractionResult.FAIL;
        }

        int crystalCost = SoulAshBlock.getCrystalCost(size);
        ItemStack crystals = context.getItemInHand();
        if (!player.hasInfiniteMaterials() && crystals.getCount() < crystalCost) {
            player.sendSystemMessage(Component.translatable("message.soul_harvest.sigil.no_crystals", crystalCost));
            return InteractionResult.FAIL;
        }

        crystals.consume(crystalCost, player);

        soulAsh.awaken(serverLevel, center, size);
        level.playSound(null, center, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1f, 0.7f);
        player.sendSystemMessage(Component.translatable("message.soul_harvest.sigil.awakened"));
        return InteractionResult.SUCCESS;
    }
}
