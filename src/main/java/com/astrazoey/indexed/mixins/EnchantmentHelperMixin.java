package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Indexed;
import com.astrazoey.indexed.MaxEnchantingSlots;
import com.astrazoey.indexed.registry.IndexedItems;
import com.google.common.collect.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.advancements.triggers.EnchantedItemTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import static java.lang.Math.min;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    //Prevents enchantments such as slow burn from being applied to armor in loot

    private static ThreadLocal<ItemStack> generatedItemType = new ThreadLocal<ItemStack>();


    @Inject(method = "selectEnchantment", at = @At(value = "HEAD"))
    private static void getItemStack(RandomSource random, ItemStack stack, int level, Stream<Holder<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        generatedItemType.set(stack);
    }

    @ModifyConstant(method = "selectEnchantment", constant = @Constant(intValue = 50, ordinal = 0))
    private static int increaseGoldBookEffectiveness(int constant) {
        if(generatedItemType.get().is(IndexedItems.GOLD_BOUND_BOOK)) {
            return 10;
        } else {
            return constant;
        }
    }

    // Special infinity behavior
    @Inject(method = "processAmmoUse", at = @At(value = "HEAD"), cancellable = true)
    private static void onStoppedUsing(ServerLevel world, ItemStack rangedWeaponStack, ItemStack projectileStack, int baseAmmoUse, CallbackInfoReturnable<Integer> cir) {

        int infinityLevel = Indexed.getEnchantmentValue(Indexed.REPLENISH_PROJECTILE, world, rangedWeaponStack);

        double infinityChance = 0.5d + (infinityLevel * 0.1d);

        double randomNumber = Math.random();

        if(infinityChance > randomNumber && infinityLevel > 0) {
            cir.setReturnValue(0);
        }
    }



    @Inject(method = "getAvailableEnchantmentResults", at = @At(value = "HEAD"), cancellable = true)
    private static void checkAcceptableEnchantments(int level, ItemStack stack, Stream<Holder<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {

        List<EnchantmentInstance> returnList = Lists.<EnchantmentInstance>newArrayList();

        boolean bl = stack.is(Items.BOOK) || stack.is(IndexedItems.GOLD_BOUND_BOOK);

        possibleEnchantments.filter(enchantment -> doStuff(enchantment, stack) || bl).forEach(enchantmentx -> {
            Enchantment enchantment = (Enchantment)enchantmentx.value();

            int maxLevel = enchantment.getMaxLevel();
            int minLevel = enchantment.getMinLevel();

            // Get Forgery I only in the enchanting table
            if(enchantment.effects().has(Indexed.REDUCE_REPAIR_COST)) {
                maxLevel = 1;
            }

            // Get Unbreaking III only in the table
            else if(enchantmentx.is(Enchantments.UNBREAKING)) {
                maxLevel = 3;
            }

            // Get Mending II only in the table
            else if(enchantmentx.is(Enchantments.MENDING)) {
                maxLevel = 2;
            }




            for (int j = maxLevel; j >= minLevel; j--) {
                if (level >= enchantment.getMinCost(j) && level <= enchantment.getMaxCost(j)) {
                    returnList.add(new EnchantmentInstance(enchantmentx, j));
                    break;
                }
            }
        });


        cir.setReturnValue(returnList);
    }


    //Exclude unbreaking from Gold Bound Book enchantments
    @Unique
    private static boolean doStuff(Holder<Enchantment> enchantment, ItemStack stack) {
        if(stack.is(IndexedItems.GOLD_BOUND_BOOK)) {
            if(enchantment.is(Enchantments.UNBREAKING)) {
                return false;
            }
        }

        return enchantment.value().isPrimaryItem(stack);
    }
}


@Mixin(EnchantmentMenu.class)
class TakeEnchantment {

    @Unique
    ThreadLocal<Integer> effectLevel = new ThreadLocal<Integer>();

    //Grant Gold Book Enchantment
    @Inject(method = "lambda$clickMenuButton$0(Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/world/entity/player/Player;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/triggers/EnchantedItemTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/ItemStack;I)V"))
    public void grantGoldBookAdvancement(ItemStack itemStack, int i, Player playerEntity, int j, ItemStack itemStack2, Level world, BlockPos pos, CallbackInfo ci) {
        if(itemStack.is(IndexedItems.GOLD_BOUND_BOOK)) {
            Indexed.ENCHANT_GOLD_BOOK.trigger((ServerPlayer) playerEntity);
        }
    }

    //Get Player Enchanted Level
    @Inject(method = "lambda$clickMenuButton$0(Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/world/entity/player/Player;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At(value = "HEAD"))
    public void getPlayerEnchantedLevel(ItemStack itemStack, int i, Player playerEntity, int j, ItemStack itemStack2, Level world, BlockPos pos, CallbackInfo ci) {
        try {
            effectLevel.set(playerEntity.getEffect(Indexed.ENCHANTED_STATUS_EFFECT).getAmplifier()+1);
        } catch (NullPointerException e) {
            effectLevel.set(0);
        }

        if(effectLevel.get() > 0) {
            if(playerEntity instanceof ServerPlayer) {
                Indexed.ENCHANTED_ADVANCEMENT.trigger((ServerPlayer) playerEntity);
            }
        }

    }

    //Take Enchanted Status Effect Into Account
    @Redirect(method = "lambda$clickMenuButton$0(Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/world/entity/player/Player;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;enchant(Lnet/minecraft/core/Holder;I)V"))
    public void enchantedStatusEffect(ItemStack instance, Holder<Enchantment> enchantment, int level) {
        if(effectLevel != null) {
            int newEnchantmentLevel = min(level+effectLevel.get(), enchantment.value().getMaxLevel());
            instance.enchant(enchantment, newEnchantmentLevel);
        } else {
            instance.enchant(enchantment, level);
        }
    }

    //Grant Overcharged Advancement
    @Redirect(method = "lambda$clickMenuButton$0(Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/world/entity/player/Player;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/triggers/EnchantedItemTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/ItemStack;I)V"))
    public void grantOverchargedAdvancement(EnchantedItemTrigger instance, ServerPlayer player, ItemStack stack, int levels) {

        if(MaxEnchantingSlots.getEnchantType(stack) != null) {
            if(MaxEnchantingSlots.getCurrent(stack) > MaxEnchantingSlots.getEnchantType(stack).getMaxEnchantingSlots()) {
                Indexed.OVERCHARGE_ITEM.trigger(player);
            }
        }

        instance.trigger(player, stack, levels);

    }
}
