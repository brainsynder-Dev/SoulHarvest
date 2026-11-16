package org.bsdevelopment.soul_harvest.network.client;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public record RiftTeleportPayload(int entityId, Vec3 position, float yaw) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RiftTeleportPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "rift_teleport"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RiftTeleportPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RiftTeleportPayload::entityId,
            Vec3.STREAM_CODEC, RiftTeleportPayload::position,
            ByteBufCodecs.FLOAT, RiftTeleportPayload::yaw,
            RiftTeleportPayload::new
    );

    public void process(IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(entityId);
            if (entity == null) return;

            InterpolationHandler interpolation = entity.getInterpolation();
            if (interpolation != null) interpolation.cancel();

            entity.setPos(position);
            entity.snapTo(position, yaw, entity.getXRot());
        });
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
