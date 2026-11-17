package org.bsdevelopment.soul_harvest.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.Mob;
import org.bsdevelopment.soul_harvest.blocks.entity.HushedMaskBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public class MobHushMixin {
    @WrapWithCondition(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;playAmbientSound()V"))
    private boolean soul_harvest$hushAmbientSound(Mob mob) {
        return !HushedMaskBlockEntity.isHushed(mob, HushedMaskBlockEntity.MuteType.IDLE);
    }
}
