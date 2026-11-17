package org.bsdevelopment.soul_harvest.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.bsdevelopment.soul_harvest.blocks.entity.HushedMaskBlockEntity;
import org.bsdevelopment.soul_harvest.blocks.entity.HushedMaskBlockEntity.MuteType;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

import java.util.HashSet;
import java.util.Set;

public record HushSettingsPayload(BlockPos pos, int width, int height, int depth, Set<MuteType> mutedTypes) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<HushSettingsPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "hush_settings"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HushSettingsPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, HushSettingsPayload::pos,
            ByteBufCodecs.VAR_INT, HushSettingsPayload::width,
            ByteBufCodecs.VAR_INT, HushSettingsPayload::height,
            ByteBufCodecs.VAR_INT, HushSettingsPayload::depth,
            NeoForgeStreamCodecs.enumCodec(MuteType.class).apply(ByteBufCodecs.collection(HashSet::new)), HushSettingsPayload::mutedTypes,
            HushSettingsPayload::new
    );

    public void process(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!context.player().isWithinBlockInteractionRange(pos, 1)) return;
            if (!(context.player().level().getBlockEntity(pos) instanceof HushedMaskBlockEntity mask)) return;

            mask.applySettings(width, height, depth, mutedTypes);
        });
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
