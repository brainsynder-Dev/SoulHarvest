package org.bsdevelopment.soul_harvest.hooks.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.Identifier;
import org.bsdevelopment.soul_harvest.client.screen.FilterScreen;
import org.bsdevelopment.soul_harvest.client.screen.LootExtractorScreen;
import org.bsdevelopment.soul_harvest.hooks.jei.client.GhostSlotHandler;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

@JeiPlugin
public class JeiHook implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(LootExtractorScreen.class, new GhostSlotHandler<>());
        registration.addGhostIngredientHandler(FilterScreen.class, new GhostSlotHandler<>());
    }
}
