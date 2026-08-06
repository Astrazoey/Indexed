package com.astrazoey.indexed.registry;

import com.astrazoey.indexed.CommonClass;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class IndexedParticles {
    public static final SimpleParticleType CRYSTAL_HARVEST =
            Registry.register(BuiltInRegistries.PARTICLE_TYPE,
                    CommonClass.id("crystal_harvest"),
                    new SimpleParticleType(false) {}
            );

    public static final SimpleParticleType CRYSTAL_BREAK =
            Registry.register(BuiltInRegistries.PARTICLE_TYPE,
                    CommonClass.id("crystal_break"),
                    new SimpleParticleType(false) {}
            );

    public static void init() {

    }
}
