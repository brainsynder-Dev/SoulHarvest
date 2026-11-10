package org.bsdevelopment.soul_harvest.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.bsdevelopment.soul_harvest.items.base.BaseItem;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModTags;

public class SoulAshSeedItem extends BaseItem {
    public SoulAshSeedItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos).is(ModTags.SOUL_ASH_CONVERTIBLE)) return InteractionResult.PASS;

        if (level instanceof ServerLevel serverLevel) {
            ModBlocks.SOUL_ASH.get().startGrowth(serverLevel, pos);
            context.getItemInHand().consume(1, context.getPlayer());
        }

        return InteractionResult.SUCCESS;
    }
}
