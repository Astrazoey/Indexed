package com.astrazoey.indexed.mixins;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;



@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @ModifyConstant(method="getExperienceToDrop", constant = @Constant(intValue = 100, ordinal = -1))
    public int removeDeathXpCap(int oldCap) {
        return 10000;
    }



    //Higher levels of silk touch
    @Redirect(method = "canHarvest", at = @At(value = "INVOKE", target="Lnet/minecraft/item/ItemStack;isSuitableFor(Lnet/minecraft/block/BlockState;)Z"))
    public boolean canHarvest(ItemStack instance, BlockState state) {

        PlayerEntity player = ((PlayerEntity) (Object) this);

        ItemStack itemStack = player.getInventory().getSelectedStack();
        int silkTouchLevel = EnchantmentHelper.getLevel(player.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), itemStack);


        if(silkTouchLevel > 1) {
            ToolComponent toolComponent = itemStack.get(DataComponentTypes.TOOL);
            if(toolComponent != null) {

                BiMap<TagKey<Block>, Integer> miningLevelMap = HashBiMap.create();
                miningLevelMap.put(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 4);
                miningLevelMap.put(BlockTags.INCORRECT_FOR_IRON_TOOL, 3);
                miningLevelMap.put(BlockTags.INCORRECT_FOR_STONE_TOOL, 2);
                miningLevelMap.put(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1);
                int realMiningLevel = 0;
                for (var rule : toolComponent.rules()) {
                    if (rule.correctForDrops().isPresent() && !rule.correctForDrops().get()) {
                        realMiningLevel = Math.min(miningLevelMap.get(rule.blocks().getTagKey().get()) + silkTouchLevel, 4);
                    }
                }
                return !state.isIn(miningLevelMap.inverse().get(realMiningLevel));
            }
        }
        return itemStack.isSuitableFor(state);
    }
}
