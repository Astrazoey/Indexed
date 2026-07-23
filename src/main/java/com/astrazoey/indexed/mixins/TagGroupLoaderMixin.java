package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.ConfigMain;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagLoader;

@Mixin(TagLoader.class)
public class TagGroupLoaderMixin {
    @ModifyReturnValue(method="load", at = @At(value="TAIL"))
    public Map<Identifier, List<TagLoader.EntryWithSource>> loadTags(Map<Identifier, List<TagLoader.EntryWithSource>> original) {
        List<TagLoader.EntryWithSource> entriesToRemove = new ArrayList<>();
        for (var resource : original.entrySet()) {
            if (resource.getKey().toLanguageKey().equals("minecraft.tradeable")) {
                for (TagLoader.EntryWithSource entry : resource.getValue())
                {
                    String entryName = entry.entry().toString();
                    if (entryName.equals("#minecraft:non_treasure")) {
                        if (ConfigMain.enableVillagerNerfs) {
                            entriesToRemove.add(entry);
                        }
                    }
                }
            }
            for (TagLoader.EntryWithSource removable : entriesToRemove) {
                resource.getValue().remove(removable);
            }
        }
        return original;
    }
}
