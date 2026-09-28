package com.scf.factory;

import com.scf.pages.BasePage;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory pattern: centralizes construction of page objects so step
 * definitions never call {@code new SomePage(driver, wait)} directly.
 * <p>
 * Two benefits this buys us in practice:
 * <ul>
 *   <li>Every page object's constructor signature can change in one place.</li>
 *   <li>Pages are cached per-thread-per-driver so repeated navigation to the
 *       same page class in a scenario reuses one instance instead of
 *       re-running {@code PageFactory.initElements} every time.</li>
 * </ul>
 * Most navigation in this framework happens by page methods returning the
 * next page directly (e.g. {@code loginPage.submitLogin()} returns an
 * {@code InventoryPage}), which is the more idiomatic Selenium style. This
 * factory is for the cases where a step needs a page object it didn't
 * navigate to directly (e.g. re-fetching the current page after a browser
 * back/forward navigation, or building the "home" page object at the start
 * of a scenario).
 */
public final class PageObjectFactory {

    private final WebDriver driver;
    private final int explicitWaitSeconds;
    private final Map<Class<? extends BasePage>, BasePage> cache = new HashMap<>();

    public PageObjectFactory(WebDriver driver, int explicitWaitSeconds) {
        this.driver = driver;
        this.explicitWaitSeconds = explicitWaitSeconds;
    }

    @SuppressWarnings("unchecked")
    public <T extends BasePage> T get(Class<T> pageClass) {
        return (T) cache.computeIfAbsent(pageClass, this::instantiate);
    }

    private <T extends BasePage> T instantiate(Class<T> pageClass) {
        try {
            Constructor<T> constructor = pageClass.getDeclaredConstructor(WebDriver.class, int.class);
            return constructor.newInstance(driver, explicitWaitSeconds);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Page object " + pageClass.getSimpleName()
                            + " must expose a (WebDriver, int) constructor", e);
        }
    }

    public void clearCache() {
        cache.clear();
    }
}
