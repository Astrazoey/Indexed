package com.astrazoey.indexed.registry;

import com.astrazoey.indexed.Indexed;
import com.astrazoey.indexed.particles.CrystalHarvestParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.intellij.lang.annotations.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public class IndexedParticles {
    public static final Map<DefaultParticleType, ParticleFactoryRegistry.PendingParticleFactory<DefaultParticleType>> FACTORIES = new LinkedHashMap<>();

    public static final DefaultParticleType CRYSTAL_HARVEST = add("crystal_harvest", CrystalHarvestParticle.Factory::new);
    public static final DefaultParticleType CRYSTAL_BREAK = add("crystal_break", EndRodParticle.Factory::new);

    public static void init() {
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        FACTORIES.forEach(registry::register);
    }

    private static DefaultParticleType add(String name, ParticleFactoryRegistry.PendingParticleFactory<DefaultParticleType> constructor) {
        var particle = Registry.register(Registries.PARTICLE_TYPE, Indexed.id(name), FabricParticleTypes.simple());
        FACTORIES.put(particle, constructor);
        return particle;
    }

}
