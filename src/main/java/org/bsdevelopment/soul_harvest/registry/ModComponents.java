package org.bsdevelopment.soul_harvest.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.bsdevelopment.soul_harvest.component.FilterData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ModConstants.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> LINKED_ALTAR = register("linked_altar", GlobalPos.CODEC, GlobalPos.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> LINKED_DESTINATION = register("linked_destination", GlobalPos.CODEC, GlobalPos.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FilterData>> FILTER_DATA = register("filter_data", FilterData.CODEC, FilterData.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> FILTER_BLACKLIST = register("filter_blacklist", Unit.CODEC, StreamCodec.unit(Unit.INSTANCE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID_CONTENTS = register("fluid_contents", SimpleFluidContent.CODEC, SimpleFluidContent.STREAM_CODEC);

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }

    private static @NotNull <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, Codec<T> codec) {
        return register(name, codec, null);
    }

    private static @NotNull <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, Codec<T> codec, @Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        if (streamCodec == null) return COMPONENTS.register(name, () -> DataComponentType.<T>builder().persistent(codec).build());

        return COMPONENTS.register(name, () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
    }
}
