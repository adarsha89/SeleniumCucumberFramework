package com.scf.driver;

import org.openqa.selenium.WebDriver;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Holds the current thread's {@link WebDriver}s in a {@link ThreadLocal}.
 * <p>
 * Cucumber (via JUnit Platform's parallel execution) can run scenarios on
 * multiple threads simultaneously. If we kept drivers in a plain static
 * field, two parallel scenarios would fight over the same browser. A
 * ThreadLocal gives each thread its own isolated drivers instead.
 * <p>
 * A thread can own several drivers at once, keyed by an identifier. One of
 * them is "current" - that's the one {@link #getDriver()} returns - and
 * {@link #switchTo(Integer)} changes which one it is.
 */
public final class DriverManager {

    private static final ThreadLocal<Map<Integer, WebDriver>> MAP_OF_DRIVERS =
            ThreadLocal.withInitial(LinkedHashMap::new);
    private static final ThreadLocal<Integer> CURRENT_IDENTIFIER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        Integer identifier = CURRENT_IDENTIFIER.get();
        WebDriver driver = identifier == null ? null : MAP_OF_DRIVERS.get().get(identifier);
        if (driver == null) {
            throw new IllegalStateException(
                    "No WebDriver has been initialized for this thread. Did Hooks.setUp() run?");
        }
        return driver;
    }

    public static WebDriver getDriver(Integer identifier) {
        WebDriver driver = MAP_OF_DRIVERS.get().get(identifier);
        if (driver == null) {
            throw new IllegalStateException("No active browser session for identifier " + identifier);
        }
        return driver;
    }

    public static boolean hasDriver(Integer identifier) {
        return MAP_OF_DRIVERS.get().containsKey(identifier);
    }

    public static Integer currentIdentifier() {
        return CURRENT_IDENTIFIER.get();
    }

    /** Registers a driver and makes it the current one. */
    public static void addToDriverMap(WebDriver driver, Integer identifier) {
        MAP_OF_DRIVERS.get().put(identifier, driver);
        CURRENT_IDENTIFIER.set(identifier);
    }

    /** Lets a test act on a previously started session again after starting another one. */
    public static void switchTo(Integer identifier) {
        if (!hasDriver(identifier)) {
            throw new IllegalStateException("No active browser session for identifier " + identifier);
        }
        CURRENT_IDENTIFIER.set(identifier);
    }

    /** Identifiers of sessions still open on the current thread, in the order they were started. */
    public static Set<Integer> activeIdentifiers() {
        return new LinkedHashSet<>(MAP_OF_DRIVERS.get().keySet());
    }

    /**
     * Quits the driver for this identifier and removes it from the map.
     *
     * @return true if a driver was registered under this identifier
     */
    public static boolean quitDriver(Integer identifier) {
        Map<Integer, WebDriver> drivers = MAP_OF_DRIVERS.get();
        WebDriver driver = drivers.remove(identifier);
        if (identifier.equals(CURRENT_IDENTIFIER.get())) {
            CURRENT_IDENTIFIER.remove();
        }
        if (drivers.isEmpty()) {
            MAP_OF_DRIVERS.remove();
        }
        if (driver == null) {
            return false;
        }
        driver.quit();
        return true;
    }
}
