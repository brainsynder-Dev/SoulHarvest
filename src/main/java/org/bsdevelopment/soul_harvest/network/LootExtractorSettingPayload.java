package org.bsdevelopment.soul_harvest.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.bsdevelopment.soul_harvest.container.LootExtractorContainer;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public record LootExtractorSettingPayload(BlockPos pos, int setting, int value) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LootExtractorSettingPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "loot_extractor_setting"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LootExtractorSettingPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, LootExtractorSettingPayload::pos,
            ByteBufCodecs.VAR_INT, LootExtractorSettingPayload::setting,
            ByteBufCodecs.VAR_INT, LootExtractorSettingPayload::value,
            LootExtractorSettingPayload::new
    );

    public void process(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player().containerMenu instanceof LootExtractorContainer menu)) return;
            if (!menu.blockEntity.getBlockPos().equals(pos) || !menu.stillValid(context.player())) return;

            menu.blockEntity.applySetting(setting, value);
        });
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
