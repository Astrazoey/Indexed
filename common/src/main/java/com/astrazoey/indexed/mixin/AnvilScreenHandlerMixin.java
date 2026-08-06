package com.astrazoey.indexed.mixin;

import com.astrazoey.indexed.CommonClass;
import com.astrazoey.indexed.MaxEnchantingSlots;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// targets two names because of fabric and neoforge differences
@Mixin(value = AnvilMenu.class, priority = 999)
public abstract class AnvilScreenHandlerMixin extends ItemCombinerMenu {

    @Unique
    ItemStack itemStack1;
    @Unique
    ItemStack itemStack3;

    @Unique
    int enchantLevel2;

    @Unique
    boolean overcharged = false;

    @Shadow
    private int repairItemCountCost;

    @Shadow
    @Final
    private DataSlot cost;

    //minimum amount of materials to repair an unenchanted tool
    @Unique
    int repairCost = 1;
    //how much repairing scales with number of enchantments. higher values = higher costs for more enchanted items
    @Unique
    int repairScaling = 6;

    public AnvilScreenHandlerMixin(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context, ItemCombinerMenuSlotDefinition forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    //Get the items inside the anvil
    @Redirect(method = {"createResult()V", "createResultInternal()V"}, at = @At(value="INVOKE", target = "Lnet/minecraft/world/Container;getItem(I)Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
    public ItemStack getItemStack1(Container inventory, int slot) {
        itemStack1 = inventory.getItem(0);
        return itemStack1;
    }

    @Redirect(method = {"createResult()V", "createResultInternal()V"}, at = @At(value="INVOKE", target = "Lnet/minecraft/world/Container;getItem(I)Lnet/minecraft/world/item/ItemStack;", ordinal = 1))
    public ItemStack getItemStack3(Container inventory, int slot) {
        itemStack3 = inventory.getItem(1);
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
            enchantingFactor = enchantingFactor - 3 * MaxEnchantingSlots.getEnchantType(itemStack1).getRepairScaling();
            if(enchantingFactor < 0) {
                enchantingFactor = 0;
            }

            return (repairCost + (int) enchantingFactor);
        } else {
            return 4; //the default value
        }
    }

    @ModifyConstant(method = {"createResult()V", "createResultInternal()V"}, constant = @Constant(intValue = 4, ordinal = 0))
    public int increaseRepairCost(int cost) {
        return calculateRepairCost();
    }

    @ModifyConstant(method = {"createResult()V", "createResultInternal()V"}, constant = @Constant(intValue = 4, ordinal = 1))
    public int increaseRepairCost2(int cost) {
        return calculateRepairCost();
    }


    // Allow enchantments to add linearly
    @Redirect(method = {"createResult()V", "createResultInternal()V"}, at = @At(value="INVOKE", target = "Ljava/lang/Math;max(II)I", ordinal = 0))
    public int linearEnchantment(int a, int b) {
        return a + b;
    }

    @Redirect(method = {"createResult()V", "createResultInternal()V"}, at = @At(value="INVOKE", target = "Lnet/minecraft/world/item/enchantment/ItemEnchantments$Mutable;getLevel(Lnet/minecraft/core/Holder;)I"))
    public int getQ(ItemEnchantments.Mutable instance, Holder<Enchantment> enchantment) {
        enchantLevel2 = instance.getLevel(enchantment);
        return enchantLevel2;
    }

    @ModifyConstant(method = {"createResult()V", "createResultInternal()V"}, constant = @Constant(intValue = 1, ordinal = 2))
    public int linearEnchantmentSameValue(int q) {
        return enchantLevel2;
    }

    //Allow items to be used in the anvil for free
    @ModifyConstant(method = {"createResult()V", "createResultInternal()V"}, constant = @Constant(expandZeroConditions = {Constant.Condition.LESS_THAN_OR_EQUAL_TO_ZERO}, ordinal = 2))
    public int allowAnyCost(int i) {
        if(itemStack3.isEmpty()) {
            return 0;
        } else {
            return -1;
        }
    }

    @ModifyConstant(method="mayPickup", constant = @Constant(expandZeroConditions = {Constant.Condition.GREATER_THAN_ZERO}))
    public int allowAnyCostForOutput(int cost) {
        if(overcharged) {
            return 50000; //prevent taking out overcharged items
        }
        return -1;
    }



    // stop combining items if max enchanting slots are exceeded
    @Redirect(method = {"createResult()V", "createResultInternal()V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 3))
    public void denyExpensiveTransactions(ResultContainer craftingResultInventory, int slot, ItemStack stack) {
        //checks if the enchanting hasn't exceeded itself
        if(MaxEnchantingSlots.getEnchantType(stack) != null) {
            if (MaxEnchantingSlots.getCurrent(stack) > MaxEnchantingSlots.getEnchantType(stack).getMaxEnchantingSlots()) {
                overcharged = true;
                craftingResultInventory.setItem(slot, stack);
            } else {
                overcharged = false;
                craftingResultInventory.setItem(slot, stack);
            }
        } else {
            craftingResultInventory.setItem(slot, stack);
        }
    }

    //Remove "Too Expensive!" stuff by keeping the repair cost of the item under 31.
    //Make lapis enchanting free
    @Redirect(method = {"createResult()V", "createResultInternal()V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1))
    public <T> Object removeTooExpensiveLimit(ItemStack itemStack, DataComponentType<T> type, T repairCost) {

        ItemStack left = this.inputSlots.getItem(0);
        ItemStack right = this.inputSlots.getItem(1);

        if (right.getItem() != Items.ENCHANTED_BOOK && right.getItem() != left.getItem()) {
            // Rename or material repair (lapis) -> free
            this.cost.set(0);
        } else {
            // Enchanted book or item combine -> cap at 30
            this.cost.set(Math.min(this.cost.get(), 30));
        }


        itemStack.set(DataComponents.REPAIR_COST, Math.min((int)repairCost, 30));
        return null;
    }

    @Inject(method="onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;getItem(I)Lnet/minecraft/world/item/ItemStack;"))
    public void grantRepairAdvancement(Player player, ItemStack stack, CallbackInfo ci) {
        if(player instanceof ServerPlayer) {
            // Only grant when taking a repaired item where lapis was consumed as the repair material.
            if (repairItemCountCost > 0) {
                CommonClass.REPAIR_ITEM.trigger((ServerPlayer) player);
            }
        }
    }

    //Use universal repair item
    @Redirect(method = {"createResult()V", "createResultInternal()V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isValidRepairItem(Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean repairable(ItemStack stack, ItemStack ingredient) {
        return stack.isDamageableItem() && ingredient.is(Items.LAPIS_LAZULI);
    }

}
