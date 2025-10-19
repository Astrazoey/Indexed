package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Indexed;
import com.astrazoey.indexed.IndexedClassHelper;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(targets = {"net/minecraft/screen/GrindstoneScreenHandler"}, priority = 999)
public class GrindstoneScreenHandlerMixin {

        @Inject(method = "grind", at = @At(value = "HEAD"))
        public void storeItem(ItemStack item, CallbackInfoReturnable<ItemStack> cir) {



            //System.out.println("grinding item" + item);

            var essence = EnchantmentHelper.getEffect(item, Indexed.ESSENCE);



            if(essence.isPresent()) {
                System.out.println("stored item with essence");

                //IndexedClassHelper.booleanThreadLocal.set(true);
            } else {
                IndexedClassHelper.booleanThreadLocal.set(false);
            }

        }

}

@Mixin(targets = {"net/minecraft/screen/GrindstoneScreenHandler$4"}, priority = 999)
class GrindstoneTakeOutput {

    @Inject(method = "onTakeItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V", at = @At(value="TAIL"))
    public void grantAdvancement(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if(IndexedClassHelper.booleanThreadLocal.get()) {
            if(player instanceof ServerPlayerEntity) {
                Indexed.GRIND_ESSENCE.trigger((ServerPlayerEntity) player);
            }
        }

    }

}


