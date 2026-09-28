package com.scf.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Shared behaviour for every page object: element waits and common actions.
 * <p>
 * Page Object Pattern: each subclass encapsulates the locators and
 * interactions for a single page/screen, so step definitions read like plain
 * English ("loginPage.login(user)") instead of raw Selenium calls, and a UI
 * change only requires editing one page class.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver, int explicitWaitSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWaitSeconds));
        // @FindBy fields are populated lazily via a proxy - see each subclass.
        PageFactory.initElements(driver, this);
    }

    protected WebElement waitVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected List<WebElement> waitVisible(List<WebElement> elements) {
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    protected void click(WebElement element) {
        waitClickable(element).click();
    }

    protected void type(WebElement element, String text) {
        WebElement el = waitVisible(element);
        el.clear();
        el.sendKeys(text);
    }

    protected boolean isDisplayed(By locator) {
        return !driver.findElements(locator).isEmpty() && driver.findElement(locator).isDisplayed();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }
}
