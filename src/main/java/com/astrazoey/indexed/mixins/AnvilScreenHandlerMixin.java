package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Indexed;
import com.astrazoey.indexed.MaxEnchantingSlots;
import com.astrazoey.indexed.registry.IndexedItems;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AnvilScreenHandler.class, priority = 999)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Unique
    ItemStack itemStack1;
    @Unique
    ItemStack itemStack3;

    @Unique
    int enchantLevel2;

    @Unique
    boolean overcharged = false;

    //minimum amount of materials to repair an unenchanted tool
    @Unique
    int repairCost = 1;
    //how much repairing scales with number of enchantments. higher values = higher costs for more enchanted items
    @Unique
    int repairScaling = 6;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    //Get the items inside the anvil
    @Redirect(method="updateResult", at = @At(value="INVOKE", target = "Lnet/minecraft/inventory/Inventory;getStack(I)Lnet/minecraft/item/ItemStack;", ordinal = 0))
    public ItemStack getItemStack1(Inventory inventory, int slot) {
        itemStack1 = inventory.getStack(0);
        return itemStack1;
    }

    @Redirect(method="updateResult", at = @At(value="INVOKE", target = "Lnet/minecraft/inventory/Inventory;getStack(I)Lnet/minecraft/item/ItemStack;", ordinal = 1))
    public ItemStack getItemStack3(Inventory inventory, int slot) {
        itemStack3 = inventory.getStack(1);
        return itemStack3;
    }



    //Change the amount of materials required for repair
    @Unique
    public int calculateRepairCost() {
        if(MaxEnchantingSlots.getEnchantType(itemStack1) != null) {
            float enchantingRatio = (float) MaxEnchantingSlots.getCurrent(itemStack1) / (float) MaxEnchantingSlots.getEnchantType(itemStack1).getMaxEnchantingSlots();
            float enchantingFactor = enchantingRatio * repairScaling;
            enchantingFactor = Math.round(enchantingFactor);
            enchantingFactor = enchantingFactor * MaxEnchantingSlots.getEnchantType(itemStack1).getRepairScaling();

            //Removes repair cost if forgery is enabled
            enchantingFactor = enchantingFactor - Indexed.getEnchantmentValue(Indexed.REDUCE_REPAIR_COST, player.getEntityWorld(), itemStack1) * 3 * MaxEnchantingSlots.getEnchantType(itemStack1).getRepairScaling();
            if(enchantingFactor < 0) {
                enchantingFactor = 0;
            }

            return (repairCost + (int) enchantingFactor);
        } else {
            return 4; //the default value
        }
    }

    @ModifyConstant(method= "updateResult", constant = @Constant(intValue = 4, ordinal = 0))
    public int increaseRepairCost(int cost) {
        return calculateRepairCost();
    }

    @ModifyConstant(method= "updateResult", constant = @Constant(intValue = 4, ordinal = 1))
    public int increaseRepairCost2(int cost) {
        return calculateRepairCost();
    }


    // Allow enchantments to add linearly
    @Redirect(method = "updateResult", at = @At(value="INVOKE", target = "Ljava/lang/Math;max(II)I", ordinal = 0))
    public int linearEnchantment(int a, int b) {
        return a + b;
    }

    @Redirect(method = "updateResult", at = @At(value="INVOKE", target = "Lnet/minecraft/component/type/ItemEnchantmentsComponent$Builder;getLevel(Lnet/minecraft/registry/entry/RegistryEntry;)I"))
    public int getQ(ItemEnchantmentsComponent.Builder instance, RegistryEntry<Enchantment> enchantment) {
        enchantLevel2 = instance.getLevel(enchantment);
        return enchantLevel2;
    }

    @ModifyConstant(method = "updateResult", constant = @Constant(intValue = 1, ordinal = 2))
    public int linearEnchantmentSameValue(int q) {
        return enchantLevel2;
    }

    //Allow items to be used in the anvil for free
    @ModifyConstant(method= "updateResult", constant = @Constant(expandZeroConditions = {Constant.Condition.LESS_THAN_OR_EQUAL_TO_ZERO}, ordinal = 2))
    public int allowAnyCost(int i) {
        if(itemStack3.isEmpty()) {
            return 0;
        } else {
            return -1;
        }
    }

    @ModifyConstant(method="canTakeOutput", constant = @Constant(expandZeroConditions = {Constant.Condition.GREATER_THAN_ZERO}))
    public int allowAnyCostForOutput(int cost) {
        if(overcharged) {
            return 50000; //prevent taking out overcharged items
        }
        return -1;
    }



    @Redirect(method="updateResult", at = @At(value="INVOKE", target="Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal=4))
    public void denyExpensiveTransactions(CraftingResultInventory craftingResultInventory, int slot, ItemStack stack) {
        //checks if the enchanting hasn't exceeded itself
        if(MaxEnchantingSlots.getEnchantType(stack) != null) {
            if (MaxEnchantingSlots.getCurrent(stack) > MaxEnchantingSlots.getEnchantType(stack).getMaxEnchantingSlots()) {
                overcharged = true;
                craftingResultInventory.setStack(slot, stack);
                //craftingResultInventory.setStack(slot, ItemStack.EMPTY);
            } else {
                overcharged = false;
                craftingResultInventory.setStack(slot, stack);
            }
        } else {
            craftingResultInventory.setStack(slot, stack);
        }
    }

    //Remove "Too Expensive!" stuff by keeping the repair cost of the item under 31.
    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;set(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1))
    public <T> Object removeTooExpensiveLimit(ItemStack itemStack, ComponentType<T> type, T repairCost) {
        itemStack.set(DataComponentTypes.REPAIR_COST, Math.min((int)repairCost, 30));
        return null;
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;set(I)V", ordinal = 5))
    public void freeRepairCost(Property property, int value) {
        if(itemStack3.getItem() != Items.ENCHANTED_BOOK && itemStack3.getItem() != itemStack1.getItem()) {
            property.set(0);
        } else {
            int maxRepairCost = 30 - Indexed.getEnchantmentValue(Indexed.REDUCE_REPAIR_COST, player.getEntityWorld(), itemStack1) * 5;
            property.set(Math.min(value, maxRepairCost));
        }
    }

    @Inject(method="onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;getStack(I)Lnet/minecraft/item/ItemStack;"))
    public void grantRepairAdvancement(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if(player instanceof ServerPlayerEntity) {
            Indexed.REPAIR_ITEM.trigger((ServerPlayerEntity) player);
        }
    }

    //Use universal repair item
    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;canRepairWith(Lnet/minecraft/item/ItemStack;)Z"))
    public boolean repairable(ItemStack stack, ItemStack ingredient) {
        return stack.isDamageable() && ingredient.isOf(IndexedItems.VITALIS);
    }



}

