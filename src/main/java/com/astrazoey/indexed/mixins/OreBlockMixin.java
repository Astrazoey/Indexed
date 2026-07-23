package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.SetOreExperience;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.DropExperienceBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(DropExperienceBlock.class)
public class OreBlockMixin implements SetOreExperience {
    @Shadow
    @Final
    @Mutable
    private IntProvider xpRange;

    @Override
    public void setExperience(IntProvider intProvider) {
        this.xpRange = intProvider;
    }
}
