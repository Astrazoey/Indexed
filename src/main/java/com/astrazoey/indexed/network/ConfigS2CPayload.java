package com.astrazoey.indexed.network;


import com.astrazoey.indexed.EnchantabilityConfig;
import com.astrazoey.indexed.Indexed;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record ConfigS2CPayload(Map<String, EnchantabilityConfig> configList) implements CustomPayload {
    public static final Identifier CONFIG_PAYLOAD_ID = Identifier.of(Indexed.MOD_ID, "config");
    public static final Id<ConfigS2CPayload> ID = new Id<>(CONFIG_PAYLOAD_ID);

    // Value codec for EnchantabilityConfig
    private static final PacketCodec<RegistryByteBuf, EnchantabilityConfig> ENCHANT_CONFIG_CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, EnchantabilityConfig::getMaxEnchantingSlots,
                    PacketCodecs.FLOAT,   EnchantabilityConfig::getRepairScaling,
                    (slots, scale) -> {
                        EnchantabilityConfig cfg = new EnchantabilityConfig(0, 0);
                        cfg.setMaxEnchantingSlots(slots);
                        cfg.setRepairScaling(scale);
                        return cfg;
                    }
            );

    private static final PacketCodec<RegistryByteBuf, Map<String, EnchantabilityConfig>> MAP_CODEC =
            PacketCodec.of(
                    (map, buf) -> {
                        // Encode size
                        PacketCodecs.VAR_INT.encode(buf, map.size());
                        // Encode each entry
                        for (Map.Entry<String, EnchantabilityConfig> e : map.entrySet()) {
                            PacketCodecs.STRING.encode((ByteBuf) buf, e.getKey());
                            ENCHANT_CONFIG_CODEC.encode((RegistryByteBuf) buf, e.getValue());
                        }
                    },
                    buf -> {
                        int size = PacketCodecs.VAR_INT.decode(buf);
                        Map<String, EnchantabilityConfig> map = new HashMap<>(size);
                        for (int i = 0; i < size; i++) {
                            String key = PacketCodecs.STRING.decode(buf);
                            EnchantabilityConfig value = ENCHANT_CONFIG_CODEC.decode(buf);
                            map.put(key, value);
                        }
                        return map;
                    }
            );

    public static final PacketCodec<RegistryByteBuf, ConfigS2CPayload> CODEC =
            MAP_CODEC.xmap(ConfigS2CPayload::new, ConfigS2CPayload::configList);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}