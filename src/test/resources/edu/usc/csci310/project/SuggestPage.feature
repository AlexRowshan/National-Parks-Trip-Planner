Feature: Suggest Page for a single park that's the best match
  Background:
    Given I have a database cleared and users created for suggest
    And I am logged in as user "a" for suggest
    And I set session storage for "a" for suggest

  Scenario: Successfully log out of the suggest page and can't go back
    Given I click the logout button for suggest
    Then I should be on the login page for suggest
    And I should not be able to go back to suggest

  Scenario: I access the suggest page without SSL
    Given I am on the suggest page using HTTP
    Then I should see a SSL error for suggest

  Scenario: Suggesting park with one friend
    And I go to the suggest page
    And I entered "b" username in the search bar
    When I click the Suggest button
    Then the "Yosemite National Park" park is displayed

#  Scenario: Attempting to suggest when a suggestion cannot be determined due to private visibility
#    Given I go to the suggest page
#    And I entered "c" username in the search bar
#    When I click the Add User button
#    Then I should get an error message that my friend has private visibility, preventing a suggestion

  Scenario: Navigate to compare page
    When I click the "Compare" button in the nav bar for suggest
    Then I should be on the "Compare" page from suggest

  Scenario: Navigate to search page
    And I am logged in as user "a" for suggest
    And I am on the suggest page
    And I set session storage for "a" for suggest
    When I click the "Search" button in the nav bar for suggest
    Then I should be on the "Search" page from suggest

  Scenario: Navigate to favorite page
    And I am logged in as user "a" for suggest
    And I am on the suggest page
    And I set session storage for "a" for suggest
    When I click the "Favorite" button in the nav bar for suggest
    Then I should be on the "Favorite" page from suggest


  Scenario: Show that suggestion result does not persist when you leave the suggestion page and come back
    And I go to the suggest page
    And I entered "b" username in the search bar
    When I click the Suggest button
    Then the "Yosemite National Park" park is displayed
    When I click the "Search" button in the nav bar for suggest
    Then I should be on the "Search" page from suggest
    When I click the "Suggest" button in the nav bar for suggest
    Then I should be on the "Suggest" page from suggest

#  Scenario: Attempting to suggest when a user doesn't exist
#    Given I go to the suggest page
#    And I entered "e" username in the search bar
#    When I click the "Suggest" button
#    Then I should get a user not found error
#
#  Scenario: Attempting to suggest with a user who has no favorites
#    Given I am on the park suggestion page
#    And I have favorited "Yosemite"
#    And my friend d has public visibility but no favorites
#    And I have entered my friend’s name in the search bar
#    When I click the "Suggest" button
#    Then I should get an invalid suggestion error as no suggestion can be determined
#
#  Scenario: Determining the suggestion from three friends total
#    Given I am on the park suggestion page
#    And I have favorited "Yosemite" as my top favorite
#    And my first friend with public visibility has favorited "Zion" and "Yosemite" with "Yosemite" as their top favorite
#    And my second friend with public visibility has favorited "Yellowstone" and "Yosemite" with "Yosemite" as their top favorite
#    And I have entered two friends' names in the search bar
#    When I click the "Suggest" button
#    Then the "Yosemite" park is displayed