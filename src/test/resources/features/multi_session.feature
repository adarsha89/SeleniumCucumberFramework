@multisession
Feature: Multiple browser sessions
  As a tester
  I want one scenario to drive several browsers at once
  So that I can verify state is isolated between independent users

  Scenario: Two users log in independently in separate browser sessions
    Given I am logged in as the "standard" user
    When I open browser session 2
    And I am on the SauceDemo login page
    And I log in as the "locked_out" user
    Then I should see the login error "Epic sadface: Sorry, this user has been locked out."
    When I switch to browser session 1
    Then I should be redirected to the inventory page
    When I close browser session 2
    Then I should be redirected to the inventory page
