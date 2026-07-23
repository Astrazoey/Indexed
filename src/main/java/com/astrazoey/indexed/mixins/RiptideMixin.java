package com.astrazoey.indexed.mixins;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public class RiptideMixin extends Item {

    public ThreadLocal<ItemStack> tridentItem = new ThreadLocal<ItemStack>();
    public ThreadLocal<LivingEntity> tridentOwner = new ThreadLocal<LivingEntity>();

    public RiptideMixin(Properties settings) {
        super(settings);
    }

    @Inject(method="use", at = @At(value="RETURN", ordinal = 1), cancellable = true)
    public void allowRiptideUsage(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = user.getItemInHand(hand);
        if(EnchantmentHelper.getTridentSpinAttackStrength(itemStack, user) > 0) {
            user.startUsingItem(hand);
            cir.setReturnValue(InteractionResult.CONSUME);

        }
    }

    @Redirect(method="releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isInWaterOrRain()Z"))
    public boolean allowRiptideToFireWithoutWater(Player playerEntity) {
        return true;
    }

    @Inject(method="releaseUsing", at = @At(value = "HEAD"))
    public void getVariables(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, CallbackInfoReturnable<Boolean> cir) {
        tridentItem.set(stack);
        tridentOwner.set(user);
    }

    @ModifyConstant(method="releaseUsing", constant = @Constant(floatValue = 0.0f, ordinal = 0))
    public float denyRiptideEffectIfDryOrdinalZero(float constant) {
        if(tridentOwner.get().isInWaterOrRain()) {
            return 0F;
        } else {
            return 100F;
        }
    }

    @ModifyConstant(method="releaseUsing", constant = @Constant(floatValue = 0.0f, ordinal = 3))
    public float denyRiptideEffectIfDryOrdinalThree(float constant) {
        if(tridentOwner.get().isInWaterOrRain()) {
            return 0F;
        } else {
            return 100F;
        }
    }

    @Inject(method="releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtWithoutBreaking(ILnet/minecraft/world/entity/player/Player;)V"), cancellable = true)
    public void useRiptideIfDry(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, CallbackInfoReturnable<Boolean> cir) {
        Player playerEntity = (Player)user;

        float riptideLevel = EnchantmentHelper.getTridentSpinAttackStrength(stack, user);


        if (riptideLevel > 0 && !playerEntity.isInWaterOrRain()) {
            //System.out.println("Trident has riptide but isn't touching water!");
            playerEntity.awardStat(Stats.ITEM_USED.get(this));

            ItemStack itemStack = stack.consumeAndReturn(1, playerEntity);
            ThrownTrident tridentEntity = Projectile.spawnProjectileFromRotation(ThrownTrident::new, (ServerLevel)world, itemStack, playerEntity, 0.0F, riptideLevel * 0.25f + 2.5f, 1.0F);
            if (playerEntity.hasInfiniteMaterials()) {
                tridentEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }

            world.playSound((Player)null, tridentEntity, SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
            //System.out.println("Method cancelled");
            cir.cancel();
        }
    }
}

@Mixin(LivingEntity.class)
class LivingEntityMixin {


    //Allow channeling and riptide compat
    @Inject(method="checkAutoSpinAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;doAutoAttackOnTouch(Lnet/minecraft/world/entity/LivingEntity;)V"))
    public void applyLightningEffectToRiptide(AABB a, AABB b, CallbackInfo ci) {

        Entity entity = (Entity) (Object) this;

        if(EnchantmentHelper.getEnchantmentLevel(entity.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.CHANNELING), entity.asLivingEntity()) > 0) {
            if (((LivingEntity) (Object) this).level().canSeeSky(((LivingEntity) (Object) this).blockPosition())) {
                LightningBolt lightningEntity = ((EntityType<LightningBolt>) (Object) BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.withDefaultNamespace("lightning_bolt"))).create(((LivingEntity) (Object) this).level(), EntitySpawnReason.TRIGGERED);
                assert lightningEntity != null;
                lightningEntity.snapTo(Vec3.atBottomCenterOf(((LivingEntity) (Object) this).blockPosition()));
                lightningEntity.setCause(entity instanceof ServerPlayer ? (ServerPlayer)entity : null);
                ((LivingEntity) (Object) this).level().addFreshEntity(lightningEntity);
                SoundEvent soundEvent = SoundEvents.TRIDENT_THUNDER.value();
                ((LivingEntity) (Object) this).playSound(soundEvent, 5.0F, 1.0F);
            }

        }
    }



}
