package com.astrazoey.indexed;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ConfigMain {

    private static final float MOD_VERSION = 1.5f;

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("indexed.properties");

    private static final String MOD_VERSION_KEY = "mod-version";
    private static float modVersion = MOD_VERSION;

    public static final String ENABLE_CONFIG_UPDATES_KEY = "enable-config-updates";
    private static boolean enableConfigUpdates = true;

    public static final String ENABLE_ENCHANTMENT_NERFS_KEY = "enable-enchantment-nerfs";
    public static boolean enableEnchantmentNerfs = true;

    public static final String ENABLE_VILLAGER_NERFS_KEY = "enable-villager-nerfs";
    public static boolean enableVillagerNerfs = true;

    public static final String ENABLE_QUICK_FLIGHT_KEY = "enable-quick-flight-enchantment";
    public static boolean enableQuickFlight = false;

    public static final String MENDING_IS_TREASURE_KEY = "mending-is-treasure";
    public static boolean mendingIsTreasure = false;

    public static void save() {
        Properties props = new Properties();
        read(props);

        if (!Files.exists(CONFIG_PATH)) {
            try {
                Files.createFile(CONFIG_PATH);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        try (OutputStream out = Files.newOutputStream(CONFIG_PATH)) {
            props.store(out, "Indexed Configuration");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(boolean modOutOfDate) {

        if(modOutOfDate && Files.exists(CONFIG_PATH)) {
            try {
                System.out.println("Mod is out of date! Resetting file!");
                modVersion = MOD_VERSION; //update mod version
                Files.delete(CONFIG_PATH);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to delete config file.");
                return;
            }
        }

        if (!Files.exists(CONFIG_PATH)) {
            try {
                Files.createFile(CONFIG_PATH);
                save();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        Properties props = new Properties();
        try (InputStream stream = Files.newInputStream(CONFIG_PATH)) {
            props.load(stream);
            assign(props);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static boolean isOutOfDate() {
        return (modVersion != MOD_VERSION) && enableConfigUpdates;
    }


    public static void read(Properties props) {
        props.setProperty(MOD_VERSION_KEY, String.valueOf(modVersion));
        props.setProperty(ENABLE_CONFIG_UPDATES_KEY, String.valueOf(enableConfigUpdates));
        props.setProperty(ENABLE_ENCHANTMENT_NERFS_KEY, String.valueOf(enableEnchantmentNerfs));
        props.setProperty(ENABLE_VILLAGER_NERFS_KEY, String.valueOf(enableVillagerNerfs));
        props.setProperty(ENABLE_QUICK_FLIGHT_KEY, String.valueOf(enableQuickFlight));
        props.setProperty(MENDING_IS_TREASURE_KEY, String.valueOf(mendingIsTreasure));
    }

    public static void assign(Properties props) {
        modVersion = defaultFloat(props.getProperty(MOD_VERSION_KEY), MOD_VERSION);
        enableConfigUpdates = defaultBoolean(props.getProperty(ENABLE_CONFIG_UPDATES_KEY), true);
        enableEnchantmentNerfs = defaultBoolean(props.getProperty(ENABLE_ENCHANTMENT_NERFS_KEY), true);
        enableVillagerNerfs = defaultBoolean(props.getProperty(ENABLE_VILLAGER_NERFS_KEY), true);
        enableQuickFlight = defaultBoolean(props.getProperty(ENABLE_QUICK_FLIGHT_KEY), false);
        mendingIsTreasure = defaultBoolean(props.getProperty(MENDING_IS_TREASURE_KEY), false);
    }

    private static boolean defaultBoolean(String bool, boolean defaultOption) {
        return bool == null ? defaultOption : Boolean.parseBoolean(bool);
    }

    private static float defaultFloat(String amount, float defaultOption) {
        return amount == null ? defaultOption : Float.parseFloat(amount);
    }

}
