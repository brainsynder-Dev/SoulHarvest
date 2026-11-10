package org.bsdevelopment.soul_harvest.client;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Vector4f;

public class LiquidExperienceClientExtensions implements IClientFluidTypeExtensions {
    @Override
    public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
        int color = TextColor.parseColor("#6FBE4E").getOrThrow().getValue();
        fluidFogColor.set(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), 1f);
    }

    @Override
    public void modifyFogRender(Camera camera, FogEnvironment environment, float renderDistance, float partialTick, FogData fogData) {
        fogData.environmentalStart = 0f;
        fogData.environmentalEnd = 6f;
        fogData.skyEnd = fogData.environmentalEnd;
        fogData.cloudEnd = fogData.environmentalEnd;
    }
}
