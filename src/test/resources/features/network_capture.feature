@network @chromium-only
Feature: Browser Network Capture and Manipulation
  As a test engineer
  I want to inspect and manipulate network traffic in the browser
  So that I can verify page behaviour under different network conditions

  These scenarios use the Chrome DevTools Protocol and therefore only run on
  Chromium-based browsers (Chrome, Edge). On Firefox they are skipped at
  runtime - see NetworkSteps.

  Background:
    Given I am on the SauceDemo login page

  Scenario: Capture network requests made while loading the login page
    When I capture network traffic while reloading the page
    Then at least one captured response should be for a stylesheet or script

  Scenario: Block image requests and confirm none are loaded
    When I block requests matching "*.jpg" and reload the page
    Then no captured response url should end with ".jpg"

  Scenario: Simulate an offline network and observe the page fails to load
    When I take the browser network offline and try to reload the page
    Then the browser should report a network error

  Scenario: Inject a custom HTTP header on outgoing requests
    When I set an extra request header "X-Test-Framework" with value "SeleniumCucumberFramework" and reload the page
    Then the captured requests should include the custom header
