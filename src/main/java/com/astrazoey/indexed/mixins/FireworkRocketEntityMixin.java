package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Indexed;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {

    @Shadow
    @Nullable
    private LivingEntity shooter;
    @Shadow
    private int lifeTime;

    private ThreadLocal<Boolean> lifeTimeUpdated = new ThreadLocal<Boolean>();


    @Redirect(method="tick", at = @At(value="INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
    public void modifyFireworkElytraSpeed(LivingEntity livingEntity, Vec3d velocity) {
        Vec3d vec3d = this.shooter.getRotationVector();
        Vec3d vec3d2 = this.shooter.getVelocity();

        double a = 0.1d;
        double b = 1.5d;
        double c = 0.5d;

        double slowBurnLevel = EnchantmentHelper.getEquipmentLevel(this.shooter.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Indexed.SLOW_BURN), this.shooter);
        double quickBurnLevel = EnchantmentHelper.getEquipmentLevel(this.shooter.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Indexed.QUICK_FLIGHT), this.shooter);
        //System.out.println("Slow burn level: " + slowBurnLevel);
        //System.out.println("Quick burn level: " + quickBurnLevel);

        if(slowBurnLevel > 0) {
            slowBurnLevel++;
            a = a / (slowBurnLevel/ (1.75d + (quickBurnLevel * 0.3d)));
            b = b / (slowBurnLevel/ (1.75d + (quickBurnLevel * 0.3d)));
            c = c / (slowBurnLevel/ (1.75d + (quickBurnLevel * 0.3d)));
        }


        if(quickBurnLevel > 0) {
            quickBurnLevel++;
            a = a * (quickBurnLevel/2.4d);
            b = b * (quickBurnLevel/2.4d);
            c = c * (quickBurnLevel/2.4d);

        }


        if(quickBurnLevel > 0 || slowBurnLevel > 0) {
            if(lifeTimeUpdated.get() == null) {
                if(slowBurnLevel > 0) {
                    lifeTime = (int) (lifeTime * ((slowBurnLevel+1) / 1.1d));
                }
                if(quickBurnLevel > 0) {
                    lifeTime = (int) (lifeTime / ((quickBurnLevel) / 1.1d));
                }

                lifeTimeUpdated.set(true);
                System.out.println("total life time is " + lifeTime);
            }
        }

        this.shooter.setVelocity(vec3d2.add(vec3d.x * a + (vec3d.x * b - vec3d2.x) * c, vec3d.y * a + (vec3d.y * b - vec3d2.y) * c, vec3d.z * a + (vec3d.z * b - vec3d2.z) * c));
    }
}
