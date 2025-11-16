package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.EnchantingType;
import com.astrazoey.indexed.Indexed;
import com.astrazoey.indexed.MaxEnchantingSlots;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.server.network.ServerPlayerEntity;
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
        ItemEnchantmentsComponent itemEnchantments = EnchantmentHelper.getEnchantments(itemStack);

        int totalLevels = 0;

        for(var enchantmentEntry : itemEnchantments.getEnchantmentEntries()) {
            if(enchantmentEntry.getKey().isIn(EnchantmentTags.CURSE)) {
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

    @Inject(method= "postHit", at = @At(value = "HEAD"))
    public void checkItemUse(LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> cir) {
        if(attacker instanceof ServerPlayerEntity) {
            grantAdvancementOnUseWithGold(((ItemStack) (Object) this), (ServerPlayerEntity) attacker);
        }
    }

    private void grantAdvancementOnUseWithGold(ItemStack stack, ServerPlayerEntity user) {
        EnchantingType enchantingType = MaxEnchantingSlots.getEnchantType(stack);
        if (user == null) return;

        if(enchantingType != null && stack.isOf(Items.GOLDEN_SWORD)) {
            if(MaxEnchantingSlots.getCurrent(stack) >= enchantingType.getMaxEnchantingSlots()) {
                Indexed.MAX_GOLD.trigger((ServerPlayerEntity) user);
            }
        }

        if(EnchantmentHelper.getLevel(user.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.KNOCKBACK),stack) >= 5) {
            Indexed.MAX_KNOCKBACK.trigger(user);
        }
    }
}