package com.scf.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CheckoutCompletePage extends BasePage {

    @FindBy(className = "complete-header")
    private WebElement completeHeader;

    public CheckoutCompletePage(WebDriver driver, int explicitWaitSeconds) {
        super(driver, explicitWaitSeconds);
    }

    public String getConfirmationMessage() {
        return waitVisible(completeHeader).getText();
    }
}
