package org.bsdevelopment.soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.bsdevelopment.soul_harvest.fluids.LiquidExperienceFluid;
import org.bsdevelopment.soul_harvest.fluids.LiquidExperienceFluidType;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, ModConstants.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, ModConstants.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> LIQUID_EXPERIENCE_TYPE = FLUID_TYPES.register("liquid_experience", () -> new LiquidExperienceFluidType(FluidType.Properties.create()
            .fallDistanceModifier(0f)
            .canExtinguish(true)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY))
    );

    public static final DeferredHolder<Fluid, LiquidExperienceFluid.Source> LIQUID_EXPERIENCE = FLUIDS.register("liquid_experience", () -> new LiquidExperienceFluid.Source(experienceProperties()));
    public static final DeferredHolder<Fluid, LiquidExperienceFluid.Flowing> FLOWING_LIQUID_EXPERIENCE = FLUIDS.register("flowing_liquid_experience", () -> new LiquidExperienceFluid.Flowing(experienceProperties()));

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }

    private static BaseFlowingFluid.Properties experienceProperties() {
        return new BaseFlowingFluid.Properties(LIQUID_EXPERIENCE_TYPE, LIQUID_EXPERIENCE, FLOWING_LIQUID_EXPERIENCE).bucket(ModItems.LIQUID_EXPERIENCE_BUCKET).block(ModBlocks.LIQUID_EXPERIENCE);
    }
}
