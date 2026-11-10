package org.bsdevelopment.soul_harvest.network.client;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.bsdevelopment.soul_harvest.client.DebugChatHandler;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public record DebugChatMessagePayload(Component message) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DebugChatMessagePayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "debug_message"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DebugChatMessagePayload> CODEC = ComponentSerialization.TRUSTED_STREAM_CODEC.map(DebugChatMessagePayload::new, DebugChatMessagePayload::message);

    public void process(IPayloadContext context) {
        context.enqueueWork(() -> DebugChatHandler.showMessage(message));
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
