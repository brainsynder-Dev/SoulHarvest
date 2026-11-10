package org.bsdevelopment.soul_harvest.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.bsdevelopment.soul_harvest.blocks.entity.WisdomVesselBlockEntity;
import org.bsdevelopment.soul_harvest.registry.ModComponents;
import org.bsdevelopment.soul_harvest.registry.ModFluids;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class WisdomVesselItemRenderer implements SpecialModelRenderer<Float> {
    @Override
    public Float extractArgument(ItemStack stack) {
        SimpleFluidContent contents = stack.getOrDefault(ModComponents.FLUID_CONTENTS.get(), SimpleFluidContent.EMPTY);
        return Math.min(1f, (float) contents.getAmount() / WisdomVesselBlockEntity.getCapacity());
    }

    @Override
    public void submit(Float fillLevel, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (fillLevel == null || fillLevel <= 0) return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(ModFluids.LIQUID_EXPERIENCE.get().defaultFluidState()).stillMaterial().sprite();
        WisdomVesselRenderer.submitFluid(poseStack, submitNodeCollector, sprite, fillLevel);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(4.1f / 16, 3.1f / 16, 4.1f / 16));
        output.accept(new Vector3f(11.9f / 16, 12.9f / 16, 11.9f / 16));
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<Float> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<Float> bake(BakingContext context) {
            return new WisdomVesselItemRenderer();
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
