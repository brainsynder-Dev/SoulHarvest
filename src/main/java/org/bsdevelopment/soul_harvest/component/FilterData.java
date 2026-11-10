package org.bsdevelopment.soul_harvest.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import org.bsdevelopment.soul_harvest.utilities.ItemFilterPredicates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record FilterData(List<Entry> entries, boolean matchComponents, boolean ignoreDamage) {
    public static final int SIZE = 9;
    public static final FilterData EMPTY = new FilterData(Collections.nCopies(SIZE, Entry.EMPTY), false, false);
    public static final Codec<FilterData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Entry.CODEC.listOf().fieldOf("entries").forGetter(FilterData::entries),
            Codec.BOOL.optionalFieldOf("match_components", false).forGetter(FilterData::matchComponents),
            Codec.BOOL.optionalFieldOf("ignore_damage", false).forGetter(FilterData::ignoreDamage)
    ).apply(instance, FilterData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FilterData> STREAM_CODEC = StreamCodec.composite(
            Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), FilterData::entries,
            ByteBufCodecs.BOOL, FilterData::matchComponents,
            ByteBufCodecs.BOOL, FilterData::ignoreDamage,
            FilterData::new
    );

    public Entry entry(int index) {
        return index < entries.size() ? entries.get(index) : Entry.EMPTY;
    }

    public FilterData withEntry(int index, Entry entry) {
        List<Entry> updated = new ArrayList<>(entries);
        while (updated.size() < SIZE) updated.add(Entry.EMPTY);
        updated.set(index, entry);
        return new FilterData(List.copyOf(updated), matchComponents, ignoreDamage);
    }

    public FilterData withMatchComponents(boolean value) {
        return new FilterData(entries, value, ignoreDamage);
    }

    public FilterData withIgnoreDamage(boolean value) {
        return new FilterData(entries, matchComponents, value);
    }

    public boolean lists(ItemStack candidate, Level level) {
        for (Entry entry : entries) {
            if (entry.matches(candidate, matchComponents, ignoreDamage, level)) return true;
        }
        return false;
    }

    public record Entry(Optional<ItemStackTemplate> item, Optional<TagKey<Item>> tag, String data) {
        public static final Entry EMPTY = new Entry(Optional.empty(), Optional.empty(), "");
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStackTemplate.CODEC.optionalFieldOf("item").forGetter(Entry::item),
                TagKey.codec(Registries.ITEM).optionalFieldOf("tag").forGetter(Entry::tag),
                Codec.STRING.optionalFieldOf("data", "").forGetter(Entry::data)
        ).apply(instance, Entry::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC), Entry::item,
                ByteBufCodecs.optional(TagKey.streamCodec(Registries.ITEM)), Entry::tag,
                ByteBufCodecs.STRING_UTF8, Entry::data,
                Entry::new
        );

        public boolean isEmpty() {
            return item.isEmpty();
        }

        public ItemStack stack() {
            return item.map(ItemStackTemplate::create).orElse(ItemStack.EMPTY);
        }

        public boolean matches(ItemStack candidate, boolean matchComponents, boolean ignoreDamage, Level level) {
            if (item.isEmpty()) return false;
            if (!matchesItem(candidate, matchComponents, ignoreDamage)) return false;
            return data.isEmpty() || ItemFilterPredicates.test(data, candidate, level);
        }

        private boolean matchesItem(ItemStack candidate, boolean matchComponents, boolean ignoreDamage) {
            if (tag.isPresent()) return candidate.typeHolder().is(tag.get());

            ItemStack filterStack = stack();
            if (!matchComponents) return ItemStack.isSameItem(filterStack, candidate);
            if (!ignoreDamage) return ItemStack.isSameItemSameComponents(filterStack, candidate);

            ItemStack filterCopy = filterStack.copy();
            ItemStack candidateCopy = candidate.copy();
            filterCopy.remove(DataComponents.DAMAGE);
            candidateCopy.remove(DataComponents.DAMAGE);
            return ItemStack.isSameItemSameComponents(filterCopy, candidateCopy);
        }
    }
}
