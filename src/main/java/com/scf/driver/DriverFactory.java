package com.scf.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LoggingPreferences;

import java.util.Map;
import java.util.logging.Level;

/**
 * Factory pattern: callers ask for a {@link BrowserType} and get back a
 * ready-to-use {@link WebDriver}, without knowing anything about
 * ChromeOptions, WebDriverManager, or how each browser is wired up.
 * <p>
 * This is what makes the framework "multi-browser": every other class
 * (pages, steps, hooks) talks to {@code WebDriver}, never to
 * {@code ChromeDriver} directly, so swapping browsers is a one-line change.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver(BrowserType browserType, boolean headless) {
        return switch (browserType) {
            case CHROME -> createChromeDriver(headless);
            case FIREFOX -> createFirefoxDriver(headless);
            case EDGE -> createEdgeDriver(headless);
        };
    }

    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        options.setExperimentalOption("prefs", disablePasswordManagerPrefs());
        // Enables driver.manage().logs().get("performance") for network capture (see NetworkCapture).
        options.setCapability("goog:loggingPrefs", performanceLoggingPreferences());
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        options.addArguments("--width=1920", "--height=1080");
        // Firefox does not support Chrome DevTools Protocol the way Chromium
        // browsers do, so network capture/manipulation tests are Chromium-only
        // (see NetworkSteps for the runtime skip).
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-notifications");
        options.setExperimentalOption("prefs", disablePasswordManagerPrefs());
        options.setCapability("ms:loggingPrefs", performanceLoggingPreferences());
        return new EdgeDriver(options);
    }

    /**
     * Headed Chromium shows a native "Change your password" breach warning after
     * logging in with a leaked password (SauceDemo's is public). It steals focus
     * and swallows the next clicks, so turn the password manager off entirely.
     */
    private static Map<String, Object> disablePasswordManagerPrefs() {
        return Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false);
    }

    private static LoggingPreferences performanceLoggingPreferences() {
        LoggingPreferences prefs = new LoggingPreferences();
        prefs.enable(org.openqa.selenium.logging.LogType.PERFORMANCE, Level.ALL);
        return prefs;
    }
}
