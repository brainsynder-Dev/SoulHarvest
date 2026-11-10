package org.bsdevelopment.soul_harvest.hooks.jade;

import org.bsdevelopment.soul_harvest.blocks.SoulHarvestAltarBlock;
import org.bsdevelopment.soul_harvest.blocks.entity.SoulHarvestAltarBlockEntity;
import org.bsdevelopment.soul_harvest.hooks.jade.client.SoulHarvestAltarJadeProvider;
import org.bsdevelopment.soul_harvest.hooks.jade.server.SoulHarvestAltarJadeDataProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeHook implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(SoulHarvestAltarJadeProvider.INSTANCE, SoulHarvestAltarBlock.class);
    }

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(SoulHarvestAltarJadeDataProvider.INSTANCE, SoulHarvestAltarBlockEntity.class);
    }
}
