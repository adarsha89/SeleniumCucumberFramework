package com.scf.context;

import com.scf.factory.PageObjectFactory;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

/**
 * Dependency-injected "world" object shared across step definition classes
 * for a single scenario (wired up by Cucumber's PicoContainer support - each
 * class here is instantiated fresh per scenario and reused wherever it's a
 * constructor parameter).
 * <p>
 * This is where page objects created by one step class (e.g. LoginSteps)
 * become visible to another (e.g. InventorySteps) without static state.
 * <p>
 * A scenario can drive several browser sessions. Each session keeps its own
 * driver, page factory and current pages, and {@link #useSession(Integer, WebDriver, int)}
 * swaps which one the steps see - so existing steps work unchanged against
 * whichever browser is active.
 */
public class ScenarioContext {

    private final Map<Integer, SessionState> sessions = new HashMap<>();
    private SessionState active;
    private Object lastApiResponse;

    /**
     * Makes the given session the one steps act on, creating its state the
     * first time the session is seen.
     */
    public void useSession(Integer identifier, WebDriver driver, int explicitWaitSeconds) {
        active = sessions.computeIfAbsent(identifier,
                id -> new SessionState(driver, new PageObjectFactory(driver, explicitWaitSeconds)));
    }

    public void removeSession(Integer identifier) {
        SessionState removed = sessions.remove(identifier);
        if (removed == active) {
            active = null;
        }
    }

    /** Step classes hand off "where we are now" (e.g. LoginSteps -> InventoryPage) through this slot. */
    public <T> void setCurrentPage(Class<T> type, T page) {
        activeSession().currentPages.put(type, page);
    }

    public <T> T getCurrentPage(Class<T> type) {
        Object page = activeSession().currentPages.get(type);
        if (page == null) {
            throw new IllegalStateException(
                    "No " + type.getSimpleName() + " has been set on the current scenario yet");
        }
        return type.cast(page);
    }

    public WebDriver getDriver() {
        return active == null ? null : active.driver;
    }

    public PageObjectFactory getPageObjectFactory() {
        return activeSession().pageObjectFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> T getLastApiResponse() {
        return (T) lastApiResponse;
    }

    public void setLastApiResponse(Object lastApiResponse) {
        this.lastApiResponse = lastApiResponse;
    }

    private SessionState activeSession() {
        if (active == null) {
            throw new IllegalStateException("No browser session is active for the current scenario");
        }
        return active;
    }

    private static final class SessionState {
        private final WebDriver driver;
        private final PageObjectFactory pageObjectFactory;
        private final Map<Class<?>, Object> currentPages = new HashMap<>();

        private SessionState(WebDriver driver, PageObjectFactory pageObjectFactory) {
            this.driver = driver;
            this.pageObjectFactory = pageObjectFactory;
        }
    }
}
