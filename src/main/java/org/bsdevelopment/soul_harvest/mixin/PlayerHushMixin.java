package org.bsdevelopment.soul_harvest.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bsdevelopment.soul_harvest.blocks.entity.HushedMaskBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerHushMixin {
    @WrapOperation(method = "damageStatsAndHearts", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    private int soul_harvest$hushDamageHearts(ServerLevel level, ParticleOptions particle, double x, double y, double z, int count, double xDist, double yDist, double zDist, double speed, Operation<Integer> original, @Local(argsOnly = true) Entity target) {
        if (HushedMaskBlockEntity.isHushed(target, HushedMaskBlockEntity.MuteType.PUFF)) return 0;

        return original.call(level, particle, x, y, z, count, xDist, yDist, zDist, speed);
    }
}
