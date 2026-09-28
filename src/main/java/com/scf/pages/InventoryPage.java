package com.scf.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class InventoryPage extends BasePage {

    @FindBy(className = "title")
    private WebElement pageTitle;

    @FindBy(className = "shopping_cart_link")
    private WebElement cartLink;

    @FindBy(className = "product_sort_container")
    private WebElement sortDropdown;

    private static final By CART_BADGE = By.className("shopping_cart_badge");
    private static final By INVENTORY_ITEMS = By.className("inventory_item");

    public InventoryPage(WebDriver driver, int explicitWaitSeconds) {
        super(driver, explicitWaitSeconds);
    }

    public boolean isLoaded() {
        return waitVisible(pageTitle).getText().equalsIgnoreCase("Products");
    }

    public int productCount() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(INVENTORY_ITEMS)).size();
    }

    public InventoryPage addProductToCartByName(String productName) {
        WebElement item = findInventoryItem(productName);
        click(item.findElement(By.cssSelector("button")));
        return this;
    }

    public InventoryPage removeProductFromCartByName(String productName) {
        WebElement item = findInventoryItem(productName);
        click(item.findElement(By.cssSelector("button")));
        return this;
    }

    private WebElement findInventoryItem(String productName) {
        // Right after login the click returns before the inventory list has rendered,
        // so a bare findElements can see an empty list. Adding a product also
        // re-renders the list, so items found a moment ago can go stale - the
        // lookup is retried inside the wait until it sees a stable list.
        try {
            return wait.until(d -> {
                try {
                    return d.findElements(INVENTORY_ITEMS).stream()
                            .filter(item -> item.findElement(By.className("inventory_item_name")).getText()
                                    .equals(productName))
                            .findFirst()
                            .orElse(null);
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            });
        } catch (TimeoutException e) {
            throw new NoSuchElementExceptionWithContext(productName);
        }
    }

    public int getCartBadgeCount() {
        List<WebElement> badges = driver.findElements(CART_BADGE);
        if (badges.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(badges.get(0).getText());
    }

    public InventoryPage sortBy(String visibleOptionText) {
        waitVisible(sortDropdown);
        new org.openqa.selenium.support.ui.Select(sortDropdown).selectByVisibleText(visibleOptionText);
        return this;
    }

    public List<String> getProductNamesInOrder() {
        return driver.findElements(By.className("inventory_item_name")).stream()
                .map(WebElement::getText)
                .toList();
    }

    public CartPage goToCart(int explicitWaitSeconds) {
        click(cartLink);
        wait.until(ExpectedConditions.urlContains("cart.html"));
        return new CartPage(driver, explicitWaitSeconds);
    }

    private static final class NoSuchElementExceptionWithContext extends RuntimeException {
        NoSuchElementExceptionWithContext(String productName) {
            super("No inventory item found with name: " + productName);
        }
    }
}
