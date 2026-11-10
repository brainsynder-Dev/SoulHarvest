package org.bsdevelopment.soul_harvest.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bsdevelopment.soul_harvest.blocks.entity.base.ILinkable;
import org.bsdevelopment.soul_harvest.items.base.BaseItem;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModComponents;

import java.util.function.Consumer;

public class SoulWandItem extends BaseItem {
    public SoulWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.is(ModBlocks.SOUL_HARVEST_ALTAR.getBlock()))
            return bind(context, player, ModComponents.LINKED_ALTAR.get(), ModComponents.LINKED_DESTINATION.get(), "message.soul_harvest.wand.bound");
        if (state.is(ModBlocks.DESTINATION_PAD.getBlock()))
            return bind(context, player, ModComponents.LINKED_DESTINATION.get(), ModComponents.LINKED_ALTAR.get(), "message.soul_harvest.wand.destination_bound");
        if (state.is(ModBlocks.LOOT_EXTRACTOR.getBlock()))
            return link(context, player, ModComponents.LINKED_ALTAR.get(), 32, "wand.no_altar", "wand.too_far", "wand.linked", "wand.unlinked");
        if (state.is(ModBlocks.WISDOM_VESSEL.getBlock()))
            return link(context, player, ModComponents.LINKED_ALTAR.get(), 32, "wand.no_altar", "wand.vessel_too_far", "wand.vessel_linked", "wand.vessel_unlinked");
        if (state.is(ModBlocks.RIFT_PAD.getBlock()))
            return link(context, player, ModComponents.LINKED_DESTINATION.get(), 128, "wand.no_destination", "wand.rift_too_far", "wand.rift_linked", "wand.rift_unlinked");
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;

        if (!level.isClientSide()) {
            ItemStack wand = player.getItemInHand(hand);
            wand.remove(ModComponents.LINKED_ALTAR.get());
            wand.remove(ModComponents.LINKED_DESTINATION.get());
            player.sendSystemMessage(Component.translatable("message.soul_harvest.wand.cleared"));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendItemInformation(ItemStack stack, TooltipContext context, Consumer<Component> info, boolean advanced) {
        GlobalPos altar = stack.get(ModComponents.LINKED_ALTAR.get());
        GlobalPos destination = stack.get(ModComponents.LINKED_DESTINATION.get());

        if (altar != null) {
            info.accept(Component.translatable(getDescriptionId() + ".linked", altar.pos().getX(), altar.pos().getY(), altar.pos().getZ()));
        } else if (destination != null) {
            info.accept(Component.translatable(getDescriptionId() + ".linked_destination", destination.pos().getX(), destination.pos().getY(), destination.pos().getZ()));
        } else {
            info.accept(Component.translatable(getDescriptionId() + ".unlinked"));
        }
    }

    private InteractionResult bind(UseOnContext context, Player player, DataComponentType<GlobalPos> component, DataComponentType<GlobalPos> otherComponent, String messageKey) {
        Level level = context.getLevel();
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ItemStack wand = context.getItemInHand();
        wand.remove(otherComponent);
        wand.set(component, GlobalPos.of(level.dimension(), context.getClickedPos()));
        playSound(level, context.getClickedPos(), 0.8f);
        player.sendSystemMessage(Component.translatable(messageKey));
        return InteractionResult.SUCCESS;
    }

    private InteractionResult link(UseOnContext context, Player player, DataComponentType<GlobalPos> component, int range,
                                   String missingKey, String tooFarKey, String linkedKey, String unlinkedKey) {
        Level level = context.getLevel();
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockPos pos = context.getClickedPos();
        if (!(level.getBlockEntity(pos) instanceof ILinkable linkable)) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {
            linkable.linkPosition(null);
            player.sendSystemMessage(Component.translatable("message.soul_harvest." + unlinkedKey));
            return InteractionResult.SUCCESS;
        }

        GlobalPos source = context.getItemInHand().get(component);
        if (source == null) {
            player.sendSystemMessage(Component.translatable("message.soul_harvest." + missingKey));
            return InteractionResult.FAIL;
        }

        if (!source.dimension().equals(level.dimension()) || !source.pos().closerThan(pos, range)) {
            player.sendSystemMessage(Component.translatable("message.soul_harvest." + tooFarKey));
            return InteractionResult.FAIL;
        }

        linkable.linkPosition(source.pos());
        playSound(level, pos, 1.2f);
        player.sendSystemMessage(Component.translatable("message.soul_harvest." + linkedKey));
        return InteractionResult.SUCCESS;
    }

    private void playSound(Level level, BlockPos pos, float pitch) {
        level.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 0.5f, pitch);
    }
}
