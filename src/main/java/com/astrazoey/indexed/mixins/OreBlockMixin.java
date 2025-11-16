package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.SetOreExperience;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.util.math.intprovider.IntProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(ExperienceDroppingBlock.class)
public class OreBlockMixin implements SetOreExperience {
    @Shadow
    @Final
    @Mutable
    private IntProvider experienceDropped;

    @Override
    public void setExperience(IntProvider intProvider) {
        this.experienceDropped = intProvider;
    }
}
