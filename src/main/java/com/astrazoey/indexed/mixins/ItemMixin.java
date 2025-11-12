package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.EnchantingType;
import com.astrazoey.indexed.EnchantingTypes;
import com.astrazoey.indexed.Indexed;
import com.astrazoey.indexed.MaxEnchantingSlots;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
//import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

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

    @Inject(method="getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/component/type/TooltipDisplayComponent;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void appendTooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir, TooltipDisplayComponent tooltipDisplayComponent, List list) {
        //if not an enchanted book
        if(((ItemStack) (Object) this).getItem() != Items.ENCHANTED_BOOK) {
            //If enchantable, add text.
            EnchantingType enchantingType = MaxEnchantingSlots.getEnchantType(((ItemStack) (Object) this));
            if(enchantingType != null) {
                MutableText mutableText;
                Formatting formatting;

                if(MaxEnchantingSlots.getCurrent(((ItemStack) (Object) this)) <= enchantingType.getMaxEnchantingSlots()) {
                    formatting = Formatting.BLUE;
                } else {
                    formatting = Formatting.RED;
                }

                mutableText = (Text.translatable("item.indexed.enchantment_tooltip", MaxEnchantingSlots.getCurrent((ItemStack) (Object) this), MaxEnchantingSlots.getEnchantType((ItemStack) (Object) this).getMaxEnchantingSlots())).formatted(formatting);

                list.add(mutableText);
            }

            if(EnchantmentHelper.hasAnyEnchantmentsWith((ItemStack) (Object) this, Indexed.HIDE_ENCHANTMENTS)) {
                list.add(Text.translatable("enchantment.indexed.mystery_tooltip").formatted(Formatting.OBFUSCATED, Formatting.RED));
            }

        }
    }

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