package com.astrazoey.indexed.mixins;

import net.minecraft.block.BlockState;

public interface PlayerEntityMixinInterface {
    default boolean canHarvest(BlockState state) {return true;}
}
