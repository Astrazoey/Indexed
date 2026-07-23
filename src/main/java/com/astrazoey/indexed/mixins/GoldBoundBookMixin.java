package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.registry.IndexedItems;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentMenu.class)
public class GoldBoundBookMixin {
    @Redirect(method = "slotsChanged(Lnet/minecraft/world/Container;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEnchantable()Z"))
    private boolean indexed$allowGoldBoundBookInTable(ItemStack itemStack) {
        return itemStack.isEnchantable() || itemStack.is(IndexedItems.GOLD_BOUND_BOOK);
    }

    @Redirect(method = "getEnchantmentList(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private boolean indexed$isGoldBoundBook(ItemStack itemStack, Object item) {
        return itemStack.is(Items.BOOK) || itemStack.is(IndexedItems.GOLD_BOUND_BOOK);
    }


}

@Mixin(EnchantmentHelper.class)
class GoldBoundBookEnchantmentMixin {

    @Redirect(method = "getAvailableEnchantmentResults(ILnet/minecraft/world/item/ItemStack;Ljava/util/stream/Stream;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private static boolean indexed$isGoldBoundBook(ItemStack itemStack, Object item) {
        return itemStack.is(Items.BOOK) || itemStack.is(IndexedItems.GOLD_BOUND_BOOK);
    }

    @Redirect(method = "enchantItem(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILjava/util/stream/Stream;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private static boolean indexed$isGoldBoundBookWhenEnchanting(ItemStack itemStack, Object item) {
        return itemStack.is(Items.BOOK) || itemStack.is(IndexedItems.GOLD_BOUND_BOOK);
    }


}
