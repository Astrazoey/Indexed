package com.astrazoey.indexed.neoforge.mixin;

import com.astrazoey.indexed.registry.IndexedItems;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

// gold bound book
@Mixin(EnchantmentMenu.class)
public class GoldBoundBookApplyEnchantmentsMixin {

    @Redirect(
            method = "lambda$clickMenuButton$0(Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/world/entity/player/Player;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;applyEnchantments(Lnet/minecraft/world/item/ItemStack;Ljava/util/List;)Lnet/minecraft/world/item/ItemStack;")
    )
    private ItemStack indexed$applyGoldBoundBookEnchantments(Item item, ItemStack stack, List<EnchantmentInstance> enchantments) {
        if (stack.is(IndexedItems.GOLD_BOUND_BOOK)) {
            return Items.ENCHANTED_BOOK.applyEnchantments(stack.transmuteCopy(Items.ENCHANTED_BOOK), enchantments);
        }
        return item.applyEnchantments(stack, enchantments);
    }
}
