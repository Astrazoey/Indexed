package com.astrazoey.indexed;

import com.astrazoey.indexed.network.ConfigS2CPayload;
import com.astrazoey.indexed.registry.IndexedParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class IndexedClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        IndexedParticles.init();

        ClientPlayNetworking.registerGlobalReceiver(ConfigS2CPayload.ID, (payload, context) -> {
            // Cache the config map client-side
            ClientEnchantingConfigHolder.setConfig(payload.configList());
        });

        Identifier identifier = Identifier.of(Indexed.MOD_ID);
        ClientLifecycleEvents.CLIENT_STARTED.register(identifier, callbacks -> {
            System.out.println("INDEXED: Client started. Loading config.");
            Indexed.initializeConfig();
        });
    }
}
