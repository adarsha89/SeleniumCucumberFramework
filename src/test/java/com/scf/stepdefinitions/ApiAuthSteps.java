package com.scf.stepdefinitions;

import com.scf.api.ApiClient;
import com.scf.context.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiAuthSteps {

    private final ScenarioContext context;

    public ApiAuthSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I call the basic-auth endpoint with the configured API credentials")
    public void iCallTheBasicAuthEndpointWithTheConfiguredApiCredentials() {
        context.setLastApiResponse(ApiClient.get().authenticateWithConfiguredCredentials());
    }

    @When("I call the basic-auth endpoint with username {string} and password {string}")
    public void iCallTheBasicAuthEndpointWithUsernameAndPassword(String username, String password) {
        context.setLastApiResponse(ApiClient.get().authenticateWith(username, password));
    }

    @When("I call the basic-auth endpoint without credentials")
    public void iCallTheBasicAuthEndpointWithoutCredentials() {
        context.setLastApiResponse(ApiClient.get().getWithoutAuth());
    }

    @Then("the API response status should be {int}")
    public void theApiResponseStatusShouldBe(int expectedStatus) {
        Response response = context.getLastApiResponse();
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
    }

    @Then("the API response should confirm the authenticated user")
    public void theApiResponseShouldConfirmTheAuthenticatedUser() {
        Response response = context.getLastApiResponse();
        assertThat(response.jsonPath().getBoolean("authenticated")).isTrue();
    }
}
