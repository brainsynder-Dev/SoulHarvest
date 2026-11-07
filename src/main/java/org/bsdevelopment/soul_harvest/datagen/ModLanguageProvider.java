package org.bsdevelopment.soul_harvest.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

import java.util.List;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, ModConstants.MOD_ID, locale);
    }

    public void addItem(Item item, String name, Translation... translations) {
        add(item, name);
        List.of(translations).forEach(translation -> add(item.getDescriptionId() + "." + translation.key(), translation.value()));
    }

    public void addBlock(Block block, String name, Translation... translations) {
        add(block, name);
        List.of(translations).forEach(translation -> add(block.getDescriptionId() + "." + translation.key(), translation.value()));
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + ModConstants.MOD_ID, "Soul Harvest");

        add("item.soul_harvest.soul_crystal", "Soul Crystal");
        add("item.soul_harvest.soul_steel_ingot", "Soul Steel Ingot");
        add("item.soul_harvest.soul_sawblade", "Soul Sawblade");
        add("item.soul_harvest.soul_link_wand", "Soul Link Wand");
        add("item.soul_harvest.soul_link_wand.linked", "Linked to altar at %s, %s, %s");
        add("item.soul_harvest.soul_link_wand.unlinked", "Right-click a Soul Harvest Altar to bind");

        add("block.soul_harvest.altar_bricks", "Altar Bricks");
        add("block.soul_harvest.soul_rune_bricks", "Soul Rune Bricks");
        add("block.soul_harvest.soul_harvest_altar", "Soul Harvest Altar");
        add("block.soul_harvest.soul_outlet", "Soul Outlet");

        add("message.soul_harvest.wand.bound", "Wand bound to the Soul Harvest Altar");
        add("message.soul_harvest.wand.linked", "Soul Outlet linked to the altar");
        add("message.soul_harvest.wand.unlinked", "Soul Outlet unlinked");
        add("message.soul_harvest.wand.cleared", "Wand cleared");
        add("message.soul_harvest.wand.no_altar", "Bind the wand to an altar first");
        add("message.soul_harvest.wand.too_far", "That outlet is too far from the altar");
    }

    private record Translation(String key, String value) {
    }
}
