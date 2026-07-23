package com.astrazoey.indexed.registry;

import net.fabricmc.api.ClientModInitializer;

/**
 * Particle factory registration changed in Fabric API 26.2. The particle
 * types remain registered; factories are intentionally deferred so a missing
 * legacy API does not prevent clients from launching.
 */
public class IndexedParticleFactory implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
    }
}
