package org.bsdevelopment.soul_harvest.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.bsdevelopment.soul_harvest.registry.ModBlocks;
import org.bsdevelopment.soul_harvest.registry.ModConstants;
import org.bsdevelopment.soul_harvest.registry.ModFluids;
import org.bsdevelopment.soul_harvest.registry.ModItems;

import java.util.List;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, ModConstants.MOD_ID, locale);
    }

    public void addItem(Item item, String name, Translation... translations) {
        add(item, name);
        addTranslations(item.getDescriptionId(), translations);
    }

    public void addBlock(Block block, String name, Translation... translations) {
        add(block, name);
        addTranslations(block.getDescriptionId(), translations);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + ModConstants.MOD_ID, "Soul Harvest");
        add("config.jade.plugin_" + ModConstants.MOD_ID + ".soul_harvest_altar", "Soul Harvest Altar");

        addItem(ModItems.SOUL_CRYSTAL.get(), "Soul Crystal");
        addItem(ModItems.SOUL_STEEL_INGOT.get(), "Soul Steel Ingot");
        addItem(ModItems.SOUL_SAWBLADE.get(), "Soul Sawblade");
        addItem(ModItems.SOUL_PROBE.get(), "Soul Probe", new Translation("description", "This item is in development and not used yet"));
        addItem(ModItems.SOUL_ASH_SEED.get(), "Soul Ash Seed", new Translation("description", "Use on soil to grow a 5x5 of Soul Ash"));
        addItem(ModItems.SOUL_BLOOM_SEED.get(), "Soul Bloom Seed", new Translation("description", "Use on soil to grow a 5x5 of Soul Bloom"));
        addItem(ModItems.SOUL_WAND.get(), "Soul Wand",
                new Translation("linked", "Linked to altar at %s, %s, %s"),
                new Translation("linked_destination", "Linked to destination pad at %s, %s, %s"),
                new Translation("unlinked", "Right-click a Soul Harvest Altar or Destination Pad to bind")
        );

        addItem(ModItems.BLANK_SOUL_RUNE.get(), "Blank Soul Rune", new Translation("description", "An empty soul rune. Craft it into an altar upgrade"));
        addItem(ModItems.LOOTING_SOUL_RUNE.get(), "Looting Soul Rune", new Translation("description", "Increases the amount of loot per kill"));
        addItem(ModItems.SMITE_SOUL_RUNE.get(), "Smite Soul Rune", new Translation("description", "Deals extra damage to undead mobs"));
        addItem(ModItems.SHARPNESS_SOUL_RUNE.get(), "Sharpness Soul Rune", new Translation("description", "Deals extra damage to mobs"));
        addItem(ModItems.SOUL_FLAME_RUNE.get(), "Soul Flame Rune", new Translation("description", "Burns mobs with soul fire"));
        addItem(ModItems.BEHEADING_SOUL_RUNE.get(), "Beheading Soul Rune", new Translation("description", "Gives mobs a chance to drop their heads"));
        addItem(ModItems.BANE_SOUL_RUNE.get(), "Bane Soul Rune", new Translation("description", "Deals extra damage to spiders and other arthropods"));
        addItem(ModItems.SPEED_SOUL_RUNE.get(), "Speed Soul Rune", new Translation("description", "Attacks and kills mobs faster"));
        addItem(ModItems.WISDOM_SOUL_RUNE.get(), "Wisdom Soul Rune", new Translation("description", "Slain mobs will drop more experience"));

        addBlock(ModBlocks.ALTAR_BRICKS.getBlock(), "Altar Bricks");
        addBlock(ModBlocks.SOUL_ALTAR_BRICKS.getBlock(), "Soul Altar Bricks");
        addBlock(ModBlocks.SOUL_HARVEST_ALTAR.getBlock(), "Soul Harvest Altar");
        addBlock(ModBlocks.LOOT_EXTRACTOR.getBlock(), "Loot Extractor",
                new Translation("description.1", "Collects the drops from the Soul Harvest Altar."),
                new Translation("description.2", "Exports it all into the storage container it's placed on.")
        );
        addBlock(ModBlocks.WISDOM_VESSEL.getBlock(), "Wisdom Vessel");
        addBlock(ModBlocks.SOUL_ASH.getBlock(), "Soul Ash",
                new Translation("description.1", "Spawns hostile mobs when awakened as a 1x1, 3x3, 5x5, 7x7 or 9x9"),
                new Translation("description.2", "Right click the center block with 4 Soul Crystals to activate the blocks"),
                new Translation("description.3", " "),
                new Translation("description.4", "Note: Mobs will only spawn on Soul Ash that has been activated")
        );
        addBlock(ModBlocks.SOUL_BLOOM.getBlock(), "Soul Bloom",
                new Translation("description.1", "Spawns passive mobs when awakened as a 1x1, 3x3, 5x5, 7x7 or 9x9"),
                new Translation("description.2", "Right click the center block with Soul Crystals to activate the blocks"),
                new Translation("description.3", " "),
                new Translation("description.4", "Note: Mobs will only spawn on activated Soul Bloom with a light level of 7 or higher")
        );
        addBlock(ModBlocks.WARDSTONE_TILES.getBlock(), "Wardstone Tiles");
        addBlock(ModBlocks.WITHER_WARD.getBlock(), "Wither Ward",
                new Translation("description", "Immune to explosions and the Wither.")
        );
        addBlock(ModBlocks.SOULSTREAM_PAD.getBlock(), "Soulstream Pad", new Translation("description", "Pushes the mob standing on it forwards"));
        addBlock(ModBlocks.RIFT_PAD.getBlock(), "Rift Pad",
                new Translation("description.1", "Teleports any entity standing on it to the linked Destination pad"),
                new Translation("description.2", "Use a Soul Wand to link the Destination Pad to the Rift Pad")
        );
        addBlock(ModBlocks.GRAVITY_ANCHOR.getBlock(), "Gravity Anchor",
                new Translation("description.1", "Prevents Spiders from climbing walls"),
                new Translation("description.2", "and flying mobs are pulled to the ground."),
                new Translation("description.3", "Note: Redstone toggles if the block is enabled/disabled.")
        );
        addBlock(ModBlocks.RIFT_ANCHOR.getBlock(), "Rift Anchor",
                new Translation("description.1", "Nearby enderman and shulkers can't teleport."),
                new Translation("description.2", "Note: Redstone toggles if the block is enabled/disabled.")
        );
        addBlock(ModBlocks.DESTINATION_PAD.getBlock(), "Destination Pad");
        addBlock(ModBlocks.SOUL_STEEL_BLOCK.getBlock(), "Block of Soul Steel");
        addBlock(ModBlocks.SOUL_CRYSTAL_BLOCK.getBlock(), "Block of Soul Crystal");
        addBlock(ModBlocks.WATCHER_BRICKS.getBlock(), "Watcher Bricks", new Translation("description", "Gives a redstone pulse when a mob dies within 8 blocks"));
        addBlock(ModBlocks.LIQUID_EXPERIENCE.get(), "Liquid Experience");
        addBlock(ModBlocks.WRAITHFIRE.get(), "Wraithfire");

        addItem(ModItems.LIQUID_EXPERIENCE_BUCKET.get(), "Liquid Experience Bucket");
        addItem(ModItems.SOUL_STEEL_NUGGET.get(), "Soul Steel Nugget");
        addItem(ModItems.FILTER.get(), "Filter",
                new Translation("description.1", "Use to set up filters, sneak-use to flip whitelist and blacklist"),
                new Translation("description.2", "Put it in a Loot Extractor filter slot for extra rules"),
                new Translation("blacklist", "Filter (Blacklist)")
        );
        add(ModFluids.LIQUID_EXPERIENCE_TYPE.get().getDescriptionId(), "Liquid Experience");

        add("message.soul_harvest.wand.bound", "Wand bound to the Soul Harvest Altar");
        add("message.soul_harvest.wand.linked", "Loot Extractor linked to the altar");
        add("message.soul_harvest.wand.unlinked", "Loot Extractor unlinked");
        add("message.soul_harvest.wand.cleared", "Wand cleared");
        add("message.soul_harvest.wand.no_altar", "Bind the wand to an altar first");
        add("message.soul_harvest.wand.too_far", "That extractor is too far from the altar");
        add("message.soul_harvest.wand.vessel_linked", "Wisdom Vessel linked to the altar");
        add("message.soul_harvest.wand.vessel_unlinked", "Wisdom Vessel unlinked");
        add("message.soul_harvest.wand.vessel_too_far", "That vessel is too far from the altar");
        add("message.soul_harvest.wand.destination_bound", "Wand bound to the Destination Pad");
        add("message.soul_harvest.wand.rift_linked", "Rift Pad linked to the Destination Pad");
        add("message.soul_harvest.wand.rift_unlinked", "Rift Pad unlinked");
        add("message.soul_harvest.wand.no_destination", "Bind the wand to a Destination Pad first");
        add("message.soul_harvest.wand.rift_too_far", "That Rift Pad is too far from the Destination Pad");

        add("message.soul_harvest.sigil.awakened", "The Sigil of Dread awakens.");
        add("message.soul_harvest.sigil.incomplete", "No flat square of Soul Ash is centered here (1x1, 3x3, 5x5, 7x7 or 9x9).");
        add("message.soul_harvest.sigil.no_crystals", "Awakening this pad needs %s Soul Crystals.");
        add("message.soul_harvest.sigil.already_awakened", "This Soul Ash is already part of an awakened Sigil of Dread.");

        add("message.soul_harvest.bloom_sigil.awakened", "The Sigil of Life awakens.");
        add("message.soul_harvest.bloom_sigil.incomplete", "No flat square of Soul Bloom is centered here (1x1, 3x3, 5x5, 7x7 or 9x9).");
        add("message.soul_harvest.bloom_sigil.no_crystals", "Awakening this pad needs %s Soul Crystals.");
        add("message.soul_harvest.bloom_sigil.already_awakened", "This Soul Bloom is already part of an awakened Sigil of Life.");

        add("gui.soul_harvest.loot_extractor.priority", "Priority");
        add("gui.soul_harvest.loot_extractor.priority.description", "Higher numbers are filled first\nTies share items in turn\nArrows +/-1, Shift +/-10, scroll works too");
        add("gui.soul_harvest.loot_extractor.filled_first", "filled first");
        add("gui.soul_harvest.loot_extractor.filled_last", "filled last");
        add("gui.soul_harvest.loot_extractor.priority_decrease", "Lower priority");
        add("gui.soul_harvest.loot_extractor.priority_increase", "Raise priority");
        add("gui.soul_harvest.loot_extractor.waiting", "Waiting to export");
        add("gui.soul_harvest.loot_extractor.filter_slot", "Click with an item to copy it");
        add("gui.soul_harvest.loot_extractor.whitelist", "Whitelist");
        add("gui.soul_harvest.loot_extractor.whitelist.description", "Only listed items are exported");
        add("gui.soul_harvest.loot_extractor.blacklist", "Blacklist");
        add("gui.soul_harvest.loot_extractor.blacklist.description", "Everything except listed items");
        add("gui.soul_harvest.loot_extractor.nbt_on", "Match NBT: On");
        add("gui.soul_harvest.loot_extractor.nbt_on.description", "Data must match exactly (enchants, name, damage)");
        add("gui.soul_harvest.loot_extractor.nbt_off", "Match NBT: Off");
        add("gui.soul_harvest.loot_extractor.nbt_off.description", "Only the item type is checked");
        add("gui.soul_harvest.loot_extractor.enabled", "Exporting: On");
        add("gui.soul_harvest.loot_extractor.enabled.description", "Click to pause this extractor");
        add("gui.soul_harvest.loot_extractor.disabled", "Exporting: Off");
        add("gui.soul_harvest.loot_extractor.disabled.description", "Click to resume this extractor");
        add("gui.soul_harvest.filter.title", "Filter");
        add("gui.soul_harvest.filter.tag", "Tag (slot %s)");
        add("gui.soul_harvest.filter.data_label", "Data filter (slot %s)");
        add("gui.soul_harvest.filter.exact_item", "Exact item");
        add("gui.soul_harvest.filter.filter_slot", "Click with an item to copy it, click empty-handed to select it, right-click to clear it");
        add("gui.soul_harvest.filter.whitelist", "Whitelist");
        add("gui.soul_harvest.filter.whitelist.description", "Only listed items pass");
        add("gui.soul_harvest.filter.blacklist", "Blacklist");
        add("gui.soul_harvest.filter.blacklist.description", "Everything except listed items passes");
        add("gui.soul_harvest.filter.nbt_on", "Match NBT: On");
        add("gui.soul_harvest.filter.nbt_on.description", "Data must match exactly (enchants, name, damage)");
        add("gui.soul_harvest.filter.nbt_off", "Match NBT: Off");
        add("gui.soul_harvest.filter.nbt_off.description", "Only the item type is checked");
        add("gui.soul_harvest.filter.damage_match", "Durability: Must match");
        add("gui.soul_harvest.filter.damage_match.description", "Worn and new items are different (needs Match NBT)");
        add("gui.soul_harvest.filter.damage_ignore", "Durability: Ignored");
        add("gui.soul_harvest.filter.damage_ignore.description", "Worn and new items match (needs Match NBT)");
        add("gui.soul_harvest.filter.data", "Data filter");
        add("gui.soul_harvest.filter.data.description", "Item components the item must have");
        add("gui.soul_harvest.filter.data.hint", "e.g. [damage=0]");
        add("gui.soul_harvest.filter.data.invalid", "Invalid data filter");
        add("gui.soul_harvest.wisdom_vessel.experience", "%s XP");
        add("gui.soul_harvest.wisdom_vessel.levels", "%s Levels");
        add("gui.soul_harvest.wisdom_vessel.store", "Store");
        add("gui.soul_harvest.wisdom_vessel.take", "Take");
        add("gui.soul_harvest.wisdom_vessel.store_1", "Store 1 level");
        add("gui.soul_harvest.wisdom_vessel.store_10", "Store 10 levels");
        add("gui.soul_harvest.wisdom_vessel.store_all", "Store all XP");
        add("gui.soul_harvest.wisdom_vessel.take_1", "Take 1 level");
        add("gui.soul_harvest.wisdom_vessel.take_10", "Take 10 levels");
        add("gui.soul_harvest.wisdom_vessel.take_all", "Take all XP");
    }

    private void addTranslations(String descriptionId, Translation... translations) {
        List.of(translations).forEach(translation -> add(descriptionId + "." + translation.key(), translation.value()));
    }

    private record Translation(String key, String value) {
    }
}
