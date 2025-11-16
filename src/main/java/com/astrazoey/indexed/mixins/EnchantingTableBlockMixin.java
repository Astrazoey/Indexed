package com.astrazoey.indexed.mixins;

import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {

    @Mutable
    @Shadow @Final
    public static List<BlockPos> POWER_PROVIDER_OFFSETS;

    @Inject(method="<clinit>", at = @At(value = "TAIL"))
    private static void changePowerProvider(CallbackInfo ci) {
        POWER_PROVIDER_OFFSETS = BlockPos.stream(-3, 0, -3, 3, 2, 3).map(BlockPos::toImmutable).toList();
    }


}
