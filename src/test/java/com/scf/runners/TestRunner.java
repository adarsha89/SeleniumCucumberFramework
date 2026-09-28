package com.scf.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Entry point that Maven Surefire runs. Delegates to the Cucumber JUnit
 * Platform engine, which discovers and executes every {@code .feature} file
 * under {@code src/test/resources/features}.
 * <p>
 * Run with, e.g.:
 * <pre>
 *   mvn test -Dbrowser=firefox -Dheadless=true
 *   mvn test -Dcucumber.filter.tags="@smoke and not @wip"
 * </pre>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.scf.stepdefinitions,com.scf.hooks")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-report/report.html, json:target/cucumber-report/report.json")
public class TestRunner {
}
