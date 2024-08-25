package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.ConfigMain;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.registry.tag.FluidTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
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


    //Higher levels of silk touch
    @Inject(method = "canHarvest", at = @At(value = "HEAD"), cancellable = true)
    public void canHarvest(BlockState state, CallbackInfoReturnable<Boolean> cir) {

        System.out.println("can harvest has run");

        ItemStack itemStack = ((PlayerEntity) (Object) this).getInventory().getMainHandStack();
        Item item = itemStack.getItem();
        int miningLevel = -1;

        int silkTouchLevel = EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack);

        if(silkTouchLevel > 1) {
            System.out.println("item has silk touch");
            if(item instanceof ToolItem) {

                System.out.println("mining level is before math: " + miningLevel);

                miningLevel = ((ToolItem) item).getMaterial().getMiningLevel() + silkTouchLevel-1;

                System.out.println("mining level is after math: " + miningLevel);

                if(miningLevel >= MiningLevelManager.getRequiredMiningLevel(state)) {
                    System.out.println("setting new return value");
                    cir.setReturnValue(true);
                }

            }
        }
    }



}
