package org.bsdevelopment.soul_harvest.utilities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.bsdevelopment.soul_harvest.network.PacketHandler;
import org.bsdevelopment.soul_harvest.network.client.DebugChatMessagePayload;
import org.bsdevelopment.soul_harvest.utilities.storage.BulkStorage;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DebugUtil {
    public static void sendDebugMessages(Player player, String message, Object... args) {
        PacketHandler.sendPayload(player, new DebugChatMessagePayload(translateColorCodes(String.format(message, args))));
    }

    public static MutableComponent translateColorCodes(@NotNull String input) {
        Pattern colorCodePattern = Pattern.compile("&([0-9a-fA-Fk-oK-OrR])");
        MutableComponent output = Component.literal("").withStyle(ChatFormatting.WHITE);

        for (String part : input.split("\n")) {
            ChatFormatting currentColor = ChatFormatting.WHITE;
            Matcher matcher = colorCodePattern.matcher(part);
            int lastEnd = 0;

            while (matcher.find()) {
                if (matcher.start() > lastEnd) output.append(Component.literal(part.substring(lastEnd, matcher.start())).withStyle(currentColor));

                currentColor = ChatFormatting.getByCode(matcher.group(1).charAt(0));
                if (currentColor == null) currentColor = ChatFormatting.WHITE;

                lastEnd = matcher.end();
            }

            if (lastEnd < part.length()) output.append(Component.literal(part.substring(lastEnd)).withStyle(currentColor));

            output.append(Component.literal("\n"));
        }
        return output;
    }

    public static String formatValue(Object value) {
        if (value instanceof Integer) return "&e" + value;
        if (value instanceof UUID) return "&b" + value;
        if (value instanceof Boolean booleanValue) return booleanValue ? "&aTRUE" : "&cFALSE";
        if (value instanceof BlockPos pos) return "X: &c" + pos.getX() + "&f, Y: &a" + pos.getY() + "&f, Z: &3" + pos.getZ();

        return value.toString();
    }

    public static String formatItemRecord(BulkStorage.CondensedItemRecord record) {
        return "- " + Component.translatable(record.itemRecord().item().getDescriptionId()).getString() + "  x&e" + NumberFormat.getIntegerInstance().format(record.count()) + "\n";
    }

    public static String formatMultiPos(BlockPos... positions) {
        return "- " + Stream.of(positions).map(DebugUtil::formatValue).collect(Collectors.joining("\n- "));
    }

    public static MutableComponent createItemComponent(ItemStack itemStack) {
        itemStack.setCount(1);
        return ComponentUtils.wrapInSquareBrackets(Component.literal(itemStack.getHoverName().getString()))
                .withStyle(style -> style.withHoverEvent(new HoverEvent.ShowItem(ItemStackTemplate.fromNonEmptyStack(itemStack))));
    }
}
