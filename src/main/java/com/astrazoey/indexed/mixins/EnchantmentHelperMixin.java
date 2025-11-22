package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.ConfigMain;
import com.astrazoey.indexed.Indexed;
import com.astrazoey.indexed.MaxEnchantingSlots;
import com.astrazoey.indexed.registry.IndexedItems;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancement.criterion.EnchantedItemCriterion;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.Math.min;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    //Prevents enchantments such as slow burn from being applied to armor in loot

    private static ThreadLocal<ItemStack> generatedItemType = new ThreadLocal<ItemStack>();


    @Inject(method = "generateEnchantments", at = @At(value = "HEAD"))
    private static void getItemStack(Random random, ItemStack stack, int level, Stream<RegistryEntry<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        generatedItemType.set(stack);
    }

    @ModifyConstant(method = "generateEnchantments", constant = @Constant(intValue = 50, ordinal = 0))
    private static int increaseGoldBookEffectiveness(int constant) {
        if(generatedItemType.get().isOf(IndexedItems.GOLD_BOUND_BOOK)) {
            return 10;
        } else {
            return constant;
        }
    }

    // Special infinity behavior
    @Inject(method = "getAmmoUse", at = @At(value = "HEAD"), cancellable = true)
    private static void onStoppedUsing(ServerWorld world, ItemStack rangedWeaponStack, ItemStack projectileStack, int baseAmmoUse, CallbackInfoReturnable<Integer> cir) {

        int infinityLevel = Indexed.getEnchantmentValue(Indexed.REPLENISH_PROJECTILE, world, rangedWeaponStack);

        double infinityChance = 0.5d + (infinityLevel * 0.1d);

        double randomNumber = Math.random();

        if(infinityChance > randomNumber && infinityLevel > 0) {
            cir.setReturnValue(0);
        }
    }

    @ModifyArg(method =  "generateEnchantments", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V", ordinal = 1))
    private static @NotNull Consumer<EnchantmentLevelEntry> returnEarlyIfOverBudget(Consumer<EnchantmentLevelEntry> action, @Local(ordinal = 0) List<EnchantmentLevelEntry> list, @Local(ordinal = 0) ItemStack stack) {
        // if overcharge is enabled, return default value. Else, run custom function
        return ConfigMain.enableOvercharge ? action : (enchant) -> {
            // get the list of enchants in itemstack form, so that I can use MaxEnchantingSlots functions
            ItemStack stackCopy = stack.copy();
            for (EnchantmentLevelEntry e : list) {
                stackCopy.addEnchantment(e.enchantment(), e.level());
            }

            int maxSlots = MaxEnchantingSlots.getEnchantType(stackCopy).maxEnchantingSlots;
            int usedSlots = MaxEnchantingSlots.getCurrent(stackCopy);

            //only add the enchantment if it fits in the remaining slots
            if (usedSlots + enchant.level() <= maxSlots) {
                list.add(enchant);
            }
        };
    }


    @Inject(method = "getPossibleEntries", at = @At(value = "HEAD"), cancellable = true)
    private static void checkAcceptableEnchantments(int level, ItemStack stack, Stream<RegistryEntry<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {

        List<EnchantmentLevelEntry> returnList = Lists.<EnchantmentLevelEntry>newArrayList();

        boolean bl = stack.isOf(Items.BOOK) || stack.isOf(IndexedItems.GOLD_BOUND_BOOK);

        possibleEnchantments.filter(enchantment -> doStuff(enchantment, stack) || bl).forEach(enchantmentx -> {
            Enchantment enchantment = (Enchantment)enchantmentx.value();

            int maxLevel = enchantment.getMaxLevel();
            int minLevel = enchantment.getMinLevel();

            // Get Forgery I only in the enchanting table
            if(enchantment.effects().contains(Indexed.REDUCE_REPAIR_COST)) {
                maxLevel = 1;
            }

            // Get Unbreaking III only in the table
            else if(enchantmentx.matchesKey(Enchantments.UNBREAKING)) {
                maxLevel = 3;
            }

            // Get Mending II only in the table
            else if(enchantmentx.matchesKey(Enchantments.MENDING)) {
                maxLevel = 2;
            }




            for (int j = maxLevel; j >= minLevel; j--) {
                if (level >= enchantment.getMinPower(j) && level <= enchantment.getMaxPower(j)) {
                    returnList.add(new EnchantmentLevelEntry(enchantmentx, j));
                    break;
                }
            }
        });


        cir.setReturnValue(returnList);
    }


    //Exclude unbreaking from Gold Bound Book enchantments
    @Unique
    private static boolean doStuff(RegistryEntry<Enchantment> enchantment, ItemStack stack) {
        if(stack.isOf(IndexedItems.GOLD_BOUND_BOOK)) {
            if(enchantment.matchesKey(Enchantments.UNBREAKING)) {
                return false;
            }
        }

        return enchantment.value().isPrimaryItem(stack);
    }
}


@Mixin(EnchantmentScreenHandler.class)
class TakeEnchantment {

    @Unique
    ThreadLocal<Integer> effectLevel = new ThreadLocal<Integer>();

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
    @Redirect(method="method_17410", at = @At(value="INVOKE", target = "Lnet/minecraft/item/ItemStack;addEnchantment(Lnet/minecraft/registry/entry/RegistryEntry;I)V"))
    public void enchantedStatusEffect(ItemStack instance, RegistryEntry<Enchantment> enchantment, int level) {
        int newEnchantmentLevel;
        if(effectLevel != null) {
            newEnchantmentLevel = min(level+effectLevel.get(), enchantment.value().getMaxLevel());
        } else {
            newEnchantmentLevel = level;
        }
        
        if (ConfigMain.enableOvercharge || MaxEnchantingSlots.getCurrent(instance) + newEnchantmentLevel <= MaxEnchantingSlots.getEnchantType(instance).maxEnchantingSlots) {
            instance.addEnchantment(enchantment, newEnchantmentLevel);
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