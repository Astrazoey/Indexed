package com.astrazoey.indexed.mixins;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public class RiptideMixin extends Item {

    public ThreadLocal<ItemStack> tridentItem = new ThreadLocal<ItemStack>();
    public ThreadLocal<LivingEntity> tridentOwner = new ThreadLocal<LivingEntity>();

    public RiptideMixin(Settings settings) {
        super(settings);
    }

    @Inject(method="use", at = @At(value="RETURN", ordinal = 1), cancellable = true)
    public void allowRiptideUsage(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack itemStack = user.getStackInHand(hand);
        if(EnchantmentHelper.getRiptide(itemStack) > 0) {
            user.setCurrentHand(hand);
            cir.setReturnValue(TypedActionResult.consume(itemStack));
        }
    }

    @Redirect(method="onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    public boolean allowRiptideToFireWithoutWater(PlayerEntity playerEntity) {
        return true;
    }

    @Inject(method="onStoppedUsing", at = @At(value = "HEAD"))
    public void getVariables(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        tridentItem.set(stack);
        tridentOwner.set(user);
    }

    @ModifyConstant(method="onStoppedUsing", constant = @Constant(expandZeroConditions = {Constant.Condition.GREATER_THAN_ZERO}))
    public int denyRiptideEffectIfDry(int constant) {
        if(tridentOwner.get().isTouchingWaterOrRain()) {
            return 0;
        } else {
            return 100;
        }
    }

    @Inject(method="onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"), cancellable = true)
    public void useRiptideIfDry(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        PlayerEntity playerEntity = (PlayerEntity)user;

        int riptideLevel = EnchantmentHelper.getRiptide(stack);


        if (riptideLevel > 0 && !playerEntity.isTouchingWaterOrRain()) {
            TridentEntity tridentEntity = new TridentEntity(world, playerEntity, stack);
            tridentEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 2.5F + (float)riptideLevel * 0.25F, 1.0F);
            if (playerEntity.getAbilities().creativeMode) {
                tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }

            world.spawnEntity(tridentEntity);
            world.playSoundFromEntity((PlayerEntity)null, tridentEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
            if (!playerEntity.getAbilities().creativeMode) {
                playerEntity.getInventory().removeOne(stack);
            }

            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            System.out.println("Method cancelled");
            ci.cancel();
        }
    }
}

@Mixin(LivingEntity.class)
class LivingEntityMixin {

    //Allow channeling and riptide compat
    @Inject(method="tickRiptide", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;attackLivingEntity(Lnet/minecraft/entity/LivingEntity;)V"))
    public void applyLightningEffectToRiptide(Box a, Box b, CallbackInfo ci) {

        Entity entity = (Entity) (Object) this;

        if(EnchantmentHelper.getEquipmentLevel(Enchantments.CHANNELING, (LivingEntity) (Object) this) > 0) {
            if (((LivingEntity) (Object) this).getWorld().isSkyVisible(((LivingEntity) (Object) this).getBlockPos())) {
                LightningEntity lightningEntity = (LightningEntity) EntityType.LIGHTNING_BOLT.create(((LivingEntity) (Object) this).getWorld());
                assert lightningEntity != null;
                lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(((LivingEntity) (Object) this).getBlockPos()));
                lightningEntity.setChanneler(entity instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity : null);
                ((LivingEntity) (Object) this).getWorld().spawnEntity(lightningEntity);
                SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
                ((LivingEntity) (Object) this).playSound(soundEvent, 5.0F, 1.0F);
            }

        }
    }

}