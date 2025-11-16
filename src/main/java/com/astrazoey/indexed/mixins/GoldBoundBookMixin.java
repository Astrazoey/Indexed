package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.registry.IndexedItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.EnchantmentScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentScreenHandler.class)
public class GoldBoundBookMixin {
    @Redirect(method = "method_17410", at = @At(value="INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 0))
    public boolean allowBookItem(ItemStack itemStack, Item item) {
        return itemStack.isOf(Items.BOOK) || itemStack.isOf(IndexedItems.GOLD_BOUND_BOOK);
    }

    @Redirect(method = "generateEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 0))
    public boolean allowBookItem2(ItemStack itemStack, Item item) {
        return itemStack.isOf(Items.BOOK) || itemStack.isOf(IndexedItems.GOLD_BOUND_BOOK);
    }


}

@Mixin(EnchantmentHelper.class)
class GoldBoundBookEnchantmentMixin {

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean allowBookItem(ItemStack itemStack, Item item) {


        return itemStack.isOf(Items.BOOK) || itemStack.isOf(IndexedItems.GOLD_BOUND_BOOK);
    }

    @Redirect(method = "enchant(Lnet/minecraft/util/math/random/Random;Lnet/minecraft/item/ItemStack;ILjava/util/stream/Stream;)Lnet/minecraft/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean allowBookItem2(ItemStack itemStack, Item item) {
        return itemStack.isOf(Items.BOOK) || itemStack.isOf(IndexedItems.GOLD_BOUND_BOOK);
    }


}
