package com.astrazoey.indexed.platform;

import java.nio.file.Path;

/**
 * Platform abstraction for loader-specific functionality.
 * Implemented in each loader module and loaded via {@link java.util.ServiceLoader}.
 */
public interface IPlatformHelper {

    /**
     * Returns the directory where config files should be stored.
     */
    Path getConfigDirectory();
}
