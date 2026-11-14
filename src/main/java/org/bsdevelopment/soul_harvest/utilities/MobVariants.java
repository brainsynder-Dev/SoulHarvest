package org.bsdevelopment.soul_harvest.utilities;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Variant;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.DyeColor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MobVariants {
    public static void randomize(ServerLevel level, Entity mob, List<String> variants, RandomSource random) {
        switch (mob) {
            case Pig pig -> pickRegistryVariant(level, Registries.PIG_VARIANT, variants, random).ifPresent(variant -> pig.setComponent(DataComponents.PIG_VARIANT, variant));
            case Cow cow -> pickRegistryVariant(level, Registries.COW_VARIANT, variants, random).ifPresent(variant -> cow.setComponent(DataComponents.COW_VARIANT, variant));
            case Chicken chicken -> pickRegistryVariant(level, Registries.CHICKEN_VARIANT, variants, random).ifPresent(variant -> chicken.setComponent(DataComponents.CHICKEN_VARIANT, variant));
            case Rabbit rabbit -> pickVariant(List.of(Rabbit.Variant.BROWN, Rabbit.Variant.WHITE, Rabbit.Variant.BLACK, Rabbit.Variant.WHITE_SPLOTCHED, Rabbit.Variant.GOLD, Rabbit.Variant.SALT), variants, random).ifPresent(variant -> rabbit.setComponent(DataComponents.RABBIT_VARIANT, variant));
            case Horse horse when !variants.isEmpty() -> pickVariant(List.of(Variant.values()), variants, random).ifPresent(variant -> horse.setComponent(DataComponents.HORSE_VARIANT, variant));
            case Sheep sheep when !variants.isEmpty() -> pickVariant(List.of(DyeColor.values()), variants, random).ifPresent(color -> sheep.setComponent(DataComponents.SHEEP_COLOR, color));
            default -> {}
        }
    }

    private static <T> Optional<Holder.Reference<T>> pickRegistryVariant(ServerLevel level, ResourceKey<Registry<T>> registryKey, List<String> variants, RandomSource random) {
        Registry<T> registry = level.registryAccess().lookupOrThrow(registryKey);
        if (variants.isEmpty()) return registry.getRandom(random);

        return Util.getRandomSafe(variants.stream().map(Identifier::tryParse).filter(Objects::nonNull).flatMap(id -> registry.get(id).stream()).toList(), random);
    }

    private static <T extends StringRepresentable> Optional<T> pickVariant(List<T> values, List<String> variants, RandomSource random) {
        if (variants.isEmpty()) return Util.getRandomSafe(values, random);

        return Util.getRandomSafe(values.stream().filter(value -> variants.contains(value.getSerializedName())).toList(), random);
    }
}
