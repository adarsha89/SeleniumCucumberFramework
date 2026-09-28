package com.scf.driver;

import com.scf.config.ConfigManager;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

/**
 * Starts and stops browser sessions for the current thread, identified by an
 * integer so a single scenario can drive several browsers at once (e.g. a
 * buyer and a seller, or two users seeing each other's changes).
 * <p>
 * Every session holds a permit from {@link BrowserSessionManager} for as long
 * as its browser is open.
 */
public class BrowserSession {

    public void start(Integer identifier) {
        if (DriverManager.hasDriver(identifier)) {
            throw new IllegalStateException("Browser session " + identifier + " is already running");
        }
        BrowserSessionManager browserSessionManager = BrowserSessionManagerProvider.get();
        browserSessionManager.acquire();
        try {
            DriverManager.addToDriverMap(createDriver(), identifier);
        } catch (RuntimeException ex) {
            browserSessionManager.release();
            throw ex;
        }
    }

    public void stop(Integer identifier) {
        if (!DriverManager.hasDriver(identifier)) {
            return;
        }
        try {
            DriverManager.quitDriver(identifier);
        } finally {
            BrowserSessionManagerProvider.get().release();
        }
    }

    /** Quits every session still open on this thread, e.g. at the end of a scenario. */
    public void stopAll() {
        for (Integer identifier : DriverManager.activeIdentifiers()) {
            try {
                stop(identifier);
            } catch (RuntimeException ignored) {
                // keep closing the remaining sessions; a failed quit must not leak the others
            }
        }
    }

    private static WebDriver createDriver() {
        ConfigManager config = ConfigManager.get();
        WebDriver driver = DriverFactory.createDriver(BrowserType.fromString(config.browser()), config.headless());
        try {
            driver.manage().window().maximize();
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.pageLoadTimeoutSeconds()));
        } catch (RuntimeException ex) {
            driver.quit();
            throw ex;
        }
        return driver;
    }
}
