package com.astrazoey.indexed.mixin;

import com.astrazoey.indexed.registry.IndexedItems;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(EnchantmentMenu.class)
public class GoldBoundBookMixin {
    @Redirect(method = "slotsChanged(Lnet/minecraft/world/Container;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEnchantable()Z"))
    private boolean indexed$allowGoldBoundBookInTable(ItemStack itemStack) {
        if (itemStack.is(IndexedItems.GOLD_BOUND_BOOK)) {
            return !itemStack.isEnchanted();
        }
        return itemStack.isEnchantable();
    }

    @Redirect(method = "getEnchantmentList(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private boolean indexed$isGoldBoundBook(ItemStack itemStack, Object item) {
        return itemStack.is(Items.BOOK) || itemStack.is(IndexedItems.GOLD_BOUND_BOOK);
    }

    // fabric only, so require = 0
    @Redirect(
            method = "lambda$clickMenuButton$0(Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/world/entity/player/Player;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"),
            require = 0
    )
    private boolean indexed$transmuteGoldBoundBook(ItemStack itemStack, Object item) {
        return itemStack.is(Items.BOOK) || itemStack.is(IndexedItems.GOLD_BOUND_BOOK);
    }

}
