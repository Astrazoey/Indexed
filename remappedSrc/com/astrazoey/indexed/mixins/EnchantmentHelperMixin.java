package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.EnchantingAcceptability;
import com.astrazoey.indexed.Indexed;
import com.astrazoey.indexed.MaxEnchantingSlots;
import com.astrazoey.indexed.registry.IndexedItems;
import net.minecraft.advancement.criterion.EnchantedItemCriterion;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static java.lang.Math.min;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    //Prevents enchantments such as slow burn from being applied to armor in loot

    private static ThreadLocal<ItemStack> itemType = new ThreadLocal<ItemStack>();
    private static ThreadLocal<ItemStack> generatedItemType = new ThreadLocal<ItemStack>();

    @Inject(method = "getPossibleEntries", at = @At(value="INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static void getItem(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        itemType.set(stack);
    }


    @Inject(method = "generateEnchantments", at = @At(value = "HEAD"))
    private static void getItemStack(Random random, ItemStack stack, int level, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        generatedItemType.set(stack);
    }

    @ModifyConstant(method = "generateEnchantments", constant = @Constant(intValue = 50, ordinal = 0))
    private static int increaseGoldBookEffectiveness(int constant) {
        if(generatedItemType.get().isOf(IndexedItems.GOLD_BOUND_BOOK)) {
            return 15;
        } else {
            return constant;
        }
    }

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isAvailableForRandomSelection()Z"))
    private static boolean checkAcceptableEnchantments(Enchantment enchantment) {

        EnchantingAcceptability acceptabilityTest = new EnchantingAcceptability();
        //return acceptabilityTest.checkAcceptability(enchantment, itemType);



        if(itemType.get().isOf(IndexedItems.GOLD_BOUND_BOOK)) {
            if(enchantment.getRarity() == Enchantment.Rarity.COMMON) {
                System.out.println("Excluded enchantment: " + enchantment);
                return false;
            }
        }

        if(acceptabilityTest.acceptableCheck(enchantment, itemType.get())) {
            return enchantment.isAvailableForRandomSelection();
        }

        return false;
    }

}


@Mixin(EnchantmentScreenHandler.class)
class TakeEnchantment {

    ThreadLocal<Integer> effectLevel = new ThreadLocal<Integer>();

    @Shadow
    private List<EnchantmentLevelEntry> generateEnchantments(ItemStack stack, int slot, int level) {
        return null;
    }

    //Grant Gold Book Enchantment
    @Inject(method = "method_17410", at = @At(value="INVOKE", target = "Lnet/minecraft/advancement/criterion/EnchantedItemCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/item/ItemStack;I)V"))
    public void grantGoldBookAdvancement(ItemStack itemStack, int i, PlayerEntity playerEntity, int j, ItemStack itemStack2, World world, BlockPos pos, CallbackInfo ci) {
        if(itemStack.isOf(IndexedItems.GOLD_BOUND_BOOK)) {
            Indexed.ENCHANT_GOLD_BOOK.trigger((ServerPlayerEntity) playerEntity);
        }
    }

    //Get Player Enchanted Level
    @Inject(method="method_17410", at = @At(value="HEAD"))
    public void getPlayerEnchantedLevel(ItemStack itemStack, int i, PlayerEntity playerEntity, int j, ItemStack itemStack2, World world, BlockPos pos, CallbackInfo ci) {
        try {
            effectLevel.set(playerEntity.getStatusEffect(Indexed.ENCHANTED_STATUS_EFFECT).getAmplifier()+1);
        } catch (NullPointerException e) {
            effectLevel.set(0);
        }

        if(effectLevel.get() > 0) {
            if(playerEntity instanceof ServerPlayerEntity) {
                Indexed.ENCHANTED_ADVANCEMENT.trigger((ServerPlayerEntity) playerEntity);
            }
        }

    }

    //Take Enchanted Status Effect Into Account
    @Redirect(method="method_17410", at = @At(value="INVOKE", target = "Lnet/minecraft/item/ItemStack;addEnchantment(Lnet/minecraft/enchantment/Enchantment;I)V"))
    public void enchantedStatusEffect(ItemStack instance, Enchantment enchantment, int level) {
        if(effectLevel != null) {
            int newEnchantmentLevel = min(level+effectLevel.get(), enchantment.getMaxLevel());
            instance.addEnchantment(enchantment, newEnchantmentLevel);
        } else {
            instance.addEnchantment(enchantment, level);
        }
    }

    @Redirect(method="method_17410", at = @At(value="INVOKE", target = "Lnet/minecraft/item/EnchantedBookItem;addEnchantment(Lnet/minecraft/item/ItemStack;Lnet/minecraft/enchantment/EnchantmentLevelEntry;)V"))
    public void enchantedStatusEffectBook(ItemStack stack, EnchantmentLevelEntry entry) {
        if(effectLevel != null) {
            EnchantmentLevelEntry newEntry = new EnchantmentLevelEntry(entry.enchantment, min(entry.level + effectLevel.get(), entry.enchantment.getMaxLevel()));
            EnchantedBookItem.addEnchantment(stack, newEntry);
        } else {
            EnchantedBookItem.addEnchantment(stack, entry);
        }
    }

    //Grant Overcharged Advancement
    @Redirect(method = "method_17410", at = @At(value="INVOKE", target = "Lnet/minecraft/advancement/criterion/EnchantedItemCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/item/ItemStack;I)V"))
    public void grantOverchargedAdvancement(EnchantedItemCriterion instance, ServerPlayerEntity player, ItemStack stack, int levels) {

        if(MaxEnchantingSlots.getEnchantType(stack) != null) {
            if(MaxEnchantingSlots.getCurrent(stack) > MaxEnchantingSlots.getEnchantType(stack).getMaxEnchantingSlots()) {
                Indexed.OVERCHARGE_ITEM.trigger(player);
            }
        }

        instance.trigger(player, stack, levels);

    }
}