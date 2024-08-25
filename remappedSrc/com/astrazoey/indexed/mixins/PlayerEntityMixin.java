package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.ConfigMain;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "getBlockBreakingSpeed", at = @At(value="TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void modifyAquaSpeed(BlockState block, CallbackInfoReturnable<Float> cir, float f) {
        if(((PlayerEntity) (Object) this).isSubmergedIn(FluidTags.WATER) && EnchantmentHelper.hasAquaAffinity((PlayerEntity) (Object) this)) {
            f = f + ((EnchantmentHelper.getEquipmentLevel(Enchantments.AQUA_AFFINITY, (PlayerEntity) (Object) this) - 1)/2f);
            cir.setReturnValue(f);
        }
    }

    @ModifyConstant(method = "attack", constant = @Constant(floatValue = 0.5f, ordinal = 1))
    public float changeKnockbackAmount(float knockbackModifier) {
        if(EnchantmentHelper.getKnockback(((PlayerEntity) (Object) this)) > 0 && ConfigMain.enableEnchantmentNerfs) {
            return 0.35f;
        } else {
            return 0.5f;
        }
    }

    @ModifyConstant(method="getXpToDrop", constant = @Constant(intValue = 100, ordinal = -1))
    public int removeDeathXpCap(int oldCap) {
        return 10000;
    }


}
