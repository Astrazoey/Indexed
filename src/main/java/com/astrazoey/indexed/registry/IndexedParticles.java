package com.astrazoey.indexed.registry;

import com.astrazoey.indexed.Indexed;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class IndexedParticles {
    public static final SimpleParticleType CRYSTAL_HARVEST =
            Registry.register(BuiltInRegistries.PARTICLE_TYPE,
                    Indexed.id("crystal_harvest"),
                    FabricParticleTypes.simple()
            );

    public static final SimpleParticleType CRYSTAL_BREAK =
            Registry.register(BuiltInRegistries.PARTICLE_TYPE,
                    Indexed.id("crystal_break"),
                    FabricParticleTypes.simple()
            );

    public static void init() {

    }
}