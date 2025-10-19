package com.astrazoey.indexed;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.WitherStatusEffect;
import net.minecraft.entity.mob.MobEntity;

public class EnchantingTypes {
    public static final EnchantingType GENERIC;

    public static final EnchantingType NETHERITE_TIER;
    public static final EnchantingType DIAMOND_TIER;
    public static final EnchantingType IRON_TIER;
    public static final EnchantingType GOLD_TIER;
    public static final EnchantingType STONE_TIER;
    public static final EnchantingType WOOD_TIER;
    public static final EnchantingType LEATHER_TIER;
    public static final EnchantingType CHAINMAIL_TIER;
    public static final EnchantingType COPPER_TIER;

    public static final EnchantingType FISHING_ROD;
    public static final EnchantingType CROSSBOW;
    public static final EnchantingType BOW;
    public static final EnchantingType TRIDENT;
    public static final EnchantingType TURTLE_HELMET;
    public static final EnchantingType MACE;

    public static final EnchantingType ELYTRA;
    //public static final EnchantingType SHEARS;
    //public static final EnchantingType FLINT_AND_STEEL;
    public static final EnchantingType SHIELD;

    //Modded Types
    public static final EnchantingType ELYTRA_MODIFIED;
    public static final EnchantingType ROSE_GOLD_TIER;
    public static final EnchantingType NETHERITE_GILDED;
    public static final EnchantingType BONE_TIER;

    //Better End Types
    public static final EnchantingType TERMINITE;
    public static final EnchantingType THALLASIUM;
    public static final EnchantingType AETERNIUM;
    public static final EnchantingType CRYSTALITE;

    //Better Nether Types
    public static final EnchantingType NETHER_RUBY;
    public static final EnchantingType CINCINNASITE;
    public static final EnchantingType CINCINNASITE_DIAMOND;

    //Dragon Loot Types
    public static final EnchantingType DRAGON;

    //Conjuring Types
    public static final EnchantingType SOUL_ALLOY;

    static {
        GENERIC = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(5).repairScaling(0.2f));

        NETHERITE_TIER = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(10).repairScaling(2f));
        DIAMOND_TIER = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(10).repairScaling(1f));
        IRON_TIER = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(12).repairScaling(0.5f));
        GOLD_TIER = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(18).repairScaling(0.7f));
        STONE_TIER = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(14).repairScaling(0.5f));
        CHAINMAIL_TIER = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(14).repairScaling(0.5f));
        WOOD_TIER = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(16).repairScaling(0.25f));
        LEATHER_TIER = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(16).repairScaling(0.25f));
        COPPER_TIER = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(12).repairScaling(0.25f));

        FISHING_ROD = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(8).repairScaling(0.2f));
        CROSSBOW = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(7).repairScaling(0.3f));
        BOW = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(8).repairScaling(0.4f));
        TRIDENT = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(8).repairScaling(0.5f));
        TURTLE_HELMET = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(15).repairScaling(0.5f));
        MACE = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(11).repairScaling(0.5f));

        ELYTRA = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(6).repairScaling(1f));
        //SHEARS = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(5).repairScaling(0.2f));
        //FLINT_AND_STEEL = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(5).repairScaling(0.2f));
        SHIELD = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(5).repairScaling(0.2f));

        //Modded Support
        ELYTRA_MODIFIED = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(5).repairScaling(1f));
        ROSE_GOLD_TIER = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(18).repairScaling(0.5f));
        BONE_TIER = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(5).repairScaling(1f));
        NETHERITE_GILDED = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(12).repairScaling(0.3f));

        //Better End Support
        AETERNIUM = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(8).repairScaling(1f));
        CRYSTALITE = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(6).repairScaling(1f));
        TERMINITE = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(12).repairScaling(0.5f));
        THALLASIUM = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(12).repairScaling(1f));

        //Better Nether Support
        NETHER_RUBY = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(11).repairScaling(1f));
        CINCINNASITE = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(8).repairScaling(1f));
        CINCINNASITE_DIAMOND = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(8).repairScaling(2f));

        //Dragon Loot Support
        DRAGON = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(8).repairScaling(1f));

        //Conjuring Support
        SOUL_ALLOY = new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(15).repairScaling(1f));
    }

}
