package com.astrazoey.indexed;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Properties;

public class Config {

    //private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("indexed_modded_items.properties");

    private static final File configFile = FabricLoader.getInstance().getConfigDir().resolve("indexed_modded_items.json").toFile();
    private static final Path configFilePath = FabricLoader.getInstance().getConfigDir().resolve("indexed_modded_items.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static LinkedHashMap<String, EnchantabilityConfig> configList;



    public static void save() {
        Properties props = new Properties();
        read(props);

        /*
        if (!Files.exists(CONFIG_PATH)) {
            try {
                Files.createFile(CONFIG_PATH);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }*/

        /*
        try (OutputStream out = Files.newOutputStream(CONFIG_PATH)) {
            props.store(out, "Indexed Configuration");
        } catch (Exception e) {
            e.printStackTrace();
        }
         */
    }

    private static boolean generateNewConfigFile() {
        try {
            Files.createFile(configFilePath);
            save();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean readConfigList() {
        try(FileReader reader = new FileReader(Config.configFile)) {
            Config.configList = gson.fromJson(reader, new TypeToken<LinkedHashMap<String, EnchantabilityConfig>>(){}.getType());

            if(configList == null) {
                Config.configFile.delete();
                System.out.println("INDEXED: Config file is null and deleted. Generating new config.");
                if(!generateNewConfigFile()) {
                    return false;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void registerConfigListItems() {
        for(int i = 0; i < configList.size(); i++) {
            Object configEntry = configList.keySet().toArray()[i];
            EnchantabilityConfig enchantConfig = configList.get(configEntry);
            String itemName = (String) configEntry;
            int maxSlots = enchantConfig.maxEnchantingSlots;
            float repairScale = enchantConfig.repairScaling;

            //System.out.println("Found entry at " + i + ". It is called \"" + itemName + "\" and has " + maxSlots + " max slots and repair scaling of " + repairScale);

            Identifier itemIdentifier = Identifier.of(itemName);
            ThreadLocal<Identifier> localItemIdentifier = new ThreadLocal<Identifier>();
            localItemIdentifier.set(itemIdentifier);

            Item registerItem = Registries.ITEM.get(localItemIdentifier.get());


            MaxEnchantingSlots.setEnchantType(registerItem, new EnchantingType(new EnchantingType.Settings().maxEnchantingSlots(maxSlots).repairScaling(repairScale)));

        }
    }


    public static void loadConfig(boolean modOutOfDate) {

        if(modOutOfDate && Files.exists(configFilePath)) {
            try {
                System.out.println("mod is out of date! resetting file!");
                Files.delete(configFilePath);
                configList = null;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to delete config file.");
                return;
            }
        }

        if(Config.configFile.exists()) {
            if(!readConfigList()) {
                System.out.println("Failed to read config list.");
                return;
            }
        } else {
            try {
                Files.createFile(configFilePath);
                save();
            } catch (Exception e) {
                if(!generateNewConfigFile()) {
                    System.out.println("Failed to generate new config file");
                    return;
                }
            }
        }


        if(configList == null) {
            if(!readConfigList()) {
                System.out.println("INDEXED: Failed to read config.");
                return;
            }
        }

        registerConfigListItems();
    }

    public static void read(Properties props) {
        LinkedHashMap<String, EnchantabilityConfig> defaultConfig = new LinkedHashMap<>();

        /*
        The following are some default values for vanilla items.
         */

        //Netherite
        defaultConfig.put("minecraft:netherite_sword", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:netherite_pickaxe", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:netherite_axe", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:netherite_hoe", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:netherite_shovel", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:netherite_helmet", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:netherite_chestplate", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:netherite_leggings", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:netherite_boots", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        //Diamond
        defaultConfig.put("minecraft:diamond_sword", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:diamond_pickaxe", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:diamond_axe", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:diamond_hoe", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:diamond_shovel", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:diamond_helmet", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:diamond_chestplate", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:diamond_leggings", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:diamond_boots", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());

        //Gold
        defaultConfig.put("minecraft:golden_sword", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:golden_pickaxe", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:golden_axe", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:golden_hoe", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:golden_shovel", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:golden_helmet", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:golden_chestplate", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:golden_leggings", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:golden_boots", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());

        //Iron
        defaultConfig.put("minecraft:iron_sword", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:iron_pickaxe", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:iron_axe", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:iron_hoe", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:iron_shovel", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:iron_helmet", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:iron_chestplate", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:iron_leggings", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:iron_boots", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());

        //Copper
        defaultConfig.put("minecraft:copper_sword", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:copper_pickaxe", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:copper_axe", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:copper_hoe", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:copper_shovel", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:copper_helmet", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:copper_chestplate", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:copper_leggings", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:copper_boots", EnchantingTypes.COPPER_TIER.getEnchantabilityConfig());

        //Stone
        defaultConfig.put("minecraft:stone_sword", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:stone_pickaxe", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:stone_axe", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:stone_hoe", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:stone_shovel", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());

        //Wood
        defaultConfig.put("minecraft:wooden_sword", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:wooden_pickaxe", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:wooden_axe", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:wooden_hoe", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:wooden_shovel", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());

        //Chainmail
        defaultConfig.put("minecraft:chainmail_helmet", EnchantingTypes.CHAINMAIL_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:chainmail_chestplate", EnchantingTypes.CHAINMAIL_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:chainmail_leggings", EnchantingTypes.CHAINMAIL_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:chainmail_boots", EnchantingTypes.CHAINMAIL_TIER.getEnchantabilityConfig());

        //Leather
        defaultConfig.put("minecraft:leather_helmet", EnchantingTypes.LEATHER_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:leather_chestplate", EnchantingTypes.LEATHER_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:leather_leggings", EnchantingTypes.LEATHER_TIER.getEnchantabilityConfig());
        defaultConfig.put("minecraft:leather_boots", EnchantingTypes.LEATHER_TIER.getEnchantabilityConfig());

        //Misc
        defaultConfig.put("minecraft:fishing_rod", EnchantingTypes.FISHING_ROD.getEnchantabilityConfig());
        defaultConfig.put("minecraft:crossbow", EnchantingTypes.CROSSBOW.getEnchantabilityConfig());
        defaultConfig.put("minecraft:bow", EnchantingTypes.BOW.getEnchantabilityConfig());
        defaultConfig.put("minecraft:trident", EnchantingTypes.TRIDENT.getEnchantabilityConfig());
        defaultConfig.put("minecraft:turtle_helmet", EnchantingTypes.TURTLE_HELMET.getEnchantabilityConfig());
        defaultConfig.put("minecraft:mace", EnchantingTypes.MACE.getEnchantabilityConfig());

        //Other
        defaultConfig.put("minecraft:elytra", EnchantingTypes.ELYTRA.getEnchantabilityConfig());
        defaultConfig.put("minecraft:shears", EnchantingTypes.GENERIC.getEnchantabilityConfig());
        defaultConfig.put("minecraft:flint_and_steel", EnchantingTypes.GENERIC.getEnchantabilityConfig());
        defaultConfig.put("minecraft:shield", EnchantingTypes.SHIELD.getEnchantabilityConfig());
        defaultConfig.put("minecraft:carrot_on_a_stick", EnchantingTypes.GENERIC.getEnchantabilityConfig());
        defaultConfig.put("minecraft:warped_fungus_on_a_stick", EnchantingTypes.GENERIC.getEnchantabilityConfig());
        defaultConfig.put("minecraft:brush", EnchantingTypes.GENERIC.getEnchantabilityConfig());


        /*
        The following is a default auto-generated config for mods that are supported by default.
         */


        //Mattock
        EnchantabilityConfig mattockConfig = new EnchantabilityConfig(7, 1.0f);
        defaultConfig.put("mattock:mattock", mattockConfig);

        //Carve Your Pumpkin
        defaultConfig.put("carvepump:wooden_carver", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("carvepump:stone_carver", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("carvepump:iron_carver", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("carvepump:gold_carver", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("carvepump:diamond_carver", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("carvepump:netherite_carver", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        //Outvoted
        defaultConfig.put("outvoted:wildfire_shield", EnchantingTypes.SHIELD.getEnchantabilityConfig());

        //Better End
        defaultConfig.put("betterend:elytra_crystalite", EnchantingTypes.ELYTRA_MODIFIED.getEnchantabilityConfig());
        defaultConfig.put("betterend:elytra_armored", EnchantingTypes.ELYTRA_MODIFIED.getEnchantabilityConfig());

        defaultConfig.put("betterend:iron_hammer", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("betterend:golden_hammer", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("betterend:diamond_hammer", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("betterend:netherite_hammer", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        defaultConfig.put("betterend:aeternium_sword", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        defaultConfig.put("betterend:aeternium_pickaxe", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        defaultConfig.put("betterend:aeternium_axe", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        defaultConfig.put("betterend:aeternium_hoe", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        defaultConfig.put("betterend:aeternium_hammer", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        defaultConfig.put("betterend:aeternium_shovel", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        defaultConfig.put("betterend:aeternium_helmet", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        defaultConfig.put("betterend:aeternium_chestplate", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        defaultConfig.put("betterend:aeternium_leggings", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());
        defaultConfig.put("betterend:aeternium_boots", EnchantingTypes.AETERNIUM.getEnchantabilityConfig());

        defaultConfig.put("betterend:terminite_sword", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        defaultConfig.put("betterend:terminite_pickaxe", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        defaultConfig.put("betterend:terminite_axe", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        defaultConfig.put("betterend:terminite_hoe", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        defaultConfig.put("betterend:terminite_hammer", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        defaultConfig.put("betterend:terminite_shovel", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        defaultConfig.put("betterend:terminite_helmet", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        defaultConfig.put("betterend:terminite_chestplate", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        defaultConfig.put("betterend:terminite_leggings", EnchantingTypes.TERMINITE.getEnchantabilityConfig());
        defaultConfig.put("betterend:terminite_boots", EnchantingTypes.TERMINITE.getEnchantabilityConfig());

        defaultConfig.put("betterend:thallasium_sword", EnchantingTypes.THALLASIUM.getEnchantabilityConfig());
        defaultConfig.put("betterend:thallasium_pickaxe", EnchantingTypes.THALLASIUM.getEnchantabilityConfig());
        defaultConfig.put("betterend:thallasium_axe", EnchantingTypes.THALLASIUM.getEnchantabilityConfig());
        defaultConfig.put("betterend:thallasium_hoe", EnchantingTypes.THALLASIUM.getEnchantabilityConfig());
        defaultConfig.put("betterend:thallasium_hammer", EnchantingTypes.THALLASIUM.getEnchantabilityConfig());
        defaultConfig.put("betterend:thallasium_shovel", EnchantingTypes.THALLASIUM.getEnchantabilityConfig());

        defaultConfig.put("betterend:crystalite_helmet", EnchantingTypes.CRYSTALITE.getEnchantabilityConfig());
        defaultConfig.put("betterend:crystalite_chestplate", EnchantingTypes.CRYSTALITE.getEnchantabilityConfig());
        defaultConfig.put("betterend:crystalite_leggings", EnchantingTypes.CRYSTALITE.getEnchantabilityConfig());
        defaultConfig.put("betterend:crystalite_boots", EnchantingTypes.CRYSTALITE.getEnchantabilityConfig());


        //Better Nether
        defaultConfig.put("betternether:cincinnasite_shears", EnchantingTypes.GENERIC.getEnchantabilityConfig());

        defaultConfig.put("betternether:cincinnasite_sword", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        defaultConfig.put("betternether:cincinnasite_axe", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        defaultConfig.put("betternether:cincinnasite_shovel", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        defaultConfig.put("betternether:cincinnasite_hoe", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        defaultConfig.put("betternether:cincinnasite_pickaxe", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        defaultConfig.put("betternether:cincinnasite_helmet", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        defaultConfig.put("betternether:cincinnasite_chestplate", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        defaultConfig.put("betternether:cincinnasite_leggings", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());
        defaultConfig.put("betternether:cincinnasite_boots", EnchantingTypes.CINCINNASITE.getEnchantabilityConfig());

        defaultConfig.put("betternether:cincinnasite_sword_diamond", EnchantingTypes.CINCINNASITE_DIAMOND.getEnchantabilityConfig());
        defaultConfig.put("betternether:cincinnasite_axe_diamond", EnchantingTypes.CINCINNASITE_DIAMOND.getEnchantabilityConfig());
        defaultConfig.put("betternether:cincinnasite_shovel_diamond", EnchantingTypes.CINCINNASITE_DIAMOND.getEnchantabilityConfig());
        defaultConfig.put("betternether:cincinnasite_hoe_diamond", EnchantingTypes.CINCINNASITE_DIAMOND.getEnchantabilityConfig());
        defaultConfig.put("betternether:cincinnasite_pickaxe_diamond", EnchantingTypes.CINCINNASITE_DIAMOND.getEnchantabilityConfig());

        defaultConfig.put("betternether:nether_ruby_sword", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        defaultConfig.put("betternether:nether_ruby_axe", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        defaultConfig.put("betternether:nether_ruby_shovel", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        defaultConfig.put("betternether:nether_ruby_hoe", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        defaultConfig.put("betternether:nether_ruby_pickaxe", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        defaultConfig.put("betternether:nether_ruby_helmet", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        defaultConfig.put("betternether:nether_ruby_chestplate", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        defaultConfig.put("betternether:nether_ruby_leggings", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());
        defaultConfig.put("betternether:nether_ruby_boots", EnchantingTypes.NETHER_RUBY.getEnchantabilityConfig());


        //AdventureZ
        defaultConfig.put("adventurez:stone_golem_helmet", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        defaultConfig.put("adventurez:stone_golem_chestplate", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        defaultConfig.put("adventurez:stone_golem_leggings", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        defaultConfig.put("adventurez:stone_golem_boots", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());

        defaultConfig.put("adventurez:ender_flute", EnchantingTypes.GENERIC.getEnchantabilityConfig());
        defaultConfig.put("adventurez:chorus_fruit_on_a_stick", EnchantingTypes.GENERIC.getEnchantabilityConfig());

        //Wolf Armor
        defaultConfig.put("wolveswitharmor:leather_wolf_armor", EnchantingTypes.LEATHER_TIER.getEnchantabilityConfig());
        defaultConfig.put("wolveswitharmor:iron_wolf_armor", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("wolveswitharmor:golden_wolf_armor", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("wolveswitharmor:diamond_wolf_armor", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("wolveswitharmor:netherite_wolf_armor", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        //Gate of Babylon
        defaultConfig.put("gateofbabylon:netherite_dagger", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:netherite_spear", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:netherite_broadsword", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:netherite_rapier", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:netherite_haladie", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:netherite_waraxe", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:netherite_katana", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:netherite_bow", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:netherite_shield", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:netherite_yoyo", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:netherite_boomerang", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        defaultConfig.put("gateofbabylon:diamond_dagger", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:diamond_spear", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:diamond_broadsword", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:diamond_rapier", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:diamond_haladie", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:diamond_waraxe", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:diamond_katana", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:diamond_bow", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:diamond_shield", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:diamond_yoyo", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:diamond_boomerang", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());

        defaultConfig.put("gateofbabylon:golden_dagger", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:golden_spear", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:golden_broadsword", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:golden_rapier", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:golden_haladie", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:golden_waraxe", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:golden_katana", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:golden_bow", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:golden_shield", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:golden_yoyo", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:golden_boomerang", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());

        defaultConfig.put("gateofbabylon:iron_dagger", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:iron_spear", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:iron_broadsword", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:iron_rapier", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:iron_haladie", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:iron_waraxe", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:iron_katana", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:iron_bow", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:iron_shield", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:iron_yoyo", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:iron_boomerang", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());

        defaultConfig.put("gateofbabylon:stone_dagger", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:stone_spear", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:stone_broadsword", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:stone_rapier", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:stone_haladie", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:stone_waraxe", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:stone_katana", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:stone_bow", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:stone_shield", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:stone_yoyo", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:stone_boomerang", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());

        defaultConfig.put("gateofbabylon:wooden_dagger", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:wooden_spear", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:wooden_broadsword", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:wooden_rapier", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:wooden_haladie", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:wooden_waraxe", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:wooden_katana", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:wooden_bow", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:wooden_shield", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:wooden_yoyo", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("gateofbabylon:wooden_boomerang", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());

        //Dragon Loot
        defaultConfig.put("dragonloot:dragon_helmet", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        defaultConfig.put("dragonloot:dragon_chestplate", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        defaultConfig.put("dragonloot:dragon_leggings", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        defaultConfig.put("dragonloot:dragon_boots", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        defaultConfig.put("dragonloot:dragon_pickaxe", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        defaultConfig.put("dragonloot:dragon_shovel", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        defaultConfig.put("dragonloot:dragon_axe", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        defaultConfig.put("dragonloot:dragon_hoe", EnchantingTypes.DRAGON.getEnchantabilityConfig());
        defaultConfig.put("dragonloot:dragon_sword", EnchantingTypes.DRAGON.getEnchantabilityConfig());

        EnchantabilityConfig dragonExtraConfig = new EnchantabilityConfig(6, 1.0f);
        defaultConfig.put("dragonloot:dragon_crossbow", dragonExtraConfig);
        defaultConfig.put("dragonloot:dragon_bow", dragonExtraConfig);
        defaultConfig.put("dragonloot:dragon_trident", dragonExtraConfig);

        //Rat Mischeif
        defaultConfig.put("ratsmischief:rat_mask", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());

        //Farmer's Delight
        defaultConfig.put("farmersdelight:flint_knife", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("farmersdelight:iron_knife", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("farmersdelight:golden_knife", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("farmersdelight:diamond_knife", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("farmersdelight:netherite_knife", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        //Consistency+
        defaultConfig.put("consistency_plus:turtle_chestplate", EnchantingTypes.TURTLE_HELMET.getEnchantabilityConfig());
        defaultConfig.put("consistency_plus:turtle_leggings", EnchantingTypes.TURTLE_HELMET.getEnchantabilityConfig());
        defaultConfig.put("consistency_plus:turtle_boots", EnchantingTypes.TURTLE_HELMET.getEnchantabilityConfig());

        //ValleyCraft
        defaultConfig.put("valley:rg_helmet", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:rg_chestplate", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:rg_leggings", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:rg_boots", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:rg_sword", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:rg_axe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:rg_shovel", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:rg_hoe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:rg_pickaxe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());

        defaultConfig.put("valley:wood_knife", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:stone_knife", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:iron_knife", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:golden_knife", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:diamond_knife", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:netherite_knife", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:rg_knife", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:bone_knife", EnchantingTypes.BONE_TIER.getEnchantabilityConfig());

        defaultConfig.put("valley:wood_sickle", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:stone_sickle", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:iron_sickle", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:golden_sickle", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:diamond_sickle", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:netherite_sickle", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:rg_sickle", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());

        defaultConfig.put("valley:wood_hatchet", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:stone_hatchet", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:iron_hatchet", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:golden_hatchet", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:diamond_hatchet", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:netherite_hatchet", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:rg_hatchet", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());

        defaultConfig.put("valley:turtle_chestplate", EnchantingTypes.TURTLE_HELMET.getEnchantabilityConfig());
        defaultConfig.put("valley:turtle_leggings", EnchantingTypes.TURTLE_HELMET.getEnchantabilityConfig());
        defaultConfig.put("valley:turtle_boots", EnchantingTypes.TURTLE_HELMET.getEnchantabilityConfig());

        defaultConfig.put("valley:tongs", EnchantingTypes.GENERIC.getEnchantabilityConfig());
        defaultConfig.put("valley:lumber_axe", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("valley:fur_chestplate", EnchantingTypes.LEATHER_TIER.getEnchantabilityConfig());

        // Impaled
        defaultConfig.put("impaled:pitchfork", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("impaled:atlan", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("impaled:elder_trident", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("impaled:hellfork", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        // Harvest Scythes
        defaultConfig.put("harvest_scythes:wooden_scythe", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("harvest_scythes:stone_scythe", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("harvest_scythes:iron_scythe", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("harvest_scythes:golden_scythe", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("harvest_scythes:diamond_scythe", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("harvest_scythes:netherite_scythe", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        defaultConfig.put("harvest_scythes:wooden_machete", EnchantingTypes.WOOD_TIER.getEnchantabilityConfig());
        defaultConfig.put("harvest_scythes:stone_machete", EnchantingTypes.STONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("harvest_scythes:iron_machete", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("harvest_scythes:golden_machete", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("harvest_scythes:diamond_machete", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("harvest_scythes:netherite_machete", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());

        // Go Fish
        defaultConfig.put("gofish:skeletal_rod", EnchantingTypes.BONE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gofish:blaze_rod", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("gofish:soul_rod", EnchantingTypes.NETHERITE_TIER.getEnchantabilityConfig());
        defaultConfig.put("gofish:diamond_reinforced_rod", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("gofish:ender_rod", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("gofish:matrix_rod", EnchantingTypes.DIAMOND_TIER.getEnchantabilityConfig());
        defaultConfig.put("gofish:frosted_rod", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("gofish:slime_rod", EnchantingTypes.IRON_TIER.getEnchantabilityConfig());
        defaultConfig.put("gofish:celestial_rod", EnchantingTypes.GOLD_TIER.getEnchantabilityConfig());

        // Additional Additions
        defaultConfig.put("additionaladditions:rose_gold_helmet", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:rose_gold_chestplate", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:rose_gold_leggings", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:rose_gold_boots", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:rose_gold_sword", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:rose_gold_axe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:rose_gold_shovel", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:rose_gold_hoe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:rose_gold_pickaxe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());

        defaultConfig.put("additionaladditions:gilded_netherite_helmet", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:gilded_netherite_chestplate", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:gilded_netherite_leggings", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:gilded_netherite_boots", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:gilded_netherite_sword", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:gilded_netherite_axe", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:gilded_netherite_shovel", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:gilded_netherite_hoe", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());
        defaultConfig.put("additionaladditions:gilded_netherite_pickaxe", EnchantingTypes.NETHERITE_GILDED.getEnchantabilityConfig());

        defaultConfig.put("additionaladditions:crossbow_with_spyglass", EnchantingTypes.CROSSBOW.getEnchantabilityConfig());

        // Oxidized
        defaultConfig.put("oxidized:rose_gold_sword", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("oxidized:rose_gold_axe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("oxidized:rose_gold_shovel", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("oxidized:rose_gold_hoe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());
        defaultConfig.put("oxidized:rose_gold_pickaxe", EnchantingTypes.ROSE_GOLD_TIER.getEnchantabilityConfig());

        // Conjuring
        defaultConfig.put("conjuring:soul_alloy_sword", EnchantingTypes.SOUL_ALLOY.getEnchantabilityConfig());
        defaultConfig.put("conjuring:soul_alloy_shovel", EnchantingTypes.SOUL_ALLOY.getEnchantabilityConfig());
        defaultConfig.put("conjuring:soul_alloy_hatchet", EnchantingTypes.SOUL_ALLOY.getEnchantabilityConfig());
        defaultConfig.put("conjuring:soul_alloy_pickaxe", EnchantingTypes.SOUL_ALLOY.getEnchantabilityConfig());

        File directory = configFile.getParentFile();
        if(directory.exists()) {
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(defaultConfig, writer);
            } catch (IOException e) {
                System.out.println("INDEXED: Could not generate new config file!");
                throw new RuntimeException("Could not save config file", e);
            }
        } else {
            System.out.println("INDEXED: Directory does not exist!");
        }

    }
}
