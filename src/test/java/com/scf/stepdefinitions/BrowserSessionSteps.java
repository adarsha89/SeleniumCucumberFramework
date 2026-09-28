package com.scf.stepdefinitions;

import com.scf.config.ConfigManager;
import com.scf.context.ScenarioContext;
import com.scf.driver.BrowserSession;
import com.scf.driver.DriverManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

/**
 * Steps for scenarios that need more than one browser. Session 1 is opened
 * by {@link com.scf.hooks.Hooks}; these steps open, switch between and close
 * additional ones. All other steps act on whichever session is active.
 */
public class BrowserSessionSteps {

    private final ScenarioContext context;
    private final BrowserSession browserSession = new BrowserSession();

    public BrowserSessionSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("I open browser session {int}")
    public void iOpenBrowserSession(int identifier) {
        browserSession.start(identifier);
        activate(identifier);
    }

    @When("I switch to browser session {int}")
    public void iSwitchToBrowserSession(int identifier) {
        DriverManager.switchTo(identifier);
        activate(identifier);
    }

    @When("I close browser session {int}")
    public void iCloseBrowserSession(int identifier) {
        browserSession.stop(identifier);
        context.removeSession(identifier);
    }

    private void activate(int identifier) {
        context.useSession(identifier, DriverManager.getDriver(identifier), ConfigManager.get().explicitWaitSeconds());
    }
}
