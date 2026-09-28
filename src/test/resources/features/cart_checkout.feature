@checkout
Feature: SauceDemo Cart and Checkout
  As a logged in shopper
  I want to check out the items in my cart
  So that I can complete a purchase

  Background:
    Given I am logged in as the "standard" user
    And I add "Sauce Labs Backpack" to the cart
    And I go to the cart page

  Rule: A shopper who gives complete information can place an order

    @smoke
    Scenario: Complete checkout with valid information
      When I proceed to checkout
      And I fill in the checkout information and continue
      And I finish the order
      Then I should see the order confirmation "Thank you for your order!"

    Scenario: Complete checkout with shopper details from a data table
      When I proceed to checkout
      And I fill in the checkout information with:
        | First Name  | Priya    |
        | Last Name   | Sharma   |
        | Postal Code | 560001   |
      And I finish the order
      Then I should see the order confirmation "Thank you for your order!"

  Rule: Every customer information field is required

    Scenario: Checkout is rejected when required information is missing
      When I proceed to checkout
      And I try to continue without filling in the checkout information
      Then I should see a checkout error message

    Scenario Outline: Checkout names the first missing field - <missing field>
      When I proceed to checkout
      And I try to continue with the checkout information:
        | First Name  | <first name>  |
        | Last Name   | <last name>   |
        | Postal Code | <postal code> |
      Then I should see the checkout error "<error>"

      Examples:
        | missing field | first name | last name | postal code | error                          |
        | First Name    |            | Sharma    | 560001      | Error: First Name is required  |
        | Last Name     | Priya      |           | 560001      | Error: Last Name is required   |
        | Postal Code   | Priya      | Sharma    |             | Error: Postal Code is required |
