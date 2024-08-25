package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.ConfigMain;
import com.astrazoey.indexed.Indexed;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin extends Item {


    public CrossbowItemMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    public static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated) {
    }

    @Shadow public abstract Predicate<ItemStack> getProjectiles();

    @Shadow
    protected static List<ItemStack> getProjectiles(ItemStack crossbow) {
        return null;
    }

    @Shadow
    protected static void clearProjectiles(ItemStack crossbow) {
    }

    @Shadow
    protected static void putProjectile(ItemStack crossbow, ItemStack projectile) {
    }

    private static ItemStack mixedProjectile;

    @Inject(method="loadProjectiles", at = @At(value = "HEAD"))
    private static void getProjectile(LivingEntity shooter, ItemStack projectile, CallbackInfoReturnable<Boolean> cir) {
        mixedProjectile = projectile;
    }


    @ModifyConstant(method="loadProjectiles", constant = @Constant(intValue = 3))
    private static int adjustProjectileCount(int value) {
        return 3 + (((EnchantmentHelper.getLevel(Enchantments.MULTISHOT, mixedProjectile))-1)*2);

    }

    @Inject(method="shootAll", at = @At(value="INVOKE", target = "Lnet/minecraft/item/CrossbowItem;shoot(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;FZFFF)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void addProjectiles(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence, CallbackInfo ci, List list, float fs[], int i, ItemStack itemStack, boolean bl) {

        int maxCounter = (((EnchantmentHelper.getLevel(Enchantments.MULTISHOT, stack))-1)*2);


        for(int counter = 0; counter < maxCounter; counter++){
            if(counter == 0) {
                shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, 5.0F);
            }
            if(counter == 1) {
                shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, -5.0F);
            }
            if(counter == 2) {
                shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed/1.1f, divergence, 7.5F);
            }
            if(counter == 3) {
                shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed/1.1f, divergence, -7.5F);
            }
            if(counter == 4) {
                shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed/1.1f, divergence, 2.5F);
            }
            if(counter == 5) {
                shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed/1.1f, divergence, -2.5F);
            }
            if(counter == 6) {
                shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed/1.3f, divergence, 12.5F);
            }
            if(counter == 7) {
                shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed/1.3f, divergence, -12.5F);
            }
        }

        if(EnchantmentHelper.getLevel(Enchantments.MULTISHOT, stack) >= 5 && entity instanceof ServerPlayerEntity) {
            //Indexed.MULTISHOT_CROSSBOW.trigger((ServerPlayerEntity) entity);
        }

    }



    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.STRING);
    }

    @ModifyConstant(method="getPullTime", constant = @Constant(intValue = 5, ordinal = 0))
    private static int changeQuickChargeTime(int timeFactor) {
        if(ConfigMain.enableEnchantmentNerfs) {
            return 4;
        } else {
            return 5;
        }
    }


}
