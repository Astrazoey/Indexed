package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.SetFlameEnchantmentLevel;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class FlameMixin {
}

@Mixin(BowItem.class)
class FlameBowMixin extends Item implements SetFlameEnchantmentLevel {

    public int flameEnchantmentLevel = 0;

    public FlameBowMixin(Settings settings) {
        super(settings);
    }


    @Inject(method="onStoppedUsing", at = @At(value ="HEAD"))
    public void getFlameEnchantmentLevel(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        flameEnchantmentLevel = EnchantmentHelper.getLevel(Enchantments.FLAME, stack);
    }



    @Redirect(method="onStoppedUsing", at = @At(value = "INVOKE", target ="Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    public boolean setFlameEnchantment(World world, Entity entity) {

        if(flameEnchantmentLevel > 0) {
            SetFlameEnchantmentLevel.set((PersistentProjectileEntity) entity, flameEnchantmentLevel);
        }

        world.spawnEntity(entity);
        return true;
    }





    @Override
    public void setFlameEnchantmentLevel(int flameLevel) {

    }


}

@Mixin(PersistentProjectileEntity.class)
class FlameProjectileMixin implements SetFlameEnchantmentLevel {

    public int flameEnchantmentLevel = 0;

    @Inject(method="applyEnchantmentEffects", at = @At(value ="HEAD"))
    public void getFlameEnchantmentLevel(LivingEntity entity, float damageModifier, CallbackInfo ci) {
        flameEnchantmentLevel = EnchantmentHelper.getEquipmentLevel(Enchantments.FLAME, entity);
        System.out.println("flame enchantment level is " + flameEnchantmentLevel + " at head");
    }

    @Redirect(method="onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setOnFireFor(I)V"))
    public void adjustFlame(Entity entity, int seconds) {
        if(flameEnchantmentLevel > 0) {
            int newAmount = 5+((flameEnchantmentLevel-1)*3);
            entity.setOnFireFor(newAmount);
        }
    }

    @Override
    public void setFlameEnchantmentLevel(int flameLevel) {
        flameEnchantmentLevel = flameLevel;
        System.out.println("set flame level to " + flameLevel);
    }


}