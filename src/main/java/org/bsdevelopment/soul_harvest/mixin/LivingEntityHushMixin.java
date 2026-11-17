package org.bsdevelopment.soul_harvest.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.bsdevelopment.soul_harvest.blocks.entity.HushedMaskBlockEntity;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityHushMixin {
    @ModifyExpressionValue(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDeathSound()Lnet/minecraft/sounds/SoundEvent;"))
    private @Nullable SoundEvent soul_harvest$hushDeathSound(@Nullable SoundEvent deathSound) {
        return HushedMaskBlockEntity.isHushed((Entity) (Object) this, HushedMaskBlockEntity.MuteType.DEATH) ? null : deathSound;
    }

    @WrapWithCondition(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private boolean soul_harvest$hushHurtSound(LivingEntity entity, DamageSource source) {
        return !HushedMaskBlockEntity.isHushed(entity, HushedMaskBlockEntity.MuteType.HURT);
    }

    @WrapWithCondition(method = "tickDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"))
    private boolean soul_harvest$hushDeathPuff(Level level, Entity entity, byte eventId) {
        return !HushedMaskBlockEntity.isHushed(entity, HushedMaskBlockEntity.MuteType.PUFF);
    }
}
