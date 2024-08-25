package com.astrazoey.indexed;

import com.astrazoey.indexed.particles.CrystalHarvestParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
//import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.particle.DamageParticle;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class Client implements ClientModInitializer {

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {

        /*
        //Initialize Particle
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(new Identifier("indexed", "particle/crystal_harvest"));
        }));
        ParticleFactoryRegistry.getInstance().register(Indexed.CRYSTAL_HARVEST, EndRodParticle.Factory::new);

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(new Identifier("indexed", "particle/crystal_break"));
        }));
        ParticleFactoryRegistry.getInstance().register(Indexed.CRYSTAL_BREAK, DamageParticle.Factory::new);

         */
        //Load Indexed Config on Client
        Identifier identifier = new Identifier(Indexed.MOD_ID);
        ClientLifecycleEvents.CLIENT_STARTED.register(identifier, callbacks -> {
            System.out.println("INDEXED: Client started. Loading config.");
            Indexed.initializeConfig();
        });
    }
}
