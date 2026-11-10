package org.bsdevelopment.soul_harvest.hooks.jade.server;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.bsdevelopment.soul_harvest.blocks.entity.SoulHarvestAltarBlockEntity;
import org.bsdevelopment.soul_harvest.config.ModConfig;
import org.bsdevelopment.soul_harvest.registry.ModConstants;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

import java.util.LinkedHashMap;
import java.util.Map;

public enum SoulHarvestAltarJadeDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof SoulHarvestAltarBlockEntity altar)) return;

        Map<Item, Long> itemTotals = new LinkedHashMap<>();
        altar.getBulkStorage().getItemMapCopy().forEach((itemRecord, count) -> itemTotals.merge(itemRecord.item(), count, Long::sum));

        int limit = Math.max(1, ModConfig.maxItemsPerLine) * 3;
        ListTag list = new ListTag();
        itemTotals.entrySet().stream().sorted(Map.Entry.<Item, Long>comparingByValue().reversed()).limit(limit)
                .forEach(entry -> {
                    CompoundTag entryTag = new CompoundTag();
                    BuiltInRegistries.ITEM.byNameCodec().encodeStart(NbtOps.INSTANCE, entry.getKey()).ifSuccess(tag -> entryTag.put("item", tag));
                    entryTag.putLong("count", entry.getValue());
                    list.add(entryTag);
                });
        data.put("items", list);
    }

    @Override
    public boolean shouldRequestData(BlockAccessor accessor) {
        return true;
    }

    @Override
    public Identifier getUid() {
        return Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "soul_harvest_altar");
    }
}
