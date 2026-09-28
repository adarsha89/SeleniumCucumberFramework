package com.scf.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CheckoutStepTwoPage extends BasePage {

    @FindBy(id = "finish")
    private WebElement finishButton;

    @FindBy(className = "summary_subtotal_label")
    private WebElement subtotalLabel;

    @FindBy(className = "summary_total_label")
    private WebElement totalLabel;

    public CheckoutStepTwoPage(WebDriver driver, int explicitWaitSeconds) {
        super(driver, explicitWaitSeconds);
    }

    public String getSubtotalText() {
        return waitVisible(subtotalLabel).getText();
    }

    public String getTotalText() {
        return waitVisible(totalLabel).getText();
    }

    public CheckoutCompletePage finishOrder(int explicitWaitSeconds) {
        click(finishButton);
        return new CheckoutCompletePage(driver, explicitWaitSeconds);
    }
}
