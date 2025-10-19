package com.astrazoey.indexed.registry;

import com.astrazoey.indexed.Indexed;
import com.astrazoey.indexed.particles.CrystalHarvestParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.intellij.lang.annotations.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public class IndexedParticles {
    public static final Map<SimpleParticleType, ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType>> FACTORIES = new LinkedHashMap<>();

    public static final SimpleParticleType CRYSTAL_HARVEST = add("crystal_harvest", CrystalHarvestParticle.Factory::new);
    public static final SimpleParticleType CRYSTAL_BREAK = add("crystal_break", EndRodParticle.Factory::new);

    public static void init() {
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        FACTORIES.forEach(registry::register);
    }

    private static SimpleParticleType add(String name, ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType> constructor) {
        var particle = Registry.register(Registries.PARTICLE_TYPE, Indexed.id(name), FabricParticleTypes.simple());
        FACTORIES.put(particle, constructor);
        return particle;
    }

}
