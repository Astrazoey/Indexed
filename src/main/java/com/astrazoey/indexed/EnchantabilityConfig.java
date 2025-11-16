package com.astrazoey.indexed;

import com.google.gson.annotations.SerializedName;

public class EnchantabilityConfig {
    @SerializedName("max_enchanting_slots")
    public int maxEnchantingSlots;
    @SerializedName("repair_scaling")
    public float repairScaling;

    public EnchantabilityConfig(int maxEnchantingSlots, float repairScaling) {
        this.maxEnchantingSlots = maxEnchantingSlots;
        this.repairScaling = repairScaling;
    }

    public void setMaxEnchantingSlots(int maxEnchantingSlots) {
        this.maxEnchantingSlots=maxEnchantingSlots;
    }

    public void setRepairScaling(float repairScaling) {
        this.repairScaling=repairScaling;
    }

    public int getMaxEnchantingSlots() {
        return maxEnchantingSlots;
    }

    public float getRepairScaling() {
        return repairScaling;
    }

}
