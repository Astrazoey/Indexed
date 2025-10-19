package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Config;
import com.astrazoey.indexed.ConfigMain;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(TagGroupLoader.class)
public class TagGroupLoaderMixin {
    @ModifyReturnValue(method="loadTags", at = @At(value="TAIL"))
    public Map<Identifier, List<TagGroupLoader.TrackedEntry>> loadTags(Map<Identifier, List<TagGroupLoader.TrackedEntry>> original) {
        List<TagGroupLoader.TrackedEntry> entriesToRemove = new ArrayList<>();
        for (var resource : original.entrySet()) {
            if (resource.getKey().toTranslationKey().equals("minecraft.tradeable")) {
                for (TagGroupLoader.TrackedEntry entry : resource.getValue())
                {
                    String entryName = entry.entry().toString();
                    switch (entryName) {
                        case "#minecraft:non_treasure":
                            if (ConfigMain.enableVillagerNerfs) {
                                entriesToRemove.add(entry);
                            }
                            break;
                        case "minecraft:mending":
                            if (!ConfigMain.mendingIsTreasure) {
                                entriesToRemove.add(entry);
                            }
                            break;
                    }
                }
            }
            else if (resource.getKey().toTranslationKey().equals("minecraft.non_treasure")) {
                for (TagGroupLoader.TrackedEntry entry : resource.getValue())
                {
                    String entryName = entry.entry().toString();
                    switch (entryName) {
                        case "minecraft:mending":
                            if (ConfigMain.mendingIsTreasure) {
                                entriesToRemove.add(entry);
                            }
                            break;
                        case "indexed:quick_flight":
                            if (!ConfigMain.enableQuickFlight) {
                                entriesToRemove.add(entry);
                            }
                            break;
                    }
                }
            }
            else if (resource.getKey().toTranslationKey().equals("minecraft.treasure")) {
                for (TagGroupLoader.TrackedEntry entry : resource.getValue())
                {
                    String entryName = entry.entry().toString();
                    switch (entryName) {
                        case "minecraft:mending":
                            if (!ConfigMain.mendingIsTreasure) {
                                entriesToRemove.add(entry);
                            }
                            break;
                    }
                }
            }
            for (TagGroupLoader.TrackedEntry removable : entriesToRemove) {
                resource.getValue().remove(removable);
            }
        }
        return original;
    }
}
