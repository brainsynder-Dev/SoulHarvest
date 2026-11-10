package org.bsdevelopment.soul_harvest.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.bsdevelopment.soul_harvest.network.client.DebugChatMessagePayload;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public class PacketHandler {
    public static void registerNetworking(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(ModConstants.MOD_ID);

        registrar.playToClient(DebugChatMessagePayload.ID, DebugChatMessagePayload.CODEC, DebugChatMessagePayload::process);
        registrar.playToServer(GhostSlotPayload.ID, GhostSlotPayload.CODEC, GhostSlotPayload::process);
        registrar.playToServer(FilterSettingPayload.ID, FilterSettingPayload.CODEC, FilterSettingPayload::process);
        registrar.playToServer(LootExtractorSettingPayload.ID, LootExtractorSettingPayload.CODEC, LootExtractorSettingPayload::process);
    }

    public static void sendPayload(Player player, CustomPacketPayload payload) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        PacketDistributor.sendToPlayer(serverPlayer, payload);
    }

    public static void sendPayload(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }
}
