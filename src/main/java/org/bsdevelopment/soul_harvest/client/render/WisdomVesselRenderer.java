package org.bsdevelopment.soul_harvest.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;
import org.bsdevelopment.soul_harvest.blocks.entity.WisdomVesselBlockEntity;
import org.bsdevelopment.soul_harvest.client.render.state.WisdomVesselRenderState;
import org.bsdevelopment.soul_harvest.registry.ModFluids;

public class WisdomVesselRenderer implements BlockEntityRenderer<WisdomVesselBlockEntity, WisdomVesselRenderState> {
    public static void submitFluid(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, TextureAtlasSprite sprite, float fillLevel) {
        float minX = 4.1f;
        float minY = 3.1f;
        float maxX = 11.9f;
        float maxY = Math.min(3 + (float) Math.ceil(fillLevel * 10), 12.9f);

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(sprite.atlasLocation()), (pose, buffer) -> {
            quad(pose, buffer, sprite, minX, maxY, minX, minX, maxY, maxX, maxX, maxY, maxX, maxX, maxY, minX, 0, 1, 0);
            quad(pose, buffer, sprite, maxX, minY, minX, minX, minY, minX, minX, maxY, minX, maxX, maxY, minX, 0, 0, -1);
            quad(pose, buffer, sprite, minX, minY, maxX, maxX, minY, maxX, maxX, maxY, maxX, minX, maxY, maxX, 0, 0, 1);
            quad(pose, buffer, sprite, minX, minY, minX, minX, minY, maxX, minX, maxY, maxX, minX, maxY, minX, -1, 0, 0);
            quad(pose, buffer, sprite, maxX, minY, maxX, maxX, minY, minX, maxX, maxY, minX, maxX, maxY, maxX, 1, 0, 0);
        });
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer buffer, TextureAtlasSprite sprite, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int normalX, int normalY, int normalZ) {
        vertex(pose, buffer, sprite, x1, y1, z1, normalX, normalY, normalZ);
        vertex(pose, buffer, sprite, x2, y2, z2, normalX, normalY, normalZ);
        vertex(pose, buffer, sprite, x3, y3, z3, normalX, normalY, normalZ);
        vertex(pose, buffer, sprite, x4, y4, z4, normalX, normalY, normalZ);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, TextureAtlasSprite sprite, float x, float y, float z, int normalX, int normalY, int normalZ) {
        float u = normalX != 0 ? z : x;
        float v = normalY != 0 ? z : 16 - y;
        buffer.addVertex(pose, x / 16, y / 16, z / 16).setColor(-1).setUv(sprite.getU(u / 16), sprite.getV(v / 16))
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(pose, normalX, normalY, normalZ);
    }

    @Override
    public WisdomVesselRenderState createRenderState() {
        return new WisdomVesselRenderState();
    }

    @Override
    public void extractRenderState(WisdomVesselBlockEntity entity, WisdomVesselRenderState state, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPos, breakProgress);
        state.LEVEL = entity.getFillLevel();
        state.FLUID = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(ModFluids.LIQUID_EXPERIENCE.get().defaultFluidState()).stillMaterial().sprite();
    }

    @Override
    public void submit(WisdomVesselRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.LEVEL <= 0) return;

        submitFluid(poseStack, submitNodeCollector, state.FLUID, state.LEVEL);
    }
}
