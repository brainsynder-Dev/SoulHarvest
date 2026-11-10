package org.bsdevelopment.soul_harvest.listeners;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.bsdevelopment.soul_harvest.blocks.entity.GravityAnchorBlockEntity;
import org.bsdevelopment.soul_harvest.registry.ModTags;

public class GravityAnchorListener {
    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Mob mob) || mob.level().isClientSide()) return;

        boolean climber = mob instanceof Spider spider && spider.isClimbing() && mob.typeHolder().is(ModTags.CLIMB_INHIBITED);
        boolean flyer = mob.typeHolder().is(ModTags.FLIGHT_INHIBITED);

        if (!climber && !flyer) return;
        if (!GravityAnchorBlockEntity.isWithinActiveAnchor(mob.level(), mob.blockPosition())) return;

        if (climber) ((Spider) mob).setClimbing(false);
        if (flyer) pullToGround(mob);
    }

    @SubscribeEvent
    public void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level && !level.isClientSide()) GravityAnchorBlockEntity.clearLevel(level.dimension());
    }

    private void pullToGround(Mob mob) {
        Vec3 displacement = mob.position().subtract(mob.xOld, mob.yOld, mob.zOld);
        Vec3 motion = mob.getDeltaMovement();

        mob.setPos(mob.xOld, mob.yOld, mob.zOld);
        mob.noPhysics = false;
        mob.move(MoverType.SELF, new Vec3(displacement.x, Math.min(displacement.y, 0) - 0.2, displacement.z));
        mob.setDeltaMovement(motion.x, Math.min(motion.y, 0), motion.z);
        mob.resetFallDistance();
    }
}
