package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.EnchantingType;
import com.astrazoey.indexed.Indexed;
import com.astrazoey.indexed.MaxEnchantingSlots;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin implements MaxEnchantingSlots {
    @Shadow public abstract Item asItem();

    public int usedEnchantingSlots = 0;
    public EnchantingType enchantingType;


    @Override
    public EnchantingType getEnchantingType(ItemStack stack) {
        return enchantingType;
    }

    @Override
    public void setEnchantingType(EnchantingType enchantingType) {
        this.enchantingType = enchantingType;
    }


    @Override
    public int getEnchantingSlots(ItemStack itemStack) {
        ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);

        int totalLevels = 0;

        for(var enchantmentEntry : itemEnchantments.entrySet()) {
            if(enchantmentEntry.getKey().is(EnchantmentTags.CURSE)) {
                totalLevels -= enchantmentEntry.getIntValue();
            } else {
                totalLevels += enchantmentEntry.getIntValue();
            }
        }

        usedEnchantingSlots = totalLevels;

        return totalLevels;
    }

}

@Mixin(ItemStack.class)
class ItemStackMixin {

    @Inject(method= "hurtEnemy", at = @At(value = "HEAD"))
    public void checkItemUse(LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> cir) {
        if(attacker instanceof ServerPlayer) {
            grantAdvancementOnUseWithGold(((ItemStack) (Object) this), (ServerPlayer) attacker);
        }
    }

    private void grantAdvancementOnUseWithGold(ItemStack stack, ServerPlayer user) {
        EnchantingType enchantingType = MaxEnchantingSlots.getEnchantType(stack);
        if (user == null) return;

        if(enchantingType != null && stack.is(Items.GOLDEN_SWORD)) {
            if(MaxEnchantingSlots.getCurrent(stack) >= enchantingType.getMaxEnchantingSlots()) {
                Indexed.MAX_GOLD.trigger((ServerPlayer) user);
            }
        }

        if(EnchantmentHelper.getItemEnchantmentLevel(user.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.KNOCKBACK),stack) >= 5) {
            Indexed.MAX_KNOCKBACK.trigger(user);
        }
    }
}