package org.bsdevelopment.soul_harvest.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public class RitualRenderData {
    public static final ContextKey<Float> PALENESS = new ContextKey<>(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "ritual_paleness"));

    public static void apply(int ritualTicks, EntityRenderState renderState) {
        float ritualTime = ritualTicks + renderState.partialTick;
        renderState.setRenderData(PALENESS, Math.clamp((ritualTime - 40) / 16f, 0f, 0.9f));
        if (!(renderState instanceof LivingEntityRenderState livingState)) return;

        float shrinkProgress = Math.clamp((ritualTime - 56) / 16f, 0f, 1f);
        float shrinkAmount = shrinkProgress * shrinkProgress * 0.9f;
        livingState.y += renderState.boundingBoxHeight * shrinkAmount * 0.5;
        livingState.scale *= 1 - shrinkAmount;
    }
}
