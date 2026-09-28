package com.scf.pages;

import com.scf.models.User;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

    @FindBy(id = "user-name")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    private static final By ERROR_MESSAGE = By.cssSelector("h3[data-test='error']");

    public LoginPage(WebDriver driver, int explicitWaitSeconds) {
        super(driver, explicitWaitSeconds);
    }

    public void open(String baseUrl) {
        driver.get(baseUrl);
    }

    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    public InventoryPage submitLogin(int explicitWaitSeconds) {
        click(loginButton);
        wait.until(ExpectedConditions.urlContains("inventory.html"));
        return new InventoryPage(driver, explicitWaitSeconds);
    }

    /** Convenience method combining the three actions above - accepts a {@link User} built via {@code UserBuilder}. */
    public InventoryPage loginAs(User user, int explicitWaitSeconds) {
        enterUsername(user.getUsername());
        enterPassword(user.getPassword());
        return submitLogin(explicitWaitSeconds);
    }

    /** Attempts a login expected to fail; stays on the login page. */
    public LoginPage attemptLogin(User user) {
        enterUsername(user.getUsername());
        enterPassword(user.getPassword());
        click(loginButton);
        return this;
    }

    public boolean hasErrorMessage() {
        return isDisplayed(ERROR_MESSAGE);
    }

    public String getErrorMessage() {
        return waitVisible(driver.findElement(ERROR_MESSAGE)).getText();
    }
}
