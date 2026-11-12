package org.bsdevelopment.soul_harvest.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.bsdevelopment.soul_harvest.blocks.base.SpawnPadBlock;
import org.bsdevelopment.soul_harvest.items.base.BaseItem;
import org.bsdevelopment.soul_harvest.registry.ModTags;

import java.util.function.Supplier;

public class SpawnPadSeedItem extends BaseItem {
    private final Supplier<? extends SpawnPadBlock> spawnPad;

    public SpawnPadSeedItem(Supplier<? extends SpawnPadBlock> spawnPad, Properties properties) {
        super(properties);
        this.spawnPad = spawnPad;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos).is(ModTags.SOUL_ASH_CONVERTIBLE)) return InteractionResult.PASS;

        if (level instanceof ServerLevel serverLevel) {
            spawnPad.get().startGrowth(serverLevel, pos);
            context.getItemInHand().consume(1, context.getPlayer());
        }

        return InteractionResult.SUCCESS;
    }
}
