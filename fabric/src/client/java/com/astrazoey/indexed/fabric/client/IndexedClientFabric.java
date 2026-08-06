package com.astrazoey.indexed.fabric.client;

import com.astrazoey.indexed.CommonClass;
import com.astrazoey.indexed.fabric.client.particles.CrystalHarvestParticle;
import com.astrazoey.indexed.network.ConfigS2CPayload;
import com.astrazoey.indexed.registry.IndexedParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.resources.Identifier;

public class IndexedClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigS2CPayload.ID, (payload, context) -> {
            ClientEnchantingConfigHolder.setConfig(payload.configList());
        });

        Identifier identifier = Identifier.parse(com.astrazoey.indexed.Constants.MOD_ID);
        ClientLifecycleEvents.CLIENT_STARTED.register(identifier, callbacks -> {
            com.astrazoey.indexed.Constants.LOG.info("Client started. Loading config.");
            CommonClass.loadConfig();
        });

        ParticleProviderRegistry.getInstance().register(IndexedParticles.CRYSTAL_HARVEST, CrystalHarvestParticle.Factory::new);
        ParticleProviderRegistry.getInstance().register(IndexedParticles.CRYSTAL_BREAK, CrystalHarvestParticle.Factory::new);
    }
}
