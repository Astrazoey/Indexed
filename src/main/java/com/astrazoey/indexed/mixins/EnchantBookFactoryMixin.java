package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.ConfigMain;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;



@Mixin(targets = {"net/minecraft/world/entity/npc/VillagerTrades$EnchantBookForEmeralds"})
public class EnchantBookFactoryMixin {
    @Redirect(method = "getOffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    public int updateMaxEnchantment(Enchantment enchantment) {
        if(enchantment.getMaxLevel() > 2 && ConfigMain.enableVillagerNerfs) {
            return 2;
        } else {
            return enchantment.getMaxLevel();
        }
    }
}

