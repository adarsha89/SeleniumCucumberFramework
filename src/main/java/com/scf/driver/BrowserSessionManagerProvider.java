package com.scf.driver;

import com.scf.config.ConfigManager;

/**
 * Holds the single JVM-wide {@link BrowserSessionManager}, sized from the
 * {@code MAX_BROWSER_SESSIONS} config value.
 */
public final class BrowserSessionManagerProvider {

    private static final int MAX_BROWSER_SESSIONS =
            ConfigManager.get().getInt("MAX_BROWSER_SESSIONS", 4);

    private static final BrowserSessionManager MANAGER =
            new BrowserSessionManager(MAX_BROWSER_SESSIONS);

    private BrowserSessionManagerProvider() {
    }

    public static BrowserSessionManager get() {
        return MANAGER;
    }
}
