package com.astrazoey.indexed;

import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public interface SetOreExperience {

    static void set(Block oreBlock, IntProvider intProvider) {
        if(oreBlock instanceof ExperienceDroppingBlock) {
            ((SetOreExperience) (ExperienceDroppingBlock) oreBlock).setExperience(intProvider);
        }

    }

    void setExperience(IntProvider intProvider);

}
