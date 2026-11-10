package org.bsdevelopment.soul_harvest.client.render;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import org.bsdevelopment.soul_harvest.registry.ModConstants;

public class WraithfireSprites {
    public static final ContextKey<Boolean> BURNING = new ContextKey<>(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "wraithfire"));
    public static final SpriteId FIRE_0 = Sheets.BLOCKS_MAPPER.apply(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "wraithfire_0"));
    public static final SpriteId FIRE_1 = Sheets.BLOCKS_MAPPER.apply(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "wraithfire_1"));

    public static SpriteId swap(SpriteId vanillaSprite) {
        return vanillaSprite.equals(ModelBakery.FIRE_0) ? FIRE_0 : FIRE_1;
    }
}
