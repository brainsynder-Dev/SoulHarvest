package org.bsdevelopment.soul_harvest.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.bsdevelopment.soul_harvest.blocks.WatcherBricksBlock;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;

public class WatcherBricksBlockEntity extends BlockEntity implements GameEventListener.Provider<WatcherBricksBlockEntity.DeathListener> {
    private final DeathListener deathListener;

    public WatcherBricksBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.WATCHER_BRICKS.getTileEntityType(), pos, blockState);
        deathListener = new DeathListener(new BlockPositionSource(pos));
    }

    @Override
    public DeathListener getListener() {
        return deathListener;
    }

    public class DeathListener implements GameEventListener {
        private final PositionSource positionSource;

        public DeathListener(PositionSource positionSource) {
            this.positionSource = positionSource;
        }

        @Override
        public PositionSource getListenerSource() {
            return positionSource;
        }

        @Override
        public int getListenerRadius() {
            return 8;
        }

        @Override
        public boolean handleGameEvent(ServerLevel level, Holder<GameEvent> gameEvent, GameEvent.Context context, Vec3 sourcePos) {
            if (!gameEvent.is(GameEvent.ENTITY_DIE) || !(context.sourceEntity() instanceof Mob)) return false;

            BlockState state = getBlockState();
            if (!(state.getBlock() instanceof WatcherBricksBlock watcherBricks)) return false;

            watcherBricks.witnessDeath(level, worldPosition, state);
            return true;
        }
    }
}
