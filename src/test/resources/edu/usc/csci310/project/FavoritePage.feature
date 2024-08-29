Feature: Manage Favorites Page
  Background:
    Given I have a database cleared and users created for fav

  Scenario: I access the fav page without SSL
    Given I am on the fav page using HTTP
    Then I should see a SSL error for fav

  Scenario: Successfully log out of the fav page
    Given I click the logout button for fav
    Then I should be on the login page for fav

  Scenario: FavPage is in accessible if logged out
    Given I click the logout button for fav
    And I should be on the login page for fav
    When I am on the login page for fav
    Then I should not have access for fav

  Scenario: Seeing user a's favorites number 1
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    Then I should see fav "Yosemite National Park"

  Scenario: Seeing user a's favorites number 2
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    Then I should see fav "John Muir National Historic Site"

  Scenario: Seeing user a's favorites number 3
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    Then I should see fav "Abraham Lincoln Birthplace National Historical Park"

  Scenario: Moving a park down
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    When I press the down arrow on the top favorite park box
    Then I should see the order of the two parks switched

  Scenario: Moving a park up
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    When I press the up arrow on the second favorite park box
    Then I should see the order of the two parks switched

  Scenario: Hovering over a favorites box to reveal management options
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    When I click on the first park box
    Then I should see a minus sign

  Scenario: Clicking the minus sign and seeing a confirmation
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    And I click on the first park box
    When I should see a minus sign and click it
    Then I should see a confirmation dialog

  Scenario: Clicking the minus sign and seeing a confirmation and clicking cancel
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    And I click on the first park box
    And I should see a minus sign and click it
    When I should see a confirmation dialog
    And I confirm cancel
    Then I should still see the park in the list of favorites

  Scenario: Clicking the minus sign and seeing a confirmation and clicking yes
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    And I click on the first park box
    And I should see a minus sign and click it
    And I should see a confirmation dialog
    When I confirm OK
    Then I should see a positive confirmation dialog
    When I confirm OK
    And the park, "Yosemite National Park" should be removed from a's list of favorites

  Scenario: Expanding a favorite park's details box accordion style
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    And I click on the first park box
    Then I should see the image, location, and entrance fee of park at the top of the box

  Scenario: Favorites list is private by default
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    Then my favorites list should be set to private by default

  Scenario: Making the favorites list public
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    When my favorites list is private
    And I uncheck the green private slider
    Then my favorites list should then be public

  Scenario: Making a public favorites list private
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    When my favorites list is private
    And I uncheck the green private slider
    Then my favorites list should then be public
    When I then check the slider again
    Then my favorites list should then be private

  Scenario: Show that favorites list persists after leaving favorites page to another then coming back to favorites
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    When I click the "Search" button in the nav bar for fav
    Then I should be on the "Search" page from fav
    When I click the "Favorites" button in the nav bar for fav
    Then I should be on the "Favorites" page from fav
    Then I should see fav "Yosemite National Park"

  Scenario: Navigate to compare page
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    Given I click the "Compare" button in the nav bar for fav
    Then I should be on the "Compare" page from fav

  Scenario: Navigate to search page
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
  Given I click the "Search" button in the nav bar for fav
  Then I should be on the "Search" page from fav

  Scenario: Navigate to suggest page
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
  Given I click the "Suggest" button in the nav bar for fav
  Then I should be on the "Suggest" page from fav

  Scenario: I log in as a user with more than 10 favorites
    And I am logged in as user "c" for fav
    And I go to the favorites page
    And I set session storage for "c" for fav
  Then I should see a load more button

  Scenario: I log in as a user with more than 10 favorites and click load more to see 10 more favorites
    And I am logged in as user "c" for fav
    And I go to the favorites page
    And I set session storage for "c" for fav
    When I click the load more button
    Then I should see 10 more favorites

  Scenario: Clicking the delete all favorites button and seeing a confirmation
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    And I click on the delete all favorites button
    Then I should see a confirmation dialog before deleting all favorites

  Scenario: Clicking the delete all favorites button and seeing a confirmation and clicking cancel
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    And I click on the delete all favorites button
    Then I should see a confirmation dialog before deleting all favorites
    And I confirm cancel
    Then I should still see the parks in the list of favorites

  Scenario: Clicking the delete all favorites button and seeing a confirmation and clicking yes
    And I am logged in as user "a" for fav
    And I go to the favorites page
    And I set session storage for "a" for fav
    And I click on the delete all favorites button
    Then I should see a confirmation dialog before deleting all favorites
    When I confirm OK
    Then I should see a positive confirmation dialog after deleting all favorites
    When I confirm OK
    And I should see no favorites
