package com.scf.pages;

import com.scf.models.CheckoutInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CheckoutStepOnePage extends BasePage {

    @FindBy(id = "first-name")
    private WebElement firstNameInput;

    @FindBy(id = "last-name")
    private WebElement lastNameInput;

    @FindBy(id = "postal-code")
    private WebElement postalCodeInput;

    @FindBy(id = "continue")
    private WebElement continueButton;

    private static final By ERROR_MESSAGE = By.cssSelector("h3[data-test='error']");

    public CheckoutStepOnePage(WebDriver driver, int explicitWaitSeconds) {
        super(driver, explicitWaitSeconds);
    }

    /** Fills the form using a {@link CheckoutInfo} assembled via {@code CheckoutInfoBuilder}. */
    public CheckoutStepTwoPage fillInfoAndContinue(CheckoutInfo info, int explicitWaitSeconds) {
        type(firstNameInput, info.getFirstName());
        type(lastNameInput, info.getLastName());
        type(postalCodeInput, info.getPostalCode());
        click(continueButton);
        return new CheckoutStepTwoPage(driver, explicitWaitSeconds);
    }

    /**
     * Fills only the fields that have a value, then clicks Continue expecting
     * to stay on this page - used to exercise the required-field validation.
     */
    public CheckoutStepOnePage attemptContinueWith(CheckoutInfo info) {
        typeIfPresent(firstNameInput, info.getFirstName());
        typeIfPresent(lastNameInput, info.getLastName());
        typeIfPresent(postalCodeInput, info.getPostalCode());
        click(continueButton);
        return this;
    }

    private void typeIfPresent(WebElement element, String text) {
        if (text != null && !text.isEmpty()) {
            type(element, text);
        }
    }

    public CheckoutStepOnePage attemptContinueWithoutFilling() {
        click(continueButton);
        return this;
    }

    public boolean hasErrorMessage() {
        return isDisplayed(ERROR_MESSAGE);
    }

    public String getErrorMessage() {
        return waitVisible(driver.findElement(ERROR_MESSAGE)).getText();
    }
}
