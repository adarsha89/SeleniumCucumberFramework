package com.scf.stepdefinitions;

import com.scf.context.ScenarioContext;
import com.scf.network.NetworkCapture;
import com.scf.network.NetworkConditions;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Steps for the network capture/manipulation scenarios. These only work on
 * Chromium-based browsers (Chrome, Edge) because they rely on the Chrome
 * DevTools Protocol - see {@link NetworkConditions}. On Firefox, every step
 * here raises a {@link PendingException}, which Cucumber reports as
 * "pending" (yellow) rather than failing the build.
 */
public class NetworkSteps {

    private final ScenarioContext context;
    private NetworkCapture networkCapture;
    private NetworkConditions networkConditions;
    private List<NetworkCapture.NetworkEvent> lastCapturedEvents = List.of();
    private List<Map<String, String>> lastCapturedHeaders = List.of();
    private boolean reloadFailed;

    public NetworkSteps(ScenarioContext context) {
        this.context = context;
    }

    private WebDriver driver() {
        return context.getDriver();
    }

    private void requireChromiumSupport() {
        if (!NetworkConditions.isSupported(driver())) {
            throw new PendingException(
                    "Network capture/manipulation via CDP is only supported on Chromium-based browsers; skipping on "
                            + driver().getClass().getSimpleName());
        }
        if (networkCapture == null) {
            networkCapture = new NetworkCapture(driver());
        }
        if (networkConditions == null) {
            networkConditions = new NetworkConditions(driver());
        }
    }

    @When("I capture network traffic while reloading the page")
    public void iCaptureNetworkTrafficWhileReloadingThePage() {
        requireChromiumSupport();
        networkCapture.captureEvents(); // drain anything from initial page load
        driver().navigate().refresh();
        lastCapturedEvents = networkCapture.captureEvents();
    }

    @Then("at least one captured response should be for a stylesheet or script")
    public void atLeastOneCapturedResponseShouldBeForAStylesheetOrScript() {
        boolean found = lastCapturedEvents.stream()
                .filter(e -> "Network.responseReceived".equals(e.method()))
                .anyMatch(e -> e.mimeType() != null
                        && (e.mimeType().contains("javascript") || e.mimeType().contains("css")));
        assertThat(found).as("expected at least one JS/CSS response in: %s", lastCapturedEvents).isTrue();
    }

    @When("I block requests matching {string} and reload the page")
    public void iBlockRequestsMatchingAndReloadThePage(String urlPattern) {
        requireChromiumSupport();
        networkConditions.blockUrls(urlPattern);
        networkCapture.captureEvents();
        driver().navigate().refresh();
        lastCapturedEvents = networkCapture.captureEvents();
    }

    @Then("no captured response url should end with {string}")
    public void noCapturedResponseUrlShouldEndWith(String suffix) {
        boolean anyMatch = lastCapturedEvents.stream()
                .filter(e -> "Network.responseReceived".equals(e.method()))
                .anyMatch(e -> e.url() != null && e.url().endsWith(suffix));
        assertThat(anyMatch).as("expected no blocked-URL responses in: %s", lastCapturedEvents).isFalse();
    }

    @When("I take the browser network offline and try to reload the page")
    public void iTakeTheBrowserNetworkOfflineAndTryToReloadThePage() {
        requireChromiumSupport();
        networkConditions.goOffline();
        try {
            driver().navigate().refresh();
            reloadFailed = false;
        } catch (WebDriverException e) {
            reloadFailed = true;
        } finally {
            networkConditions.goOnline(); // restore for any later steps/scenarios
        }
    }

    @Then("the browser should report a network error")
    public void theBrowserShouldReportANetworkError() {
        boolean pageShowsError = driver().getPageSource().toLowerCase()
                .matches("(?s).*(no internet|err_internet_disconnected|dns_probe|offline).*");
        assertThat(reloadFailed || pageShowsError)
                .as("expected the reload to fail or the page to show an offline error")
                .isTrue();
    }

    @When("I set an extra request header {string} with value {string} and reload the page")
    public void iSetAnExtraRequestHeaderWithValueAndReloadThePage(String headerName, String headerValue) {
        requireChromiumSupport();
        networkConditions.setExtraHeaders(Map.of(headerName, headerValue));
        networkCapture.captureRequestHeaders();
        driver().navigate().refresh();
        lastCapturedHeaders = networkCapture.captureRequestHeaders();
    }

    @Then("the captured requests should include the custom header")
    public void theCapturedRequestsShouldIncludeTheCustomHeader() {
        boolean found = lastCapturedHeaders.stream().anyMatch(h -> "SeleniumCucumberFramework".equals(h.get("X-Test-Framework")));
        assertThat(found).as("expected custom header in captured requests: %s", lastCapturedHeaders).isTrue();
    }
}
