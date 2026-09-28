@login
Feature: SauceDemo Login
  As a shopper
  I want to log in to SauceDemo
  So that I can browse and purchase products

  Background:
    Given I am on the SauceDemo login page

  @smoke
  Scenario: Successful login with a standard user
    When I log in as the "standard" user
    Then I should be redirected to the inventory page

  Scenario: Login is rejected for a locked out user
    When I log in as the "locked_out" user
    Then I should see the login error "Epic sadface: Sorry, this user has been locked out."

  Scenario: Login is rejected for the env-configured credentials
    When I log in using the credentials from the TEST_USER_EMAIL environment variable
    Then I should see a login error message
