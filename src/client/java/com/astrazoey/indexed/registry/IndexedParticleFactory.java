package com.astrazoey.indexed.registry;

import com.astrazoey.indexed.particles.CrystalHarvestParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.EndRodParticle;

public class IndexedParticleFactory implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(
                IndexedParticles.CRYSTAL_HARVEST,
                CrystalHarvestParticle.Factory::new
        );

        ParticleFactoryRegistry.getInstance().register(
                IndexedParticles.CRYSTAL_BREAK,
                EndRodParticle.Factory::new
        );
    }
}