package org.bsdevelopment.soul_harvest.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.SpriteId;
import org.bsdevelopment.soul_harvest.client.render.WraithfireSprites;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlameFeatureRenderer.class)
public class FlameFeatureRendererMixin {
    @WrapOperation(method = "renderFlame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/sprite/AtlasManager;get(Lnet/minecraft/client/resources/model/sprite/SpriteId;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"))
    private TextureAtlasSprite soul_harvest$wraithfireSprite(AtlasManager atlasManager, SpriteId spriteId, Operation<TextureAtlasSprite> original, @Local(argsOnly = true) EntityRenderState state) {
        if (!Boolean.TRUE.equals(state.getRenderData(WraithfireSprites.BURNING))) return original.call(atlasManager, spriteId);
        return original.call(atlasManager, WraithfireSprites.swap(spriteId));
    }
}
