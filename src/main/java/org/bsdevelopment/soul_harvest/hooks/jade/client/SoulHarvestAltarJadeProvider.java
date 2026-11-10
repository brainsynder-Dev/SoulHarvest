package org.bsdevelopment.soul_harvest.hooks.jade.client;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.bsdevelopment.soul_harvest.config.ModConfig;
import org.bsdevelopment.soul_harvest.registry.ModConstants;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.JadeUI;

public enum SoulHarvestAltarJadeProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        ListTag list = data.getListOrEmpty("items");
        if (list.isEmpty()) return;

        int itemsPerLine = Math.max(1, ModConfig.maxItemsPerLine);
        for (int index = 0; index < list.size(); index++) {
            CompoundTag entry = list.getCompound(index).orElse(null);
            if (entry == null) continue;

            Item item = BuiltInRegistries.ITEM.byNameCodec().parse(NbtOps.INSTANCE, entry.get("item")).result().orElse(null);
            if (item == null) continue;

            Element element = JadeUI.item(item.getDefaultInstance(), 1.0f, String.valueOf(entry.getLongOr("count", 0)));
            if (index % itemsPerLine == 0) {
                tooltip.add(element);
            } else {
                tooltip.append(element);
            }
        }
    }

    @Override
    public Identifier getUid() {
        return Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "soul_harvest_altar");
    }
}
