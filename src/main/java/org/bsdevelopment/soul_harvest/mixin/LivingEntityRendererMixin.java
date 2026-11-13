package org.bsdevelopment.soul_harvest.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.bsdevelopment.soul_harvest.client.render.RitualRenderData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @ModifyVariable(method = "getOverlayCoords", at = @At("HEAD"), argsOnly = true)
    private static float soul_harvest$ritualPaleness(float whiteOverlayProgress, @Local(argsOnly = true) LivingEntityRenderState state) {
        Float paleness = state.getRenderData(RitualRenderData.PALENESS);
        if (paleness == null) return whiteOverlayProgress;
        return Math.max(whiteOverlayProgress, paleness);
    }
}
