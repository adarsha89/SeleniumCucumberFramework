@inventory
Feature: SauceDemo Inventory
  As a logged in shopper
  I want to browse and sort products
  So that I can find what I want to buy

  Background:
    Given I am logged in as the "standard" user

  @smoke
  Scenario: Inventory page lists all products
    Then the inventory page should show 6 products

  Rule: The cart reflects every product the shopper adds

    Scenario: Add a product to the cart
      When I add "Sauce Labs Backpack" to the cart
      Then the cart badge should show 1 item

    Scenario: Add several products to the cart from a data table
      When I add the following products to the cart:
        | Sauce Labs Backpack     |
        | Sauce Labs Bike Light   |
        | Sauce Labs Bolt T-Shirt |
      Then the cart badge should show 3 items
      When I go to the cart page
      Then the cart should contain exactly these products:
        | Sauce Labs Backpack     |
        | Sauce Labs Bike Light   |
        | Sauce Labs Bolt T-Shirt |

  Rule: Products can be sorted by name or price

    Scenario Outline: Sort products by <sort option>
      When I sort products by "<sort option>"
      Then the first product listed should be "<first product>"

      Examples:
        | sort option         | first product                     |
        | Name (A to Z)       | Sauce Labs Backpack               |
        | Name (Z to A)       | Test.allTheThings() T-Shirt (Red) |
        | Price (low to high) | Sauce Labs Onesie                 |
        | Price (high to low) | Sauce Labs Fleece Jacket          |
