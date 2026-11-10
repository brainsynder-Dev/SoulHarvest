package org.bsdevelopment.soul_harvest.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.StacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import org.bsdevelopment.soul_harvest.blocks.entity.base.ILinkable;
import org.bsdevelopment.soul_harvest.blocks.entity.base.InventoryBlockEntity;
import org.bsdevelopment.soul_harvest.config.ModConfig;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModComponents;
import org.bsdevelopment.soul_harvest.registry.ModFluids;
import org.bsdevelopment.soul_harvest.registry.ModItems;
import org.bsdevelopment.soul_harvest.utilities.ModUtilities;

public class WisdomVesselBlockEntity extends InventoryBlockEntity implements ILinkable {
    public static final int MB_PER_EXPERIENCE = 20;
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int TRANSFER_TIME = 20;
    private final FluidStacksResourceHandler tank = new FluidStacksResourceHandler(1, getCapacity()) {
        @Override
        protected int getCapacity(int index, FluidResource resource) {
            return WisdomVesselBlockEntity.getCapacity();
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            return resource.is(ModFluids.LIQUID_EXPERIENCE_TYPE.get());
        }

        @Override
        protected void onContentsChanged(int index, FluidStack previousContents) {
            setChanged();
            if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    };
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return transferProgress;
        }

        @Override
        public void set(int index, int value) {
            transferProgress = value;
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    private BlockPos altarPos = null;
    private int transferProgress = 0;

    public WisdomVesselBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.WISDOM_VESSEL.getTileEntityType(), 2, pos, blockState);
    }

    public static int getCapacity() {
        return ModUtilities.getExperienceForLevel(ModConfig.wisdomVesselMaxLevel) * MB_PER_EXPERIENCE;
    }

    public static boolean isBucket(ItemStack stack) {
        return stack.is(Items.BUCKET) || stack.is(ModItems.LIQUID_EXPERIENCE_BUCKET.get());
    }

    public FluidStacksResourceHandler getTank() {
        return tank;
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public float getFillLevel() {
        return Math.min(1f, (float) getStoredAmount() / getCapacity());
    }

    public int getStoredExperience() {
        return getStoredAmount() / MB_PER_EXPERIENCE;
    }

    public int getFreeExperience() {
        return Math.max(0, getCapacity() - getStoredAmount()) / MB_PER_EXPERIENCE;
    }

    public void storeExperience(Player player, int experience) {
        int stored = Math.min(experience, getFreeExperience());
        if (stored <= 0) return;

        player.giveExperiencePoints(-stored);
        setStoredAmount(getStoredAmount() + stored * MB_PER_EXPERIENCE);
    }

    public void takeExperience(Player player, int experience) {
        int taken = Math.min(experience, getStoredExperience());
        if (taken <= 0) return;

        setStoredAmount(getStoredAmount() - taken * MB_PER_EXPERIENCE);
        player.giveExperiencePoints(taken);
    }

    public void drawFromAltar(ServerLevel level) {
        if (altarPos == null || level.getGameTime() % 20 != 0 || !level.isLoaded(altarPos)) return;
        if (!(level.getBlockEntity(altarPos) instanceof SoulHarvestAltarBlockEntity altar) || !altar.isLinkedVessel(worldPosition)) {
            linkPosition(null);
            return;
        }

        int drawn = altar.extractExperience(getFreeExperience());
        if (drawn > 0) setStoredAmount(getStoredAmount() + drawn * MB_PER_EXPERIENCE);
    }

    public void drainIntoVesselsBelow(ServerLevel level) {
        if (level.getGameTime() % 20 != 0 || getStoredAmount() <= 0) return;

        WisdomVesselBlockEntity targetVessel = null;
        BlockPos belowPos = worldPosition.below();
        while (level.getBlockEntity(belowPos) instanceof WisdomVesselBlockEntity vesselBelow && vesselBelow.getStoredAmount() < getCapacity()) {
            targetVessel = vesselBelow;
            belowPos = belowPos.below();
        }
        if (targetVessel == null) return;

        int drained = Math.min(getStoredAmount(), getCapacity() - targetVessel.getStoredAmount());
        targetVessel.setStoredAmount(targetVessel.getStoredAmount() + drained);
        setStoredAmount(getStoredAmount() - drained);
    }

    public void processBuckets() {
        if (!canProcessInput()) {
            transferProgress = 0;
            return;
        }

        if (++transferProgress < TRANSFER_TIME) return;
        transferProgress = 0;

        ItemStack input = getItem(INPUT_SLOT);
        ItemStack output = getItem(OUTPUT_SLOT);
        if (input.is(Items.BUCKET)) {
            setStoredAmount(getStoredAmount() - FluidType.BUCKET_VOLUME);
            setItem(OUTPUT_SLOT, new ItemStack(ModItems.LIQUID_EXPERIENCE_BUCKET.get()));
        } else {
            setStoredAmount(getStoredAmount() + FluidType.BUCKET_VOLUME);
            if (output.isEmpty()) {
                setItem(OUTPUT_SLOT, new ItemStack(Items.BUCKET));
            } else {
                output.grow(1);
                setChanged();
            }
        }
        removeItem(INPUT_SLOT, 1);
    }

    @Override
    public void linkPosition(BlockPos pos) {
        BlockPos previousAltarPos = altarPos;
        altarPos = pos;
        setChanged();
        if (level == null) return;

        if (previousAltarPos != null
                && level.getBlockEntity(previousAltarPos) instanceof SoulHarvestAltarBlockEntity previousAltar
                && previousAltar.isLinkedVessel(worldPosition)) previousAltar.setLinkedVessel(null);
        if (pos != null && level.getBlockEntity(pos) instanceof SoulHarvestAltarBlockEntity altar) altar.setLinkedVessel(worldPosition);
    }

    @Override
    public boolean isLinked() {
        return altarPos != null;
    }

    @Override
    public void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        if (!tag.contains("bound_altar")) return;

        CompoundTag compound = tag.getCompoundOrEmpty("bound_altar");
        altarPos = new BlockPos(compound.getIntOr("x", 0), compound.getIntOr("y", 0), compound.getIntOr("z", 0));
    }

    @Override
    public void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        if (altarPos == null) return;

        CompoundTag compound = new CompoundTag();
        compound.putInt("x", altarPos.getX());
        compound.putInt("y", altarPos.getY());
        compound.putInt("z", altarPos.getZ());
        tag.put("bound_altar", compound);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        SimpleFluidContent contents = components.getOrDefault(ModComponents.FLUID_CONTENTS.get(), SimpleFluidContent.EMPTY);
        if (!contents.isEmpty()) tank.set(0, FluidResource.of(contents.copy()), contents.getAmount());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        FluidStack contents = tank.copyToList().getFirst();
        if (!contents.isEmpty()) components.set(ModComponents.FLUID_CONTENTS.get(), SimpleFluidContent.copyOf(contents));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        tank.deserialize(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        tank.serialize(output);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        output.discard(StacksResourceHandler.VALUE_IO_KEY);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    private boolean canProcessInput() {
        ItemStack input = getItem(INPUT_SLOT);
        ItemStack output = getItem(OUTPUT_SLOT);

        if (input.is(Items.BUCKET)) return getStoredAmount() >= FluidType.BUCKET_VOLUME && output.isEmpty();
        if (input.is(ModItems.LIQUID_EXPERIENCE_BUCKET.get())) {
            boolean outputFits = output.isEmpty() || (output.is(Items.BUCKET) && output.getCount() < output.getMaxStackSize());
            return getCapacity() - getStoredAmount() >= FluidType.BUCKET_VOLUME && outputFits;
        }
        return false;
    }

    private int getStoredAmount() {
        return (int) tank.getAmountAsLong(0);
    }

    private void setStoredAmount(int amount) {
        if (amount <= 0) {
            tank.set(0, FluidResource.EMPTY, 0);
            return;
        }

        tank.set(0, FluidResource.of(ModFluids.LIQUID_EXPERIENCE.get()), amount);
    }
}
