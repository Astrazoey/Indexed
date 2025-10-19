package com.astrazoey.indexed.mixins;


import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.entity.SummonEntityEnchantmentEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SummonEntityEnchantmentEffect.class)
public class ChannelingMixin {

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LightningEntity;setChanneler(Lnet/minecraft/server/network/ServerPlayerEntity;)V", shift = At.Shift.AFTER))
    protected void onEntityHit(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos, CallbackInfo ci) {

        //System.out.println("trident injection successful");

        if(level > 1) {

            //System.out.println("higher channeling level detected");

            BlockPos blockPos = BlockPos.ofFloored(pos);


            for(int i = 2; i <= (level-1)*2; i++) {

                //System.out.println("spawning bolt" + i);

                BlockPos randPos = blockPos;
                int blockOffset = (int) (level*1.5f);

                int addRandX = (int) (Math.random() * (blockOffset - -blockOffset)) + -blockOffset;
                int addRandZ = (int) (Math.random() * (blockOffset - -blockOffset)) + -blockOffset;

                //System.out.println("X = " + addRandX + ", Z = " + addRandZ);

                randPos = randPos.add(addRandX, 0, addRandZ);

                //System.out.println(randPos);

                LightningEntity lightningEntity = (LightningEntity)EntityType.LIGHTNING_BOLT.spawn(world, blockPos, SpawnReason.TRIGGERED);
                if (lightningEntity != null) {
                    lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(randPos));
                    lightningEntity.setChanneler((ServerPlayerEntity)context.owner());
                }
            }

        }


    }

}
