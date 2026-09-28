package com.scf.driver;

/**
 * Every browser this framework knows how to drive. Adding a new browser is a
 * two-step change: add the enum constant here, then add a matching
 * {@code case} in {@link DriverFactory}.
 */
public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType fromString(String value) {
        if (value == null || value.isBlank()) {
            return CHROME;
        }
        return BrowserType.valueOf(value.trim().toUpperCase());
    }
}
