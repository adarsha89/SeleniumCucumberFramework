package com.scf.hooks;

import com.scf.config.ConfigManager;
import com.scf.context.ScenarioContext;
import com.scf.driver.BrowserSession;
import com.scf.driver.DriverManager;
import com.scf.utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Hooks {

    /** Identifier of the browser every scenario starts with; extra sessions are opened by steps. */
    public static final int DEFAULT_SESSION = 1;

    private static final Logger LOG = LogManager.getLogger(Hooks.class);

    private final ScenarioContext scenarioContext;
    private final BrowserSession browserSession = new BrowserSession();

    public Hooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before
    public void setUp(io.cucumber.java.Scenario scenario) {
        ConfigManager config = ConfigManager.get();
        LOG.info("Starting scenario '{}' on browser {}", scenario.getName(), config.browser());

        browserSession.start(DEFAULT_SESSION);
        scenarioContext.useSession(DEFAULT_SESSION, DriverManager.getDriver(), config.explicitWaitSeconds());
    }

    @After
    public void tearDown(io.cucumber.java.Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                for (Integer identifier : DriverManager.activeIdentifiers()) {
                    byte[] screenshot = ScreenshotUtil.capture(DriverManager.getDriver(identifier));
                    scenario.attach(screenshot, "image/png", scenario.getName() + " [session " + identifier + "]");
                }
                LOG.warn("Scenario '{}' failed - screenshot attached", scenario.getName());
            }
        } finally {
            LOG.info("Finished scenario '{}' with status {}", scenario.getName(), scenario.getStatus());
            browserSession.stopAll();
        }
    }
}
