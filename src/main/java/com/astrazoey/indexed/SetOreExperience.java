package com.astrazoey.indexed;

import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;

public interface SetOreExperience {

    static void set(Block oreBlock, IntProvider intProvider) {
        if(oreBlock instanceof DropExperienceBlock) {
            ((SetOreExperience) (DropExperienceBlock) oreBlock).setExperience(intProvider);
        }

    }

    void setExperience(IntProvider intProvider);

}
