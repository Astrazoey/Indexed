package com.astrazoey.indexed.mixins;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntryList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Map;

@Mixin({Enchantment.Builder.class})
public interface EnchantmentMixin {
    @Mutable
    @Accessor("definition")
    public void setDefinition(Enchantment.Definition definition);

    @Mutable
    @Accessor("effectLists")
    Map<ComponentType<?>, List<?>> effectLists();

    @Mutable
    @Accessor("effectLists")
    public void setEffectLists(Map<ComponentType<?>, List<?>> effectLists);

    @Mutable
    @Accessor("effectMap")
    ComponentMap.Builder effectMap();

    @Mutable
    @Accessor("effectMap")
    public void setEffectMap(ComponentMap.Builder effectMap);
}
