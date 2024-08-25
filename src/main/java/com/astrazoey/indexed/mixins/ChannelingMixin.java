package com.astrazoey.indexed.mixins;


import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public class ChannelingMixin extends PersistentProjectileEntity {

    protected ChannelingMixin(EntityType<? extends PersistentProjectileEntity> type, World world, ItemStack stack) {
        super(type, world, stack);
    }

    @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    protected void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {

        System.out.println("trident injection successful");

        Entity entity2 = this.getOwner();
        Entity entity = entityHitResult.getEntity();


        int ChannelingLevel = EnchantmentHelper.getLevel(Enchantments.CHANNELING, this.getItemStack());

        if(ChannelingLevel > 1) {

            System.out.println("higher channeling level detected");

            BlockPos blockPos = entityHitResult.getEntity().getBlockPos();


            for(int i = 2; i <= (ChannelingLevel-1)*2; i++) {

                System.out.println("spawning bolt" + i);

                BlockPos randPos = blockPos;
                int blockOffset = (int) (ChannelingLevel*1.5f);

                int addRandX = (int) (Math.random() * (blockOffset - -blockOffset)) + -blockOffset;
                int addRandZ = (int) (Math.random() * (blockOffset - -blockOffset)) + -blockOffset;

                System.out.println("X = " + addRandX + ", Z = " + addRandZ);

                randPos = randPos.add(addRandX, 0, addRandZ);

                System.out.println(randPos);

                LightningEntity lightningEntity = (LightningEntity)EntityType.LIGHTNING_BOLT.create(this.getWorld());
                if (lightningEntity != null) {
                    lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(randPos));
                    lightningEntity.setChanneler(entity2 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity2 : null);
                    this.getWorld().spawnEntity(lightningEntity);
                }
            }

        }


    }

}
