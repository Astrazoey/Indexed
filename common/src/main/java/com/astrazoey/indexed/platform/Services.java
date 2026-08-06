package com.astrazoey.indexed.platform;

import java.util.ServiceLoader;

/**
 * Loads platform-specific implementations via {@link ServiceLoader}.
 * Each loader module provides its own implementation in
 * {@code META-INF/services/}.
 */
public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
