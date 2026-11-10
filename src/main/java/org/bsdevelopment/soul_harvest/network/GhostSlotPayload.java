package org.bsdevelopment.soul_harvest.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.bsdevelopment.soul_harvest.container.base.GhostSlotContainer;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public record GhostSlotPayload(int slotId, ItemStack stack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GhostSlotPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "ghost_slot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GhostSlotPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, GhostSlotPayload::slotId,
            ItemStack.OPTIONAL_STREAM_CODEC, GhostSlotPayload::stack,
            GhostSlotPayload::new
    );

    public void process(IPayloadContext context) {
        context.enqueueWork(() -> {
            AbstractContainerMenu menu = context.player().containerMenu;
            if (!(menu instanceof GhostSlotContainer ghostSlotContainer) || !menu.stillValid(context.player())) return;
            if (!ghostSlotContainer.isGhostSlot(slotId)) return;

            ghostSlotContainer.setGhostItem(slotId, stack);
        });
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
