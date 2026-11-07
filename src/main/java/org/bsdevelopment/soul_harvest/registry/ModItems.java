package org.bsdevelopment.soul_harvest.registry;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ModConstants.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModConstants.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD_CREATIVE_TAB = CREATIVE_MODE_TABS.register(ModConstants.MOD_ID,
            () -> CreativeModeTab.builder().title(gradientTitle())
                    .withTabsBefore(CreativeModeTabs.OP_BLOCKS)
                    .icon(() -> new ItemStack(Items.SOUL_LANTERN))
                    .displayItems((parameters, output) -> {
                    }).build());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        CREATIVE_MODE_TABS.register(eventBus);
    }

    private static Component gradientTitle() {
        String text = "Soul Harvest";
        int startColor = TextColor.parseColor("#0A2A3F").getOrThrow().getValue();
        int endColor = TextColor.parseColor("#19B8FF").getOrThrow().getValue();
        MutableComponent title = Component.empty();

        for (int index = 0; index < text.length(); index++) {
            int color = ARGB.srgbLerp((float) index / (text.length() - 1), startColor, endColor);
            title.append(Component.literal(String.valueOf(text.charAt(index))).withColor(color).withStyle(ChatFormatting.BOLD));
        }
        return title;
    }
}
