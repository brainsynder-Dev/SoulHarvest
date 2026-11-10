package org.bsdevelopment.soul_harvest.utilities;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ItemFilterPredicates {
    private static final Map<String, Predicate<ItemStack>> CACHE = new HashMap<>();

    public static boolean test(String data, ItemStack stack, Level level) {
        Predicate<ItemStack> predicate = CACHE.get(data);
        if (predicate == null) {
            try {
                predicate = parse(data, level);
            } catch (CommandSyntaxException exception) {
                predicate = candidate -> false;
            }

            if (CACHE.size() >= 256) CACHE.clear();

            CACHE.put(data, predicate);
        }
        return predicate.test(stack);
    }

    public static String validate(String data, Level level) {
        if (data.isEmpty()) return null;

        try {
            parse(data, level);
            return null;
        } catch (CommandSyntaxException exception) {
            return exception.getMessage();
        }
    }

    private static Predicate<ItemStack> parse(String data, Level level) throws CommandSyntaxException {
        CommandBuildContext context = CommandBuildContext.simple(level.registryAccess(), level.enabledFeatures());
        return ItemPredicateArgument.itemPredicate(context).parse(new StringReader("*" + data));
    }
}
