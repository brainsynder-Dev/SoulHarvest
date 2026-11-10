package org.bsdevelopment.soul_harvest.utilities.fakeplayer;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public class CustomFakePlayer extends FakePlayer {
    private final BlockPos ownerPos;

    public CustomFakePlayer(ServerLevel level, BlockPos ownerPos) {
        super(level, ModConstants.FAKE_PROFILE);
        this.ownerPos = ownerPos;
    }

    public BlockPos getOwnerPos() {
        return ownerPos;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return false;
    }
}
