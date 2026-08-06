package com.astrazoey.indexed.neoforge.client;

import com.astrazoey.indexed.EnchantingType;
import com.astrazoey.indexed.MaxEnchantingSlots;
import com.astrazoey.indexed.neoforge.client.particles.CrystalHarvestParticle;
import com.astrazoey.indexed.registry.IndexedParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * Client-only NeoForge handlers. Only referenced from the client-side guard in
 * IndexedNeoForge so these classes are never loaded on a dedicated server.
 */
public final class IndexedNeoForgeClient {
    private IndexedNeoForgeClient() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(IndexedNeoForgeClient::registerParticleProviders);
        NeoForge.EVENT_BUS.addListener(IndexedNeoForgeClient::onItemTooltip);
    }

    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(IndexedParticles.CRYSTAL_HARVEST, CrystalHarvestParticle.Factory::new);
        event.registerSpriteSet(IndexedParticles.CRYSTAL_BREAK, CrystalHarvestParticle.Factory::new);
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        // Not for enchanted books
        if (stack.is(Items.ENCHANTED_BOOK)) {
            return;
        }

        EnchantingType enchantingType = MaxEnchantingSlots.getEnchantType(stack);
        if (enchantingType == null) {
            return;
        }

        int current = MaxEnchantingSlots.getCurrent(stack);
        int max = NeoForgeEnchantingConfigHolder.getMaxEnchantingSlots(stack.getItem(), enchantingType.getMaxEnchantingSlots());
        ChatFormatting formatting = current <= max ? ChatFormatting.BLUE : ChatFormatting.RED;

        event.getToolTip().add(Component.translatable("item.indexed.enchantment_tooltip", current, max).withStyle(formatting));
    }
}
