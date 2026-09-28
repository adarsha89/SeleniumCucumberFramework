package com.scf.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class CartPage extends BasePage {

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    private static final By CART_ITEM_NAMES = By.className("inventory_item_name");

    public CartPage(WebDriver driver, int explicitWaitSeconds) {
        super(driver, explicitWaitSeconds);
    }

    public List<String> getItemNames() {
        return driver.findElements(CART_ITEM_NAMES).stream().map(WebElement::getText).toList();
    }

    public int getItemCount() {
        return getItemNames().size();
    }

    public CheckoutStepOnePage proceedToCheckout(int explicitWaitSeconds) {
        click(checkoutButton);
        return new CheckoutStepOnePage(driver, explicitWaitSeconds);
    }
}
