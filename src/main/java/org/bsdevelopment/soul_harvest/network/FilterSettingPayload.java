package org.bsdevelopment.soul_harvest.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.bsdevelopment.soul_harvest.container.FilterContainer;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public record FilterSettingPayload(int setting, int slot, int value, String text) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FilterSettingPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "filter_setting"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FilterSettingPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, FilterSettingPayload::setting,
            ByteBufCodecs.VAR_INT, FilterSettingPayload::slot,
            ByteBufCodecs.VAR_INT, FilterSettingPayload::value,
            ByteBufCodecs.stringUtf8(256), FilterSettingPayload::text,
            FilterSettingPayload::new
    );

    public void process(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player().containerMenu instanceof FilterContainer menu) || !menu.stillValid(context.player())) return;

            menu.applySetting(setting, slot, value, text);
        });
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
