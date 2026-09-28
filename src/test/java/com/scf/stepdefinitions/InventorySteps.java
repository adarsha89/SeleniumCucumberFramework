package com.scf.stepdefinitions;

import com.scf.config.ConfigManager;
import com.scf.context.ScenarioContext;
import com.scf.pages.CartPage;
import com.scf.pages.InventoryPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InventorySteps {

    private final ScenarioContext context;
    private final ConfigManager config = ConfigManager.get();

    public InventorySteps(ScenarioContext context) {
        this.context = context;
    }

    @Then("the inventory page should show {int} products")
    public void theInventoryPageShouldShowProducts(int expectedCount) {
        assertThat(context.getCurrentPage(InventoryPage.class).productCount()).isEqualTo(expectedCount);
    }

    @When("I add {string} to the cart")
    public void iAddToTheCart(String productName) {
        InventoryPage inventoryPage = context.getCurrentPage(InventoryPage.class);
        inventoryPage.addProductToCartByName(productName);
    }

    @When("I add the following products to the cart:")
    public void iAddTheFollowingProductsToTheCart(List<String> productNames) {
        InventoryPage inventoryPage = context.getCurrentPage(InventoryPage.class);
        productNames.forEach(inventoryPage::addProductToCartByName);
    }

    @Then("the cart badge should show {int} item(s)")
    public void theCartBadgeShouldShowItem(int expectedCount) {
        assertThat(context.getCurrentPage(InventoryPage.class).getCartBadgeCount()).isEqualTo(expectedCount);
    }

    @When("I sort products by {string}")
    public void iSortProductsBy(String sortOption) {
        context.getCurrentPage(InventoryPage.class).sortBy(sortOption);
    }

    @Then("the first product listed should be {string}")
    public void theFirstProductListedShouldBe(String expectedProductName) {
        assertThat(context.getCurrentPage(InventoryPage.class).getProductNamesInOrder().get(0))
                .isEqualTo(expectedProductName);
    }

    @When("I go to the cart page")
    public void iGoToTheCartPage() {
        CartPage cartPage = context.getCurrentPage(InventoryPage.class).goToCart(config.explicitWaitSeconds());
        context.setCurrentPage(CartPage.class, cartPage);
    }
}
