package com.astrazoey.indexed;

import com.astrazoey.indexed.platform.Services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Unified JSON config for Indexed, stored at {@code config/indexed.json}.
 * Versioned via {@code config_version}; outdated configs are regenerated
 * with defaults unless {@code enable_config_updates} is false.
 */
public class IndexedConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int CURRENT_VERSION = 2;

    private int config_version = CURRENT_VERSION;
    private boolean enable_config_updates = true;
    private boolean enable_enchantment_nerfs = true;

    private LinkedHashMap<String, EnchantabilityConfig> item_enchanting = new LinkedHashMap<>();
    private LinkedHashMap<String, Integer> crystal_globe_fuel = new LinkedHashMap<>();

    private static Path getConfigPath() {
        return Services.PLATFORM.getConfigDirectory().resolve("indexed.json");
    }

    public static IndexedConfig load() {
        Path configPath = getConfigPath();
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                IndexedConfig config = GSON.fromJson(json, IndexedConfig.class);
                if (config == null) {
                    return createDefault();
                }
                if (config.item_enchanting == null) {
                    config.item_enchanting = new LinkedHashMap<>();
                }
                if (config.crystal_globe_fuel == null) {
                    config.crystal_globe_fuel = new LinkedHashMap<>();
                }
                if (config.config_version < CURRENT_VERSION && config.enable_config_updates) {
                    Constants.LOG.info("Config version {} is outdated (current {}), regenerating with defaults",
                            config.config_version, CURRENT_VERSION);
                    return createDefault();
                }
                // Fill in any entries added since the config was written
                buildItemDefaults().forEach(config.item_enchanting::putIfAbsent);
                buildCrystalGlobeFuelDefaults().forEach(config.crystal_globe_fuel::putIfAbsent);
                return config;
            } catch (Exception e) {
                Constants.LOG.error("Failed to load config, using defaults", e);
                return createDefault();
            }
        } else {
            return createDefault();
        }
    }

    private static IndexedConfig createDefault() {
        IndexedConfig config = new IndexedConfig();
        config.item_enchanting = buildItemDefaults();
        config.crystal_globe_fuel = buildCrystalGlobeFuelDefaults();
        config.save();
        return config;
    }

    public void save() {
        Path configPath = getConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(this));
            Constants.LOG.info("Config saved to {}", configPath);
        } catch (IOException e) {
            Constants.LOG.error("Failed to save config", e);
        }
    }

    public void registerConfigListItems() {
        for (var configEntry : item_enchanting.entrySet()) {
            String itemName = configEntry.getKey();
            EnchantabilityConfig enchantConfig = configEntry.getValue();
            int maxSlots = enchantConfig.maxEnchantingSlots;
            float repairScale = enchantConfig.repairScaling;

            Identifier itemIdentifier = Identifier.parse(itemName);
            Item registerItem = BuiltInRegistries.ITEM.getValue(itemIdentifier);

            MaxEnchantingSlots.setEnchantType(registerItem, new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(maxSlots).repairScaling(repairScale)));
        }
    }

    // Getters for the simple config values (replacing ConfigMain statics)
    public boolean isEnableConfigUpdates() { return enable_config_updates; }
    public boolean isEnableEnchantmentNerfs() { return enable_enchantment_nerfs; }

    public LinkedHashMap<String, EnchantabilityConfig> getItemEnchanting() { return item_enchanting; }

    public Map<String, EnchantabilityConfig> getConfigList() {
        return item_enchanting;
    }

    public Map<String, Integer> getCrystalGlobeFuel() {
        if (crystal_globe_fuel == null) {
            crystal_globe_fuel = buildCrystalGlobeFuelDefaults();
        }
        return crystal_globe_fuel;
    }

    private static LinkedHashMap<String, Integer> buildCrystalGlobeFuelDefaults() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("minecraft:lapis_lazuli", 2);
        map.put("minecraft:echo_shard", 5);
        map.put("minecraft:ender_pearl", 2);
        map.put("minecraft:ender_eye", 5);
        map.put("minecraft:ghast_tear", 5);
        map.put("minecraft:phantom_membrane", 5);
        map.put("minecraft:amethyst_shard", 2);
        return map;
    }

    // Default item enchanting config
    private static LinkedHashMap<String, EnchantabilityConfig> buildItemDefaults() {
        LinkedHashMap<String, EnchantabilityConfig> map = new LinkedHashMap<>();

        //Netherite
        map.put("minecraft:netherite_sword", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("minecraft:netherite_pickaxe", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("minecraft:netherite_axe", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("minecraft:netherite_hoe", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("minecraft:netherite_shovel", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("minecraft:netherite_helmet", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("minecraft:netherite_chestplate", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("minecraft:netherite_leggings", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("minecraft:netherite_boots", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        //Diamond
        map.put("minecraft:diamond_sword", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("minecraft:diamond_pickaxe", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("minecraft:diamond_axe", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("minecraft:diamond_hoe", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("minecraft:diamond_shovel", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("minecraft:diamond_helmet", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("minecraft:diamond_chestplate", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("minecraft:diamond_leggings", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("minecraft:diamond_boots", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());

        //Gold
        map.put("minecraft:golden_sword", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("minecraft:golden_pickaxe", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("minecraft:golden_axe", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("minecraft:golden_hoe", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("minecraft:golden_shovel", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("minecraft:golden_helmet", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("minecraft:golden_chestplate", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("minecraft:golden_leggings", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("minecraft:golden_boots", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());

        //Iron
        map.put("minecraft:iron_sword", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("minecraft:iron_pickaxe", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("minecraft:iron_axe", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("minecraft:iron_hoe", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("minecraft:iron_shovel", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("minecraft:iron_helmet", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("minecraft:iron_chestplate", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("minecraft:iron_leggings", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("minecraft:iron_boots", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());

        //Copper
        map.put("minecraft:copper_sword", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        map.put("minecraft:copper_pickaxe", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        map.put("minecraft:copper_axe", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        map.put("minecraft:copper_hoe", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        map.put("minecraft:copper_shovel", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        map.put("minecraft:copper_helmet", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        map.put("minecraft:copper_chestplate", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        map.put("minecraft:copper_leggings", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        map.put("minecraft:copper_boots", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());

        //Stone
        map.put("minecraft:stone_sword", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("minecraft:stone_pickaxe", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("minecraft:stone_axe", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("minecraft:stone_hoe", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("minecraft:stone_shovel", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());

        //Wood
        map.put("minecraft:wooden_sword", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("minecraft:wooden_pickaxe", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("minecraft:wooden_axe", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("minecraft:wooden_hoe", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("minecraft:wooden_shovel", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());

        //Chainmail
        map.put("minecraft:chainmail_helmet", EnchantingTypes.CHAINMAIL_TIER.getEnchantabilityConfig());
        map.put("minecraft:chainmail_chestplate", EnchantingTypes.CHAINMAIL_TIER.getEnchantabilityConfig());
        map.put("minecraft:chainmail_leggings", EnchantingTypes.CHAINMAIL_TIER.getEnchantabilityConfig());
        map.put("minecraft:chainmail_boots", EnchantingTypes.CHAINMAIL_TIER.getEnchantabilityConfig());

        //Leather
        map.put("minecraft:leather_helmet", EnchantingTypes.LEATHER_TIER.getEnchantabilityConfig());
        map.put("minecraft:leather_chestplate", EnchantingTypes.LEATHER_TIER.getEnchantabilityConfig());
        map.put("minecraft:leather_leggings", EnchantingTypes.LEATHER_TIER.getEnchantabilityConfig());
        map.put("minecraft:leather_boots", EnchantingTypes.LEATHER_TIER.getEnchantabilityConfig());

        //Misc
        map.put("minecraft:fishing_rod", EnchantingTypes.FISHING_ROD.getEnchantabilityConfig());
        map.put("minecraft:crossbow", EnchantingTypes.CROSSBOW.getEnchantabilityConfig());
        map.put("minecraft:bow", EnchantingTypes.BOW.getEnchantabilityConfig());
        map.put("minecraft:trident", EnchantingTypes.TRIDENT.getEnchantabilityConfig());
        map.put("minecraft:wooden_spear", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("minecraft:stone_spear", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("minecraft:copper_spear", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        map.put("minecraft:iron_spear", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("minecraft:golden_spear", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("minecraft:diamond_spear", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("minecraft:netherite_spear", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("minecraft:turtle_helmet", EnchantingTypes.TURTLE_HELMET.getEnchantabilityConfig());
        map.put("minecraft:mace", EnchantingTypes.MACE.getEnchantabilityConfig());

        //Other
        map.put("minecraft:elytra", EnchantingTypes.ELYTRA.getEnchantabilityConfig());
        map.put("minecraft:shears", EnchantingTypes.GENERIC.getEnchantabilityConfig());
        map.put("minecraft:flint_and_steel", EnchantingTypes.GENERIC.getEnchantabilityConfig());
        map.put("minecraft:shield", EnchantingTypes.SHIELD.getEnchantabilityConfig());
        map.put("minecraft:carrot_on_a_stick", EnchantingTypes.GENERIC.getEnchantabilityConfig());
        map.put("minecraft:warped_fungus_on_a_stick", EnchantingTypes.GENERIC.getEnchantabilityConfig());
        map.put("minecraft:brush", EnchantingTypes.GENERIC.getEnchantabilityConfig());

        //Modded support
        map.put("mattock:mattock", new EnchantabilityConfig(7, 1.0f));

        map.put("carvepump:wooden_carver", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("carvepump:stone_carver", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("carvepump:iron_carver", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("carvepump:gold_carver", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("carvepump:diamond_carver", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("carvepump:netherite_carver", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        map.put("outvoted:wildfire_shield", EnchantingTypes.SHIELD.getEnchantabilityConfig());

        map.put("betterend:elytra_crystalite", EnchantingTypes.ELYTRA_MODIFIED.getEnchantabilityConfig());
        map.put("betterend:elytra_armored", EnchantingTypes.ELYTRA_MODIFIED.getEnchantabilityConfig());
        map.put("betterend:iron_hammer", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("betterend:golden_hammer", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("betterend:diamond_hammer", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("betterend:netherite_hammer", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("betterend:aeternium_sword", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        map.put("betterend:aeternium_pickaxe", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        map.put("betterend:aeternium_axe", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        map.put("betterend:aeternium_hoe", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        map.put("betterend:aeternium_hammer", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        map.put("betterend:aeternium_shovel", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        map.put("betterend:aeternium_helmet", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        map.put("betterend:aeternium_chestplate", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        map.put("betterend:aeternium_leggings", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        map.put("betterend:aeternium_boots", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        map.put("betterend:terminite_sword", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        map.put("betterend:terminite_pickaxe", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        map.put("betterend:terminite_axe", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        map.put("betterend:terminite_hoe", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        map.put("betterend:terminite_hammer", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        map.put("betterend:terminite_shovel", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        map.put("betterend:terminite_helmet", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        map.put("betterend:terminite_chestplate", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        map.put("betterend:terminite_leggings", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        map.put("betterend:terminite_boots", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        map.put("betterend:thallasium_sword", EnchantingTypes.THALLASIUM.getEnchantabilityConfig());
        map.put("betterend:thallasium_pickaxe", EnchantingTypes.THALLASIUM.getEnchantabilityConfig());
        map.put("betterend:thallasium_axe", EnchantingTypes.THALLASIUM.getEnchantabilityConfig());
        map.put("betterend:thallasium_hoe", EnchantingTypes.THALLASIUM.getEnchantabilityConfig());
        map.put("betterend:thallasium_hammer", EnchantingTypes.THALLASIUM.getEnchantabilityConfig());
        map.put("betterend:thallasium_shovel", EnchantingTypes.THALLASIUM.getEnchantabilityConfig());
        map.put("betterend:crystalite_helmet", EnchantingTypes.CRYSTALITE.getEnchantabilityConfig());
        map.put("betterend:crystalite_chestplate", EnchantingTypes.CRYSTALITE.getEnchantabilityConfig());
        map.put("betterend:crystalite_leggings", EnchantingTypes.CRYSTALITE.getEnchantabilityConfig());
        map.put("betterend:crystalite_boots", EnchantingTypes.CRYSTALITE.getEnchantabilityConfig());

        map.put("betternether:cincinnasite_shears", EnchantingTypes.GENERIC.getEnchantabilityConfig());
        map.put("betternether:cincinnasite_sword", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        map.put("betternether:cincinnasite_axe", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        map.put("betternether:cincinnasite_shovel", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        map.put("betternether:cincinnasite_hoe", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        map.put("betternether:cincinnasite_pickaxe", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        map.put("betternether:cincinnasite_helmet", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        map.put("betternether:cincinnasite_chestplate", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        map.put("betternether:cincinnasite_leggings", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        map.put("betternether:cincinnasite_boots", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        map.put("betternether:cincinnasite_sword_diamond", EnchantingTypes.CINCINNASITE_DIAMOND.getEnchantabilityConfig());
        map.put("betternether:cincinnasite_axe_diamond", EnchantingTypes.CINCINNASITE_DIAMOND.getEnchantabilityConfig());
        map.put("betternether:cincinnasite_shovel_diamond", EnchantingTypes.CINCINNASITE_DIAMOND.getEnchantabilityConfig());
        map.put("betternether:cincinnasite_hoe_diamond", EnchantingTypes.CINCINNASITE_DIAMOND.getEnchantabilityConfig());
        map.put("betternether:cincinnasite_pickaxe_diamond", EnchantingTypes.CINCINNASITE_DIAMOND.getEnchantabilityConfig());
        map.put("betternether:nether_ruby_sword", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        map.put("betternether:nether_ruby_axe", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        map.put("betternether:nether_ruby_shovel", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        map.put("betternether:nether_ruby_hoe", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        map.put("betternether:nether_ruby_pickaxe", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        map.put("betternether:nether_ruby_helmet", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        map.put("betternether:nether_ruby_chestplate", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        map.put("betternether:nether_ruby_leggings", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        map.put("betternether:nether_ruby_boots", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());

        map.put("adventurez:stone_golem_helmet", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        map.put("adventurez:stone_golem_chestplate", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        map.put("adventurez:stone_golem_leggings", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        map.put("adventurez:stone_golem_boots", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        map.put("adventurez:ender_flute", EnchantingTypes.GENERIC.getEnchantabilityConfig());
        map.put("adventurez:chorus_fruit_on_a_stick", EnchantingTypes.GENERIC.getEnchantabilityConfig());

        map.put("wolveswitharmor:leather_wolf_armor", EnchantingTypes.LEATHER_TIER.getEnchantabilityConfig());
        map.put("wolveswitharmor:iron_wolf_armor", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("wolveswitharmor:golden_wolf_armor", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("wolveswitharmor:diamond_wolf_armor", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("wolveswitharmor:netherite_wolf_armor", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        map.put("gateofbabylon:netherite_dagger", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:netherite_spear", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:netherite_broadsword", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:netherite_rapier", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:netherite_haladie", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:netherite_waraxe", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:netherite_katana", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:netherite_bow", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:netherite_shield", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:netherite_yoyo", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:netherite_boomerang", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:diamond_dagger", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:diamond_spear", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:diamond_broadsword", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:diamond_rapier", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:diamond_haladie", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:diamond_waraxe", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:diamond_katana", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:diamond_bow", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:diamond_shield", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:diamond_yoyo", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:diamond_boomerang", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:golden_dagger", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:golden_spear", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:golden_broadsword", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:golden_rapier", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:golden_haladie", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:golden_waraxe", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:golden_katana", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:golden_bow", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:golden_shield", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:golden_yoyo", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:golden_boomerang", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:iron_dagger", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:iron_spear", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:iron_broadsword", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:iron_rapier", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:iron_haladie", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:iron_waraxe", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:iron_katana", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:iron_bow", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:iron_shield", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:iron_yoyo", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:iron_boomerang", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:stone_dagger", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:stone_spear", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:stone_broadsword", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:stone_rapier", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:stone_haladie", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:stone_waraxe", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:stone_katana", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:stone_bow", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:stone_shield", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:stone_yoyo", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:stone_boomerang", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:wooden_dagger", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:wooden_spear", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:wooden_broadsword", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:wooden_rapier", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:wooden_haladie", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:wooden_waraxe", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:wooden_katana", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:wooden_bow", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:wooden_shield", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:wooden_yoyo", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("gateofbabylon:wooden_boomerang", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());

        map.put("dragonloot:dragon_helmet", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        map.put("dragonloot:dragon_chestplate", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        map.put("dragonloot:dragon_leggings", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        map.put("dragonloot:dragon_boots", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        map.put("dragonloot:dragon_pickaxe", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        map.put("dragonloot:dragon_shovel", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        map.put("dragonloot:dragon_axe", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        map.put("dragonloot:dragon_hoe", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        map.put("dragonloot:dragon_sword", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        map.put("dragonloot:dragon_crossbow", new EnchantabilityConfig(6, 1.0f));
        map.put("dragonloot:dragon_bow", new EnchantabilityConfig(6, 1.0f));
        map.put("dragonloot:dragon_trident", new EnchantabilityConfig(6, 1.0f));

        map.put("ratsmischief:rat_mask", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());

        map.put("farmersdelight:flint_knife", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("farmersdelight:iron_knife", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("farmersdelight:golden_knife", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("farmersdelight:diamond_knife", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("farmersdelight:netherite_knife", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        map.put("consistency_plus:turtle_chestplate", EnchantingTypes.TURTLE_HELMET.getEnchantabilityConfig());
        map.put("consistency_plus:turtle_leggings", EnchantingTypes.TURTLE_HELMET.getEnchantabilityConfig());
        map.put("consistency_plus:turtle_boots", EnchantingTypes.TURTLE_HELMET.getEnchantabilityConfig());

        map.put("valley:rg_helmet", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:rg_chestplate", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:rg_leggings", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:rg_boots", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:rg_sword", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:rg_axe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:rg_shovel", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:rg_hoe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:rg_pickaxe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:wood_knife", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("valley:stone_knife", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("valley:iron_knife", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("valley:golden_knife", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:diamond_knife", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("valley:netherite_knife", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("valley:rg_knife", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:bone_knife", EnchantingTypes.BONE_TIER.getEnchantabilityConfig());
        map.put("valley:wood_sickle", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("valley:stone_sickle", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("valley:iron_sickle", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("valley:golden_sickle", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:diamond_sickle", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("valley:netherite_sickle", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("valley:rg_sickle", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:wood_hatchet", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("valley:stone_hatchet", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("valley:iron_hatchet", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("valley:golden_hatchet", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:diamond_hatchet", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("valley:netherite_hatchet", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("valley:rg_hatchet", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("valley:turtle_chestplate", EnchantingTypes.TURTLE_HELMET.getEnchantabilityConfig());
        map.put("valley:turtle_leggings", EnchantingTypes.TURTLE_HELMET.getEnchantabilityConfig());
        map.put("valley:turtle_boots", EnchantingTypes.TURTLE_HELMET.getEnchantabilityConfig());
        map.put("valley:tongs", EnchantingTypes.GENERIC.getEnchantabilityConfig());
        map.put("valley:lumber_axe", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("valley:fur_chestplate", EnchantingTypes.LEATHER_TIER.getEnchantabilityConfig());

        map.put("impaled:pitchfork", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("impaled:atlan", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("impaled:elder_trident", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("impaled:hellfork", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        map.put("harvest_scythes:wooden_scythe", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("harvest_scythes:stone_scythe", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("harvest_scythes:iron_scythe", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("harvest_scythes:golden_scythe", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("harvest_scythes:diamond_scythe", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("harvest_scythes:netherite_scythe", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("harvest_scythes:wooden_machete", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        map.put("harvest_scythes:stone_machete", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        map.put("harvest_scythes:iron_machete", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("harvest_scythes:golden_machete", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        map.put("harvest_scythes:diamond_machete", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("harvest_scythes:netherite_machete", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        map.put("gofish:skeletal_rod", EnchantingTypes.BONE_TIER.getEnchantabilityConfig());
        map.put("gofish:blaze_rod", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gofish:soul_rod", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        map.put("gofish:diamond_reinforced_rod", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gofish:ender_rod", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gofish:matrix_rod", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        map.put("gofish:frosted_rod", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("gofish:slime_rod", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        map.put("gofish:celestial_rod", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());

        map.put("additionaladditions:rose_gold_helmet", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("additionaladditions:rose_gold_chestplate", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("additionaladditions:rose_gold_leggings", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("additionaladditions:rose_gold_boots", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("additionaladditions:rose_gold_sword", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("additionaladditions:rose_gold_axe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("additionaladditions:rose_gold_shovel", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("additionaladditions:rose_gold_hoe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("additionaladditions:rose_gold_pickaxe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("additionaladditions:gilded_netherite_helmet", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        map.put("additionaladditions:gilded_netherite_chestplate", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        map.put("additionaladditions:gilded_netherite_leggings", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        map.put("additionaladditions:gilded_netherite_boots", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        map.put("additionaladditions:gilded_netherite_sword", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        map.put("additionaladditions:gilded_netherite_axe", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        map.put("additionaladditions:gilded_netherite_shovel", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        map.put("additionaladditions:gilded_netherite_hoe", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        map.put("additionaladditions:gilded_netherite_pickaxe", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        map.put("additionaladditions:crossbow_with_spyglass", EnchantingTypes.CROSSBOW.getEnchantabilityConfig());

        map.put("oxidized:rose_gold_sword", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("oxidized:rose_gold_axe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("oxidized:rose_gold_shovel", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("oxidized:rose_gold_hoe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        map.put("oxidized:rose_gold_pickaxe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());

        map.put("conjuring:soul_alloy_sword", EnchantingTypes.SOUL_ALLOY.getEnchantabilityConfig());
        map.put("conjuring:soul_alloy_shovel", EnchantingTypes.SOUL_ALLOY.getEnchantabilityConfig());
        map.put("conjuring:soul_alloy_hatchet", EnchantingTypes.SOUL_ALLOY.getEnchantabilityConfig());
        map.put("conjuring:soul_alloy_pickaxe", EnchantingTypes.SOUL_ALLOY.getEnchantabilityConfig());

        return map;
    }
}
