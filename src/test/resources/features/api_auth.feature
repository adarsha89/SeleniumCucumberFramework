@api
Feature: API Basic Authentication
  As a test engineer
  I want to verify HTTP Basic Auth credential handling
  So that I know the API_AUTH_USERNAME/API_AUTH_PASSWORD wiring is correct

  These scenarios hit https://httpbin.org, a public request-echo service, so
  they need no application server of their own.

  @smoke
  Scenario: Authenticating with the configured API credentials succeeds
    When I call the basic-auth endpoint with the configured API credentials
    Then the API response status should be 200
    And the API response should confirm the authenticated user

  Scenario: Authenticating with the wrong credentials fails
    When I call the basic-auth endpoint with username "wrong-user" and password "wrong-pass"
    Then the API response status should be 401

  Scenario: Calling the endpoint without any credentials fails
    When I call the basic-auth endpoint without credentials
    Then the API response status should be 401
