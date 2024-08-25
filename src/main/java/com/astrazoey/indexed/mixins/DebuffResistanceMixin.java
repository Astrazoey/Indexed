package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Indexed;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class DebuffResistanceMixin {


    @Inject(method = "Lnet/minecraft/entity/LivingEntity;addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At("RETURN"), cancellable = true)
    private void check(StatusEffectInstance effect, @Nullable Entity source, CallbackInfoReturnable<Boolean> cir) {
        System.out.println("Last Check: Effect has a duration of " + effect.getDuration()/20);
    }

    //@ModifyVariable()

    /**
     * @author
     */
    @ModifyVariable(method = "Lnet/minecraft/entity/LivingEntity;addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"))
    private StatusEffectInstance modifyEffectDuration(StatusEffectInstance effect) {
        System.out.println("Effect applied");

        int level = EnchantmentHelper.getEquipmentLevel(Indexed.DEBUFF_RESISTANCE, (LivingEntity) (Object) this);

        if(level > 0) {
            if(!effect.getEffectType().isBeneficial()) {

                System.out.println("Current duration = " + effect.getDuration() / 20);

                int remainingDuration = effect.getDuration() - (level * 10 * 20);
                if (remainingDuration < 0) {
                    remainingDuration = 0;
                }

                effect = new StatusEffectInstance(effect.getEffectType(), remainingDuration, effect.getAmplifier());

                System.out.println("Effect duration is " + effect.getDuration()/20 + " and amplifier is + " + effect.getAmplifier());

            }
        }

        return effect;
    }


    @Shadow
    public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {return true;}

}
