package org.bsdevelopment.soul_harvest.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import org.bsdevelopment.soul_harvest.client.render.WraithfireSprites;
import org.bsdevelopment.soul_harvest.utilities.Wraithfire;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
    @WrapOperation(method = "renderScreenEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/sprite/SpriteGetter;get(Lnet/minecraft/client/resources/model/sprite/SpriteId;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"))
    private TextureAtlasSprite soul_harvest$wraithfireOverlay(SpriteGetter sprites, SpriteId spriteId, Operation<TextureAtlasSprite> original) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !Wraithfire.isBurning(minecraft.player)) return original.call(sprites, spriteId);

        return original.call(sprites, WraithfireSprites.swap(spriteId));
    }
}
