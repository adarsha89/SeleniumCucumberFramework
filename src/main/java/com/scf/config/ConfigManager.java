package com.scf.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central place to resolve configuration.
 * <p>
 * Resolution order (highest priority first):
 * 1. JVM system property (-Dkey=value)
 * 2. OS environment variable
 * 3. .env file at the project root
 * 4. src/test/resources/config.properties (non-secret defaults)
 * <p>
 * This is a classic Singleton: one instance loads the sources once and
 * every caller reads through the same cached view.
 */
public final class ConfigManager {

    private static final ConfigManager INSTANCE = new ConfigManager();

    private final Dotenv dotenv;
    private final Properties fileProperties = new Properties();

    private ConfigManager() {
        this.dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();
        loadPropertiesFile();
    }

    public static ConfigManager get() {
        return INSTANCE;
    }

    private void loadPropertiesFile() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                fileProperties.load(is);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
    }

    public String get(String key) {
        String value = System.getProperty(key);
        if (isBlank(value)) {
            value = System.getenv(key);
        }
        if (isBlank(value)) {
            value = dotenv.get(key);
        }
        if (isBlank(value)) {
            value = fileProperties.getProperty(key);
        }
        return value;
    }

    public String get(String key, String defaultValue) {
        String value = get(key);
        return isBlank(value) ? defaultValue : value;
    }

    public int getInt(String key, int defaultValue) {
        String value = get(key);
        return isBlank(value) ? defaultValue : Integer.parseInt(value.trim());
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return isBlank(value) ? defaultValue : Boolean.parseBoolean(value.trim());
    }

    public String require(String key) {
        String value = get(key);
        if (isBlank(value)) {
            throw new IllegalStateException(
                    "Missing required configuration value: " + key
                            + " (set it as a system property, env var, or in .env)");
        }
        return value;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // Convenience accessors for the values this framework cares about.

    public String baseUrl() {
        return require("SAUCEDEMO_BASE_URL");
    }

    public String testUserEmail() {
        return require("TEST_USER_EMAIL");
    }

    public String testUserPassword() {
        return require("TEST_USER_PASSWORD");
    }

    public String apiAuthUsername() {
        return require("API_AUTH_USERNAME");
    }

    public String apiAuthPassword() {
        return require("API_AUTH_PASSWORD");
    }

    public String browser() {
        return get("browser", get("BROWSER", "chrome"));
    }

    public boolean headless() {
        return getBoolean("headless", getBoolean("HEADLESS", false));
    }

    public int explicitWaitSeconds() {
        return getInt("explicit.wait.seconds", 10);
    }

    public int pageLoadTimeoutSeconds() {
        return getInt("pageload.timeout.seconds", 30);
    }
}
