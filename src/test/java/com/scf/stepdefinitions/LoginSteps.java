package com.scf.stepdefinitions;

import com.scf.builder.UserBuilder;
import com.scf.config.ConfigManager;
import com.scf.context.ScenarioContext;
import com.scf.models.User;
import com.scf.pages.InventoryPage;
import com.scf.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginSteps {

    private final ScenarioContext context;
    private final ConfigManager config = ConfigManager.get();

    public LoginSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("I am on the SauceDemo login page")
    public void iAmOnTheSaucedemoLoginPage() {
        LoginPage loginPage = context.getPageObjectFactory().get(LoginPage.class);
        loginPage.open(config.baseUrl());
        context.setCurrentPage(LoginPage.class, loginPage);
    }

    @Given("I am logged in as the {string} user")
    public void iAmLoggedInAsTheUser(String userKey) {
        iAmOnTheSaucedemoLoginPage();
        User user = resolveUser(userKey);
        InventoryPage inventoryPage = context.getCurrentPage(LoginPage.class).loginAs(user, config.explicitWaitSeconds());
        context.setCurrentPage(InventoryPage.class, inventoryPage);
    }

    @When("I log in as the {string} user")
    public void iLogInAsTheUser(String userKey) {
        User user = resolveUser(userKey);
        LoginPage loginPage = context.getCurrentPage(LoginPage.class);

        if (userKey.equals("locked_out")) {
            context.setCurrentPage(LoginPage.class, loginPage.attemptLogin(user));
        } else {
            InventoryPage inventoryPage = loginPage.loginAs(user, config.explicitWaitSeconds());
            context.setCurrentPage(InventoryPage.class, inventoryPage);
        }
    }

    @When("I log in using the credentials from the TEST_USER_EMAIL environment variable")
    public void iLogInUsingEnvCredentials() {
        User user = UserBuilder.envConfiguredUser();
        LoginPage loginPage = context.getCurrentPage(LoginPage.class);
        context.setCurrentPage(LoginPage.class, loginPage.attemptLogin(user));
    }

    @Then("I should be redirected to the inventory page")
    public void iShouldBeRedirectedToTheInventoryPage() {
        InventoryPage inventoryPage = context.getCurrentPage(InventoryPage.class);
        assertThat(inventoryPage.isLoaded()).isTrue();
    }

    @Then("I should see the login error {string}")
    public void iShouldSeeTheLoginError(String expectedMessage) {
        LoginPage loginPage = context.getCurrentPage(LoginPage.class);
        assertThat(loginPage.hasErrorMessage()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains(expectedMessage);
    }

    @Then("I should see a login error message")
    public void iShouldSeeALoginErrorMessage() {
        assertThat(context.getCurrentPage(LoginPage.class).hasErrorMessage()).isTrue();
    }

    private User resolveUser(String userKey) {
        return switch (userKey) {
            case "standard" -> UserBuilder.standardUser();
            case "locked_out" -> UserBuilder.lockedOutUser();
            case "problem" -> UserBuilder.problemUser();
            case "performance_glitch" -> UserBuilder.performanceGlitchUser();
            default -> throw new IllegalArgumentException("Unknown user key: " + userKey);
        };
    }
}
