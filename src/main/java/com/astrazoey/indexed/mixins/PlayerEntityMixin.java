package com.astrazoey.indexed.mixins;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;



@Mixin(Player.class)
public class PlayerEntityMixin {

    @ModifyConstant(method="getBaseExperienceReward", constant = @Constant(intValue = 100, ordinal = -1))
    public int removeDeathXpCap(int oldCap) {
        return 10000;
    }



    //Higher levels of silk touch
    @Redirect(method = "hasCorrectToolForDrops", at = @At(value = "INVOKE", target="Lnet/minecraft/world/item/ItemStack;isCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public boolean canHarvest(ItemStack instance, BlockState state) {

        Player player = ((Player) (Object) this);

        ItemStack itemStack = player.getInventory().getSelectedItem();
        int silkTouchLevel = EnchantmentHelper.getItemEnchantmentLevel(player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), itemStack);


        if(silkTouchLevel > 1) {
            Tool toolComponent = itemStack.get(DataComponents.TOOL);
            if(toolComponent != null) {

                BiMap<TagKey<Block>, Integer> miningLevelMap = HashBiMap.create();
                miningLevelMap.put(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 4);
                miningLevelMap.put(BlockTags.INCORRECT_FOR_IRON_TOOL, 3);
                miningLevelMap.put(BlockTags.INCORRECT_FOR_STONE_TOOL, 2);
                miningLevelMap.put(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1);
                int realMiningLevel = 0;
                for (var rule : toolComponent.rules()) {
                    if (rule.correctForDrops().isPresent() && !rule.correctForDrops().get()) {
                        realMiningLevel = Math.min(miningLevelMap.get(rule.blocks().unwrapKey().get()) + silkTouchLevel, 4);
                    }
                }
                return !state.is(miningLevelMap.inverse().get(realMiningLevel));
            }
        }
        return itemStack.isCorrectToolForDrops(state);
    }
}
