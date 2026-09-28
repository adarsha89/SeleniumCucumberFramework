package com.scf.stepdefinitions;

import com.scf.builder.CheckoutInfoBuilder;
import com.scf.config.ConfigManager;
import com.scf.context.ScenarioContext;
import com.scf.models.CheckoutInfo;
import com.scf.pages.CartPage;
import com.scf.pages.CheckoutCompletePage;
import com.scf.pages.CheckoutStepOnePage;
import com.scf.pages.CheckoutStepTwoPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CartCheckoutSteps {

    private final ScenarioContext context;
    private final ConfigManager config = ConfigManager.get();

    public CartCheckoutSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I proceed to checkout")
    public void iProceedToCheckout() {
        CartPage cartPage = context.getCurrentPage(CartPage.class);
        CheckoutStepOnePage stepOne = cartPage.proceedToCheckout(config.explicitWaitSeconds());
        context.setCurrentPage(CheckoutStepOnePage.class, stepOne);
    }

    @When("I fill in the checkout information and continue")
    public void iFillInTheCheckoutInformationAndContinue() {
        CheckoutInfo info = CheckoutInfoBuilder.aCheckoutInfo().build();
        CheckoutStepOnePage stepOne = context.getCurrentPage(CheckoutStepOnePage.class);
        CheckoutStepTwoPage stepTwo = stepOne.fillInfoAndContinue(info, config.explicitWaitSeconds());
        context.setCurrentPage(CheckoutStepTwoPage.class, stepTwo);
    }

    @When("I fill in the checkout information with:")
    public void iFillInTheCheckoutInformationWith(DataTable table) {
        CheckoutStepOnePage stepOne = context.getCurrentPage(CheckoutStepOnePage.class);
        CheckoutStepTwoPage stepTwo = stepOne.fillInfoAndContinue(toCheckoutInfo(table), config.explicitWaitSeconds());
        context.setCurrentPage(CheckoutStepTwoPage.class, stepTwo);
    }

    @When("I try to continue with the checkout information:")
    public void iTryToContinueWithTheCheckoutInformation(DataTable table) {
        CheckoutStepOnePage stepOne = context.getCurrentPage(CheckoutStepOnePage.class);
        context.setCurrentPage(CheckoutStepOnePage.class, stepOne.attemptContinueWith(toCheckoutInfo(table)));
    }

    @When("I try to continue without filling in the checkout information")
    public void iTryToContinueWithoutFillingInTheCheckoutInformation() {
        CheckoutStepOnePage stepOne = context.getCurrentPage(CheckoutStepOnePage.class);
        context.setCurrentPage(CheckoutStepOnePage.class, stepOne.attemptContinueWithoutFilling());
    }

    @When("I finish the order")
    public void iFinishTheOrder() {
        CheckoutStepTwoPage stepTwo = context.getCurrentPage(CheckoutStepTwoPage.class);
        CheckoutCompletePage complete = stepTwo.finishOrder(config.explicitWaitSeconds());
        context.setCurrentPage(CheckoutCompletePage.class, complete);
    }

    @Then("I should see the order confirmation {string}")
    public void iShouldSeeTheOrderConfirmation(String expectedMessage) {
        assertThat(context.getCurrentPage(CheckoutCompletePage.class).getConfirmationMessage())
                .isEqualTo(expectedMessage);
    }

    @Then("the cart should contain exactly these products:")
    public void theCartShouldContainExactlyTheseProducts(List<String> expectedProducts) {
        assertThat(context.getCurrentPage(CartPage.class).getItemNames())
                .containsExactlyInAnyOrderElementsOf(expectedProducts);
    }

    @Then("I should see the checkout error {string}")
    public void iShouldSeeTheCheckoutError(String expectedMessage) {
        assertThat(context.getCurrentPage(CheckoutStepOnePage.class).getErrorMessage()).isEqualTo(expectedMessage);
    }

    @Then("I should see a checkout error message")
    public void iShouldSeeACheckoutErrorMessage() {
        assertThat(context.getCurrentPage(CheckoutStepOnePage.class).hasErrorMessage()).isTrue();
    }

    /**
     * Reads a two-column "field | value" table. Blank cells come through as
     * null, which the page object treats as "leave this field empty".
     */
    private CheckoutInfo toCheckoutInfo(DataTable table) {
        Map<String, String> fields = table.asMap();
        return CheckoutInfoBuilder.aCheckoutInfo()
                .withFirstName(fields.get("First Name"))
                .withLastName(fields.get("Last Name"))
                .withPostalCode(fields.get("Postal Code"))
                .build();
    }
}
