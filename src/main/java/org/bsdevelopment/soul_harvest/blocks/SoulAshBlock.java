package org.bsdevelopment.soul_harvest.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;
import org.bsdevelopment.soul_harvest.config.MobSpawnConfig;
import org.bsdevelopment.soul_harvest.config.ModConfig;
import org.bsdevelopment.soul_harvest.registry.ModConstants;
import org.bsdevelopment.soul_harvest.registry.ModTags;
import org.bsdevelopment.soul_harvest.utilities.WeightedRandomSelector;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SoulAshBlock extends Block {
    public static final int[] SIGIL_SIZES = {1, 3, 5, 7, 9};
    public static final IntegerProperty SIGIL = IntegerProperty.create("sigil", 0, 165);

    private final Map<GlobalPos, Long> nextUpdateSpawnTimes = new HashMap<>();

    public SoulAshBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(SIGIL, 0));
    }

    public static boolean onlyHostiles(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entityType) {
        return entityType.getCategory() == MobCategory.MONSTER;
    }

    public static int getSigilSize(int sigilIndex) {
        int offset = 0;
        for (int size : SIGIL_SIZES) {
            offset += size * size;
            if (sigilIndex <= offset) return size;
        }
        return 0;
    }

    public static int getSigilOffset(int size) {
        int offset = 0;
        for (int sigilSize : SIGIL_SIZES) {
            if (sigilSize == size) return offset;
            offset += sigilSize * sigilSize;
        }
        return offset;
    }

    public static int getCrystalCost(int size) {
        return Math.max(1, (size * size + 9) / 11);
    }

    public int findPadSize(Level level, BlockPos center) {
        for (int index = SIGIL_SIZES.length - 1; index >= 0; index--) {
            if (isCompletePad(level, center, SIGIL_SIZES[index])) return SIGIL_SIZES[index];
        }
        return 0;
    }

    public void awaken(ServerLevel level, BlockPos center, int size) {
        int radius = size / 2;
        int offset = getSigilOffset(size);
        for (BlockPos piece : BlockPos.betweenClosed(center.offset(-radius, 0, -radius), center.offset(radius, 0, radius))) {
            int sigilIndex = offset + (piece.getZ() - center.getZ() + radius) * size + (piece.getX() - center.getX() + radius) + 1;
            level.setBlock(piece, defaultBlockState().setValue(SIGIL, sigilIndex), Block.UPDATE_CLIENTS);
        }
        level.scheduleTick(center, this, getSpawnInterval(size));
    }

    public void startGrowth(ServerLevel level, BlockPos center) {
        level.setBlockAndUpdate(center, defaultBlockState());
        level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, center, Block.getId(defaultBlockState()));
        level.playSound(null, center, SoundEvents.SOUL_ESCAPE.value(), SoundSource.BLOCKS, 1f, 0.6f);
        level.scheduleTick(center, this, 20);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIGIL);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        int sigilIndex = state.getValue(SIGIL);
        if (sigilIndex == 0 || !ModConfig.soulAshRedstoneUpdates || !(level instanceof ServerLevel serverLevel)) return;

        GlobalPos center = GlobalPos.of(serverLevel.dimension(), getSigilCenter(pos, sigilIndex));
        long gameTime = serverLevel.getGameTime();
        Long nextSpawnTime = nextUpdateSpawnTimes.get(center);
        if (nextSpawnTime != null && gameTime < nextSpawnTime) return;

        nextUpdateSpawnTimes.put(center, gameTime + Mth.randomBetweenInclusive(serverLevel.getRandom(), 4, 8));
        spawnMonster(serverLevel, center.pos(), getSigilSize(sigilIndex), serverLevel.getRandom());
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        int sigilIndex = state.getValue(SIGIL);
        if (sigilIndex == 0) return;

        BlockPos center = getSigilCenter(pos, sigilIndex);
        int radius = getSigilSize(sigilIndex) / 2;
        nextUpdateSpawnTimes.remove(GlobalPos.of(level.dimension(), center));
        for (BlockPos piece : BlockPos.betweenClosed(center.offset(-radius, 0, -radius), center.offset(radius, 0, radius))) {
            BlockState pieceState = level.getBlockState(piece);
            if (pieceState.is(this) && pieceState.getValue(SIGIL) != 0) level.setBlock(piece, pieceState.setValue(SIGIL, 0), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int sigilIndex = state.getValue(SIGIL);
        if (sigilIndex == 0) {
            growNextRing(level, pos);
            return;
        }

        if (getSigilCenter(pos, sigilIndex).equals(pos)) spawnMonster(level, pos, getSigilSize(sigilIndex), random);
    }

    private boolean isCompletePad(Level level, BlockPos center, int size) {
        int radius = size / 2;
        for (BlockPos piece : BlockPos.betweenClosed(center.offset(-radius, 0, -radius), center.offset(radius, 0, radius))) {
            BlockState pieceState = level.getBlockState(piece);
            if (!pieceState.is(this) || pieceState.getValue(SIGIL) != 0 || level.getBlockState(piece.above()).isSolidRender())
                return false;
        }
        return true;
    }

    private BlockPos getSigilCenter(BlockPos pos, int sigilIndex) {
        int size = getSigilSize(sigilIndex);
        int piece = sigilIndex - getSigilOffset(size) - 1;
        return pos.offset(size / 2 - piece % size, 0, size / 2 - piece / size);
    }

    private int getSpawnInterval(int size) {
        return Math.max(20, ModConfig.soulAshSpawnInterval * 25 / (size * size));
    }

    private int getMaxEntities(int size) {
        return Math.max(1, ModConfig.soulAshMaxEntities * size * size / 25);
    }

    private void growNextRing(ServerLevel level, BlockPos center) {
        for (int ring = 1; ring <= 2; ring++) {
            boolean grew = false;
            for (BlockPos target : BlockPos.betweenClosed(center.offset(-ring, 0, -ring), center.offset(ring, 0, ring))) {
                if (Math.max(Math.abs(target.getX() - center.getX()), Math.abs(target.getZ() - center.getZ())) != ring)
                    continue;
                if (!level.getBlockState(target).is(ModTags.SOUL_ASH_CONVERTIBLE)) continue;

                level.setBlockAndUpdate(target, defaultBlockState());
                level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, target, Block.getId(defaultBlockState()));
                grew = true;
            }
            if (!grew) continue;

            level.playSound(null, center, SoundEvents.SOUL_ESCAPE.value(), SoundSource.BLOCKS, 1f, 0.6f + ring * 0.2f);
            if (ring < 2) level.scheduleTick(center, this, 20);
            return;
        }
    }

    private SpawnSelection selectSpawn(ServerLevel level, BlockPos center, RandomSource random) {
        if (ModConfig.soulAshBiomeSpawns) {
            Optional<MobSpawnSettings.SpawnerData> biomeSpawn = level.getBiome(center).value().getMobSettings().getMobs(MobCategory.MONSTER).getRandom(random);
            if (biomeSpawn.isPresent()) {
                MobSpawnSettings.SpawnerData spawnerData = biomeSpawn.get();
                return new SpawnSelection(spawnerData.type(), Mth.randomBetweenInclusive(random, spawnerData.minCount(), spawnerData.maxCount()));
            }
        }

        MobSpawnConfig.SpawnEntry entry = WeightedRandomSelector.selectRandomEntry(ModConstants.SOUL_ASH_SPAWNS.getValidEntries(level, center), random);
        if (entry == null) return null;

        Optional<EntityType<?>> type = BuiltInRegistries.ENTITY_TYPE.getOptional(entry.getMobResource());
        return type.map(entityType -> new SpawnSelection(entityType, WeightedRandomSelector.getRandomSpawnCount(entry, random))).orElse(null);
    }

    private void spawnMonster(ServerLevel level, BlockPos center, int size, RandomSource random) {
        level.scheduleTick(center, this, getSpawnInterval(size));
        if (level.getDifficulty() == Difficulty.PEACEFUL) return;
        if (!level.hasNearbyAlivePlayer(center.getX() + 0.5, center.getY(), center.getZ() + 0.5, 32)) return;

        int radius = size / 2;
        int maxEntities = getMaxEntities(size);
        AABB padArea = new AABB(center).inflate(radius, 0, radius).expandTowards(0, 3, 0);
        int nearbyMonsters = level.getEntitiesOfClass(Monster.class, padArea).size();
        if (nearbyMonsters >= maxEntities) return;

        int remainingRoom = maxEntities - nearbyMonsters;
        while (remainingRoom > 0) {
            SpawnSelection selection = selectSpawn(level, center, random);
            if (selection == null) return;

            int groupSize = Math.max(1, Math.min(selection.count(), remainingRoom));
            remainingRoom -= groupSize;
            for (int attempt = 0; attempt < groupSize; attempt++) {
                BlockPos spawnPos = center.offset(random.nextInt(size) - radius, 1, random.nextInt(size) - radius);
                if (level.getMaxLocalRawBrightness(spawnPos) > 0 || !SpawnPlacements.isSpawnPositionOk(selection.type(), level, spawnPos)) continue;
                if (selection.type().spawn(level, spawnPos, EntitySpawnReason.SPAWNER) == null) continue;

                level.sendParticles(ParticleTypes.SOUL, spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5, 12, 0.3, 0.5, 0.3, 0.02);
            }
        }
    }

    private record SpawnSelection(EntityType<?> type, int count) {}
}
