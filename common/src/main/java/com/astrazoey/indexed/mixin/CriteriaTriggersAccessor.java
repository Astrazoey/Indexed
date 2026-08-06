package com.astrazoey.indexed.mixin;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.CriterionTrigger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CriteriaTriggers.class)
public interface CriteriaTriggersAccessor {
    @Invoker("register")
    static <T extends CriterionTrigger<?>> T indexed$register(String id, T trigger) {
        throw new AssertionError();
    }
}
