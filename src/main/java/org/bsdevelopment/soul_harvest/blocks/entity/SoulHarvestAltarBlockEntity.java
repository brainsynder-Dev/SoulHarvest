package org.bsdevelopment.soul_harvest.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.Unit;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.AABB;
import org.bsdevelopment.soul_harvest.blocks.entity.base.IStorage;
import org.bsdevelopment.soul_harvest.blocks.entity.base.InventoryBlockEntity;
import org.bsdevelopment.soul_harvest.client.screen.widgets.Widgets;
import org.bsdevelopment.soul_harvest.config.ModConfig;
import org.bsdevelopment.soul_harvest.container.SlotPosition;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModItems;
import org.bsdevelopment.soul_harvest.utilities.Wraithfire;
import org.bsdevelopment.soul_harvest.utilities.fakeplayer.CustomFakePlayer;
import org.bsdevelopment.soul_harvest.utilities.storage.BulkStorage;

import java.util.*;

public class SoulHarvestAltarBlockEntity extends InventoryBlockEntity implements IStorage {
    public final Map<Integer, SlotPosition> SLOTS = new LinkedHashMap<>();
    private final Map<Item, Integer> runeCounts = new HashMap<>();
    private final Set<BlockPos> linkedExtractors = new LinkedHashSet<>();

    public float sawRotation;
    public float previousSawRotation;
    private BulkStorage bulkStorage = new BulkStorage();
    private CustomFakePlayer fakePlayer;
    private BlockPos linkedVesselPos;
    private ItemStack attackWeapon;
    private int storedExperience = 0;
    private int attackCooldown = 0;
    private int distributionTurn = 0;

    public SoulHarvestAltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.SOUL_HARVEST_ALTAR.getTileEntityType(), 8, pos, blockState);

        SLOTS.put(0, new SlotPosition(10, 20, Widgets.SMITE_RUNE_SLOT, ModItems.SMITE_SOUL_RUNE.get()));
        SLOTS.put(1, new SlotPosition(30, 20, Widgets.BANE_RUNE_SLOT, ModItems.BANE_SOUL_RUNE.get()));
        SLOTS.put(2, new SlotPosition(50, 20, Widgets.SHARPNESS_RUNE_SLOT, ModItems.SHARPNESS_SOUL_RUNE.get()));
        SLOTS.put(3, new SlotPosition(70, 20, Widgets.FLAME_RUNE_SLOT, ModItems.SOUL_FLAME_RUNE.get()));
        SLOTS.put(4, new SlotPosition(90, 20, Widgets.SPEED_RUNE_SLOT, ModItems.SPEED_SOUL_RUNE.get()));
        SLOTS.put(5, new SlotPosition(110, 20, Widgets.WISDOM_RUNE_SLOT, ModItems.WISDOM_SOUL_RUNE.get()));
        SLOTS.put(6, new SlotPosition(130, 20, Widgets.LOOTING_RUNE_SLOT, ModItems.LOOTING_SOUL_RUNE.get()));
        SLOTS.put(7, new SlotPosition(150, 20, Widgets.BEHEADING_RUNE_SLOT, ModItems.BEHEADING_SOUL_RUNE.get()));
    }

    public int getRuneCount(Item rune) {
        return runeCounts.getOrDefault(rune, 0);
    }

    public int getAttackInterval() {
        return Math.max(2, 20 - getRuneCount(ModItems.SPEED_SOUL_RUNE.get()) * 2);
    }

    public boolean isLinkedVessel(BlockPos vesselPos) {
        return vesselPos.equals(linkedVesselPos);
    }

    public void setLinkedVessel(BlockPos vesselPos) {
        linkedVesselPos = vesselPos;
        setChanged();
    }

    public void addLinkedExtractor(BlockPos extractorPos) {
        if (linkedExtractors.add(extractorPos)) setChanged();
    }

    public void removeLinkedExtractor(BlockPos extractorPos) {
        if (linkedExtractors.remove(extractorPos)) setChanged();
    }

    public void absorbItems(ServerLevel level) {
        if (!ModConfig.altarAbsorbItems || level.getGameTime() % 10 != 0) return;

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition).inflate(4), item -> item.isAlive() && !item.hasPickUpDelay());
        if (items.isEmpty()) return;

        items.forEach(item -> {
            bulkStorage.addItem(item.getItem());
            item.discard();
        });
        setChanged();
    }

    public void distributeDrops(ServerLevel level) {
        if (level.getGameTime() % 10 != 0 || linkedExtractors.isEmpty() || bulkStorage.getItemMapRaw().isEmpty()) return;

        List<LootExtractorBlockEntity> extractors = getOrderedExtractors(level);
        if (extractors.isEmpty()) return;

        boolean changed = false;
        Map<BulkStorage.ItemRecord, Long> items = bulkStorage.getItemMapRaw();
        for (Map.Entry<BulkStorage.ItemRecord, Long> entry : new ArrayList<>(items.entrySet())) {
            long remaining = entry.getValue();
            ItemStack template = entry.getKey().toStack(1);
            for (LootExtractorBlockEntity extractor : extractors) {
                if (remaining <= 0) break;
                if (!extractor.accepts(template)) continue;

                int offered = (int) Math.min(remaining, Integer.MAX_VALUE);
                remaining -= extractor.receiveItems(level, entry.getKey().toStack(offered));
            }
            if (remaining == entry.getValue()) continue;

            changed = true;
            if (remaining > 0) {
                items.put(entry.getKey(), remaining);
            } else {
                items.remove(entry.getKey());
            }
        }
        if (changed) setChanged();
    }

    public int getStoredExperience() {
        return storedExperience;
    }

    public void tickSaw(boolean powered) {
        previousSawRotation = sawRotation;
        if (!powered) return;

        sawRotation += Math.min(45f, 360f / getAttackInterval());
        if (sawRotation >= 360f) {
            sawRotation -= 360f;
            previousSawRotation -= 360f;
        }
    }

    public void tickAttack(ServerLevel level, boolean powered) {
        if (!powered) {
            attackCooldown = 0;
            return;
        }

        if (++attackCooldown < getAttackInterval()) return;
        attackCooldown = 0;

        AABB attackArea = new AABB(worldPosition.above()).inflate(1, 0, 1).expandTowards(0, 2, 0);
        List<Mob> targets = level.getEntitiesOfClass(Mob.class, attackArea, mob -> mob.isAlive() && mob.isAttackable());
        if (targets.isEmpty()) return;

        CustomFakePlayer attacker = getFakePlayer(level);
        attacker.setItemInHand(InteractionHand.MAIN_HAND, getAttackWeapon(level));
        boolean flameRune = getRuneCount(ModItems.SOUL_FLAME_RUNE.get()) > 0;
        for (Mob target : targets) {
            target.invulnerableTime = 0;
            attacker.attack(target);
            if (flameRune) Wraithfire.markBurning(target);
        }
        attacker.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    }

    public void collectDrops(LivingEntity entity, Collection<ItemEntity> drops) {
        drops.forEach(drop -> bulkStorage.addItem(drop.getItem()));

        float beheadingChance = getRuneCount(ModItems.BEHEADING_SOUL_RUNE.get()) * 0.05f;
        if (beheadingChance > 0 && entity.getRandom().nextFloat() < beheadingChance) bulkStorage.addItem(getHead(entity.getType()));
        setChanged();
    }

    public void collectExperience(int experience) {
        float multiplier = 1 + getRuneCount(ModItems.WISDOM_SOUL_RUNE.get()) * 0.25f;
        long total = storedExperience + (long) Math.round(experience * multiplier);
        storedExperience = (int) Math.min(Integer.MAX_VALUE, total);
        setChanged();
    }

    public int extractExperience(int maxExperience) {
        int extracted = Math.min(maxExperience, storedExperience);
        if (extracted <= 0) return 0;

        storedExperience -= extracted;
        setChanged();
        return extracted;
    }

    @Override
    public BulkStorage getBulkStorage() {
        return bulkStorage;
    }

    @Override
    public void saveStorage() {
        setChanged();
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        super.setItem(index, stack);
        onRunesChanged();
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack removed = super.removeItem(index, count);
        onRunesChanged();
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack removed = super.removeItemNoUpdate(index);
        onRunesChanged();
        return removed;
    }

    @Override
    public void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        storedExperience = tag.getIntOr("experience", 0);
        linkedVesselPos = tag.contains("linked_vessel") ? BlockPos.of(tag.getLongOr("linked_vessel", 0)) : null;
        linkedExtractors.clear();
        tag.getLongArray("linked_extractors").ifPresent(positions -> Arrays.stream(positions).forEach(position -> linkedExtractors.add(BlockPos.of(position))));
        bulkStorage = tag.get("storage") == null ? new BulkStorage() : BulkStorage.CODEC.codec().parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("storage")).result().orElseGet(BulkStorage::new);
    }

    @Override
    public void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("experience", storedExperience);
        if (linkedVesselPos != null) tag.putLong("linked_vessel", linkedVesselPos.asLong());
        tag.putLongArray("linked_extractors", linkedExtractors.stream().mapToLong(BlockPos::asLong).toArray());
        tag.put("storage", BulkStorage.CODEC.codec().encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), bulkStorage).getOrThrow());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        updateRunes();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        ContainerHelper.saveAllItems(output, getItems());
        return output.buildResult();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        fakePlayer = null;
    }

    private List<LootExtractorBlockEntity> getOrderedExtractors(ServerLevel level) {
        Map<Integer, List<LootExtractorBlockEntity>> extractorsByPriority = new TreeMap<>(Comparator.reverseOrder());
        Iterator<BlockPos> iterator = linkedExtractors.iterator();
        while (iterator.hasNext()) {
            BlockPos extractorPos = iterator.next();
            if (!level.isLoaded(extractorPos)) continue;
            if (!(level.getBlockEntity(extractorPos) instanceof LootExtractorBlockEntity extractor) || !worldPosition.equals(extractor.getAltarPos())) {
                iterator.remove();
                setChanged();
                continue;
            }

            if (extractor.isEnabled()) extractorsByPriority.computeIfAbsent(extractor.getPriority(), priority -> new ArrayList<>()).add(extractor);
        }

        distributionTurn++;
        List<LootExtractorBlockEntity> orderedExtractors = new ArrayList<>();
        extractorsByPriority.values().forEach(group -> {
            Collections.rotate(group, distributionTurn % group.size());
            orderedExtractors.addAll(group);
        });
        return orderedExtractors;
    }

    private void onRunesChanged() {
        updateRunes();

        if (level != null && !level.isClientSide()) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    private void updateRunes() {
        runeCounts.clear();
        for (int slot = 0; slot < getContainerSize(); slot++) {
            ItemStack stack = getItem(slot);
            if (!stack.isEmpty()) runeCounts.merge(stack.getItem(), Math.min(stack.getCount(), ModConfig.getRuneMaxStack(stack.getItem())), Integer::sum);
        }
        attackWeapon = null;
    }

    private CustomFakePlayer getFakePlayer(ServerLevel level) {
        if (fakePlayer == null) {
            fakePlayer = new CustomFakePlayer(level, worldPosition);
            fakePlayer.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(1024);
            fakePlayer.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(20);
            fakePlayer.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5);
        }
        return fakePlayer;
    }

    private ItemStack getAttackWeapon(ServerLevel level) {
        if (attackWeapon != null) return attackWeapon;

        attackWeapon = new ItemStack(Items.NETHERITE_SWORD);
        attackWeapon.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        enchantWeapon(level, ModItems.SMITE_SOUL_RUNE.get(), Enchantments.SMITE);
        enchantWeapon(level, ModItems.BANE_SOUL_RUNE.get(), Enchantments.BANE_OF_ARTHROPODS);
        enchantWeapon(level, ModItems.SHARPNESS_SOUL_RUNE.get(), Enchantments.SHARPNESS);
        enchantWeapon(level, ModItems.SOUL_FLAME_RUNE.get(), Enchantments.FIRE_ASPECT);
        enchantWeapon(level, ModItems.LOOTING_SOUL_RUNE.get(), Enchantments.LOOTING);
        return attackWeapon;
    }

    private void enchantWeapon(ServerLevel level, Item rune, ResourceKey<Enchantment> enchantment) {
        int runeCount = getRuneCount(rune);
        if (runeCount <= 0) return;

        attackWeapon.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment), runeCount);
    }

    private ItemStack getHead(EntityType<?> type) {
        if (type == EntityType.ZOMBIE) return new ItemStack(Items.ZOMBIE_HEAD);
        if (type == EntityType.SKELETON) return new ItemStack(Items.SKELETON_SKULL);
        if (type == EntityType.WITHER_SKELETON) return new ItemStack(Items.WITHER_SKELETON_SKULL);
        if (type == EntityType.CREEPER) return new ItemStack(Items.CREEPER_HEAD);
        if (type == EntityType.PIGLIN) return new ItemStack(Items.PIGLIN_HEAD);
        return ItemStack.EMPTY;
    }
}
