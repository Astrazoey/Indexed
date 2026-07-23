package com.astrazoey.indexed.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.enchantment.Enchantment;

@Mixin({Enchantment.Builder.class})
public interface EnchantmentMixin {
    @Mutable
    @Accessor("definition")
    public void setDefinition(Enchantment.EnchantmentDefinition definition);

    @Mutable
    @Accessor("effectLists")
    Map<DataComponentType<?>, List<?>> effectLists();

    @Mutable
    @Accessor("effectLists")
    public void setEffectLists(Map<DataComponentType<?>, List<?>> effectLists);

    @Mutable
    @Accessor("effectMapBuilder")
    DataComponentMap.Builder effectMap();

    @Mutable
    @Accessor("effectMapBuilder")
    public void setEffectMap(DataComponentMap.Builder effectMap);
}
