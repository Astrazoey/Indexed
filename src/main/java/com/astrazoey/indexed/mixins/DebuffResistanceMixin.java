package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Indexed;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(LivingEntity.class)
public abstract class DebuffResistanceMixin {


    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    /**
     * @author Astrazoey
     */
    @ModifyVariable(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"), argsOnly = true)
    private StatusEffectInstance modifyEffectDuration(StatusEffectInstance effect) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack chestplate = getEquippedStack(EquipmentSlot.CHEST);
        if(entity.getEntityWorld() instanceof ServerWorld) {
            int level = Indexed.getEnchantmentValue(Indexed.DEBUFF_REDUCTION, (ServerWorld) entity.getEntityWorld(), chestplate);
            level = level / 3; //fixes level coming back three times higher for some reason

            // System.out.println("LEVEL IS " + level);
            if(level > 0) {
                if(!effect.getEffectType().value().isBeneficial()) {

                    //System.out.println("Current duration = " + effect.getDuration() / 20);

                    int remainingDuration = effect.getDuration() - (level * 10 * 20);
                    if (remainingDuration < 0) {
                        remainingDuration = 0;
                    }

                    effect = new StatusEffectInstance(effect.getEffectType(), remainingDuration, effect.getAmplifier());

                    //System.out.println("Effect duration is " + effect.getDuration()/20 + " and amplifier is + " + effect.getAmplifier());

                }
            }
        }


        return effect;
    }


    @Shadow
    public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {return true;}

}
