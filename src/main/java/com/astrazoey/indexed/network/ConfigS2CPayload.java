package com.astrazoey.indexed.network;


import com.astrazoey.indexed.EnchantabilityConfig;
import com.astrazoey.indexed.Indexed;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ConfigS2CPayload(Map<String, EnchantabilityConfig> configList) implements CustomPacketPayload {
    public static final Identifier CONFIG_PAYLOAD_ID = Identifier.fromNamespaceAndPath(Indexed.MOD_ID, "config");
    public static final Type<ConfigS2CPayload> ID = new Type<>(CONFIG_PAYLOAD_ID);

    // Value codec for EnchantabilityConfig
    private static final StreamCodec<RegistryFriendlyByteBuf, EnchantabilityConfig> ENCHANT_CONFIG_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, EnchantabilityConfig::getMaxEnchantingSlots,
                    ByteBufCodecs.FLOAT,   EnchantabilityConfig::getRepairScaling,
                    (slots, scale) -> {
                        EnchantabilityConfig cfg = new EnchantabilityConfig(0, 0);
                        cfg.setMaxEnchantingSlots(slots);
                        cfg.setRepairScaling(scale);
                        return cfg;
                    }
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, Map<String, EnchantabilityConfig>> MAP_CODEC =
            StreamCodec.ofMember(
                    (map, buf) -> {
                        // Encode size
                        ByteBufCodecs.VAR_INT.encode(buf, map.size());
                        // Encode each entry
                        for (Map.Entry<String, EnchantabilityConfig> e : map.entrySet()) {
                            ByteBufCodecs.STRING_UTF8.encode((ByteBuf) buf, e.getKey());
                            ENCHANT_CONFIG_CODEC.encode((RegistryFriendlyByteBuf) buf, e.getValue());
                        }
                    },
                    buf -> {
                        int size = ByteBufCodecs.VAR_INT.decode(buf);
                        Map<String, EnchantabilityConfig> map = new HashMap<>(size);
                        for (int i = 0; i < size; i++) {
                            String key = ByteBufCodecs.STRING_UTF8.decode(buf);
                            EnchantabilityConfig value = ENCHANT_CONFIG_CODEC.decode(buf);
                            map.put(key, value);
                        }
                        return map;
                    }
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigS2CPayload> CODEC =
            MAP_CODEC.map(ConfigS2CPayload::new, ConfigS2CPayload::configList);


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}