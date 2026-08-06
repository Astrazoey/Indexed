package com.astrazoey.indexed;

public class EnchantingType {
    public float repairScaling;
    public int maxEnchantingSlots;


    public EnchantingType(EnchantingType.Settings settings) {
        this.repairScaling = settings.repairScaling;
        this.maxEnchantingSlots = settings.maxEnchantingSlots;
    }

    public float getRepairScaling() {
        return repairScaling;
    }

    public int getMaxEnchantingSlots() {
        return maxEnchantingSlots;
    }

    public EnchantabilityConfig getEnchantabilityConfig() {
        EnchantabilityConfig enchantConfig = new EnchantabilityConfig(0, 0);
        enchantConfig.setMaxEnchantingSlots(this.maxEnchantingSlots);
        enchantConfig.setRepairScaling(this.repairScaling);
        return enchantConfig;
    }


    public static class Settings {
        float repairScaling;
        int maxEnchantingSlots;

        public Settings() {

        }

        public EnchantingType.Settings repairScaling(float repairScaling) {
            this.repairScaling = repairScaling;
            return this;
        }

        public EnchantingType.Settings maxEnchantingSlots(int maxEnchantingSlots) {
            this.maxEnchantingSlots = maxEnchantingSlots;
            return this;
        }

    }
}
