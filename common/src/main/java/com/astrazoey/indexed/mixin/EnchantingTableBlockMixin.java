package com.astrazoey.indexed.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EnchantingTableBlock;

// Allows enchanting tables to see bookshelves from further away
@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {

    @Mutable
    @Shadow @Final
    public static List<BlockPos> BOOKSHELF_OFFSETS;

    @Inject(method="<clinit>", at = @At(value = "TAIL"))
    private static void changePowerProvider(CallbackInfo ci) {
        BOOKSHELF_OFFSETS = BlockPos.betweenClosedStream(-3, 0, -3, 3, 2, 3).map(BlockPos::immutable).toList();
    }


}
