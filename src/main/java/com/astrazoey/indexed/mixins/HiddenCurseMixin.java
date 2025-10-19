package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Indexed;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class HiddenCurseMixin {

}

@Mixin(ArmorFeatureRenderer.class)
class ArmorFeatureRendererMixin {
    @Inject(method ="renderArmor", at = @At(value="HEAD"), cancellable = true)
    public void applyHiddenCurseEffect(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, ItemStack stack, EquipmentSlot slot, int light, BipedEntityRenderState bipedEntityRenderState, CallbackInfo ci) {
        if (EnchantmentHelper.hasAnyEnchantmentsWith(stack, Indexed.HIDE_ARMOR)) {
            ci.cancel();
        }
    }

}

@Mixin(ElytraFeatureRenderer.class)
class ElytraFeatureRendererMixin {


    @Inject(method ="render", at = @At(value="HEAD"), cancellable = true)
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, BipedEntityRenderState bipedEntityRenderState, float f, float g, CallbackInfo ci) {
        ItemStack itemStack = bipedEntityRenderState.equippedChestStack;
        if (EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, Indexed.HIDE_ARMOR)) {
            ci.cancel();
        }
    }
}
@Mixin(ItemModelManager.class)
class ItemRendererMixin {

    private static ThreadLocal<ItemDisplayContext> renderMode = new ThreadLocal<ItemDisplayContext>();


    @Inject(method = "clearAndUpdate", at = @At(value = "HEAD"), cancellable = true)
    private void checkForHiddenCurse(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, World world, HeldItemContext heldItemContext, int seed, CallbackInfo ci) {
        if((EnchantmentHelper.hasAnyEnchantmentsWith(stack, Indexed.HIDE_ARMOR) && (displayContext != ItemDisplayContext.GROUND) && (displayContext != ItemDisplayContext.GUI) && (displayContext != ItemDisplayContext.FIXED))) {
            ci.cancel();
        }
    }


}
