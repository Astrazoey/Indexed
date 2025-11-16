package com.astrazoey.indexed.registry;

import com.astrazoey.indexed.Indexed;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

public class IndexedParticles {
    public static final SimpleParticleType CRYSTAL_HARVEST =
            Registry.register(Registries.PARTICLE_TYPE,
                    Indexed.id("crystal_harvest"),
                    FabricParticleTypes.simple()
            );

    public static final SimpleParticleType CRYSTAL_BREAK =
            Registry.register(Registries.PARTICLE_TYPE,
                    Indexed.id("crystal_break"),
                    FabricParticleTypes.simple()
            );

    public static void init() {

    }
}