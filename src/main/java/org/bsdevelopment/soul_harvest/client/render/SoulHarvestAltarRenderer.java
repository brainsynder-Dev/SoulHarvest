package org.bsdevelopment.soul_harvest.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.bsdevelopment.soul_harvest.blocks.entity.SoulHarvestAltarBlockEntity;
import org.bsdevelopment.soul_harvest.client.render.state.SoulHarvestAltarRenderState;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public class SoulHarvestAltarRenderer implements BlockEntityRenderer<SoulHarvestAltarBlockEntity, SoulHarvestAltarRenderState> {
    private final ItemModelResolver itemModel;
    private ItemStack saw;

    public SoulHarvestAltarRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModel = context.itemModelResolver();
    }

    @Override
    public SoulHarvestAltarRenderState createRenderState() {
        return new SoulHarvestAltarRenderState();
    }

    @Override
    public void extractRenderState(SoulHarvestAltarBlockEntity entity, SoulHarvestAltarRenderState state, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPos, breakProgress);
        state.ROTATION = Mth.lerp(partialTicks, entity.previousSawRotation, entity.sawRotation);
        itemModel.updateForTopItem(state.SAW, getSaw(), ItemDisplayContext.NONE, entity.getLevel(), null, 0);
    }

    @Override
    public void submit(SoulHarvestAltarRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.ROTATION));
        state.SAW.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    private ItemStack getSaw() {
        if (saw == null) {
            saw = new ItemStack(ModBlocks.SOUL_HARVEST_ALTAR.getItem());
            saw.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "soul_harvest_altar_saw"));
        }

        return saw;
    }
}
