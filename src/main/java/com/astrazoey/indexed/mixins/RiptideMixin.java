package com.astrazoey.indexed.mixins;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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
    public void allowRiptideUsage(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = user.getStackInHand(hand);
        if(EnchantmentHelper.getTridentSpinAttackStrength(itemStack, user) > 0) {
            user.setCurrentHand(hand);
            cir.setReturnValue(ActionResult.CONSUME);

        }
    }

    @Redirect(method="onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    public boolean allowRiptideToFireWithoutWater(PlayerEntity playerEntity) {
        return true;
    }

    @Inject(method="onStoppedUsing", at = @At(value = "HEAD"))
    public void getVariables(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfoReturnable<Boolean> cir) {
        tridentItem.set(stack);
        tridentOwner.set(user);
    }

    @ModifyConstant(method="onStoppedUsing", constant = @Constant(floatValue = 0.0f, ordinal = 0))
    public float denyRiptideEffectIfDryOrdinalZero(float constant) {
        if(tridentOwner.get().isTouchingWaterOrRain()) {
            return 0F;
        } else {
            return 100F;
        }
    }

    @ModifyConstant(method="onStoppedUsing", constant = @Constant(floatValue = 0.0f, ordinal = 3))
    public float denyRiptideEffectIfDryOrdinalThree(float constant) {
        if(tridentOwner.get().isTouchingWaterOrRain()) {
            return 0F;
        } else {
            return 100F;
        }
    }

    @Inject(method="onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/player/PlayerEntity;)V"), cancellable = true)
    public void useRiptideIfDry(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity playerEntity = (PlayerEntity)user;

        float riptideLevel = EnchantmentHelper.getTridentSpinAttackStrength(stack, user);


        if (riptideLevel > 0 && !playerEntity.isTouchingWaterOrRain()) {
            //System.out.println("Trident has riptide but isn't touching water!");
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));

            ItemStack itemStack = stack.splitUnlessCreative(1, playerEntity);
            TridentEntity tridentEntity = ProjectileEntity.spawnWithVelocity(TridentEntity::new, (ServerWorld)world, itemStack, playerEntity, 0.0F, riptideLevel * 0.25f + 2.5f, 1.0F);
            if (playerEntity.isInCreativeMode()) {
                tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }

            world.playSoundFromEntity((PlayerEntity)null, tridentEntity, SoundEvents.ITEM_TRIDENT_THROW.value(), SoundCategory.PLAYERS, 1.0F, 1.0F);
            //System.out.println("Method cancelled");
            cir.cancel();
        }
    }
}

@Mixin(LivingEntity.class)
class LivingEntityMixin {


    //Allow channeling and riptide compat
    @Inject(method="tickRiptide", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;attackLivingEntity(Lnet/minecraft/entity/LivingEntity;)V"))
    public void applyLightningEffectToRiptide(Box a, Box b, CallbackInfo ci) {

        Entity entity = (Entity) (Object) this;

        if(EnchantmentHelper.getEquipmentLevel(entity.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.CHANNELING), entity.getEntity()) > 0) {
            if (((LivingEntity) (Object) this).getEntityWorld().isSkyVisible(((LivingEntity) (Object) this).getBlockPos())) {
                LightningEntity lightningEntity = (LightningEntity) EntityType.LIGHTNING_BOLT.create(((LivingEntity) (Object) this).getEntityWorld(), SpawnReason.TRIGGERED);
                assert lightningEntity != null;
                lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(((LivingEntity) (Object) this).getBlockPos()));
                lightningEntity.setChanneler(entity instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity : null);
                ((LivingEntity) (Object) this).getEntityWorld().spawnEntity(lightningEntity);
                SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER.value();
                ((LivingEntity) (Object) this).playSound(soundEvent, 5.0F, 1.0F);
            }

        }
    }



}