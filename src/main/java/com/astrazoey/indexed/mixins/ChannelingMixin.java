package com.astrazoey.indexed.mixins;


import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.SummonEntityEffect;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SummonEntityEffect.class)
public class ChannelingMixin {

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LightningBolt;setCause(Lnet/minecraft/server/level/ServerPlayer;)V", shift = At.Shift.AFTER))
    protected void onEntityHit(ServerLevel world, int level, EnchantedItemInUse context, Entity user, Vec3 pos, CallbackInfo ci) {


        if(level > 1) {
            BlockPos blockPos = BlockPos.containing(pos);


            for(int i = 2; i <= (level-1)*2; i++) {

                BlockPos randPos = blockPos;
                int blockOffset = (int) (level*1.5f);

                int addRandX = (int) (Math.random() * (blockOffset + blockOffset)) - blockOffset;
                int addRandZ = (int) (Math.random() * (blockOffset + blockOffset)) - blockOffset;

                randPos = randPos.offset(addRandX, 0, addRandZ);

                LightningBolt lightningEntity = ((EntityType<LightningBolt>) (Object) BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.withDefaultNamespace("lightning_bolt"))).spawn(world, blockPos, EntitySpawnReason.TRIGGERED);
                if (lightningEntity != null) {
                    lightningEntity.snapTo(Vec3.atBottomCenterOf(randPos));
                    lightningEntity.setCause((ServerPlayer)context.owner());
                }
            }

        }


    }

}
