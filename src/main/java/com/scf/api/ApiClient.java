package com.scf.api;

import com.scf.config.ConfigManager;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Singleton pattern: one shared, pre-configured REST client for the whole
 * suite, so base URL / auth wiring lives in exactly one place.
 * <p>
 * Demonstrates API_AUTH_USERNAME / API_AUTH_PASSWORD against httpbin.org's
 * {@code /basic-auth/{user}/{passwd}} endpoint, which validates whatever
 * Basic Auth credentials it receives and echoes back the authenticated user -
 * a safe, side-effect-free way to prove credential wiring works end to end.
 */
public final class ApiClient {

    private static final ApiClient INSTANCE = new ApiClient();

    private final String baseUrl;
    private final String basicAuthPath;
    private final String username;
    private final String password;

    private ApiClient() {
        ConfigManager config = ConfigManager.get();
        this.baseUrl = config.get("api.auth.base.url", "https://httpbin.org");
        this.basicAuthPath = config.get("api.auth.basic.path", "/basic-auth/{user}/{passwd}");
        this.username = config.apiAuthUsername();
        this.password = config.apiAuthPassword();
    }

    public static ApiClient get() {
        return INSTANCE;
    }

    private RequestSpecification baseRequest() {
        return given().baseUri(baseUrl).relaxedHTTPSValidation();
    }

    /** Calls /basic-auth/{user}/{passwd} using the configured credentials. */
    public Response authenticateWithConfiguredCredentials() {
        return baseRequest()
                .auth().preemptive().basic(username, password)
                .when()
                .get(basicAuthPath, username, password);
    }

    /** Calls /basic-auth/{user}/{passwd} with arbitrary credentials, for negative-auth scenarios. */
    public Response authenticateWith(String suppliedUsername, String suppliedPassword) {
        return baseRequest()
                .auth().preemptive().basic(suppliedUsername, suppliedPassword)
                .when()
                .get(basicAuthPath, username, password);
    }

    public Response getWithoutAuth() {
        return baseRequest()
                .when()
                .get(basicAuthPath, username, password);
    }
}
