Feature: Comparison Page Functionality
  Background:
    Given I have a database cleared and users created for compare

  Scenario: 2 public users, check the proper ratio where both users have the same park
    Given I am logged in as user "Person1"
    And I go to the compare page
    And I add the user "Person2"
    When I click compare
  Then I should see "2/2" for the ratio of the first park card

  Scenario: 2 public users, check the proper names when hovering over a ratio
    Given I am logged in as user "Person1"
    And I go to the compare page
    And I add the user "Person2"
    And I click compare
    When I hover my mouse over the ratio
    Then I should see "Person1 Person2" as a pop up

  Scenario: Click/render park card
    Given I am logged in as user "Person1"
    And I go to the compare page
    And I add the user "Person2"
    And I click compare
    When I click the first park name
    Then The park information should render

  Scenario: Attempting to add user with private favorites list
    Given I am logged in as user "Person1"
    And I go to the compare page
    And I add the private user "Person3"
    Then I should see an alert "The entered username belongs to a private user. Cannot compare parks."

  Scenario: Attempting to search for an unregistred user
    Given I am logged in as user "Person1"
    And I go to the compare page
    And I add the unregistered user "Lebron"
    Then I should see an alert "The entered username does not exist."

  Scenario: Output the union of all parks
    Given I am logged in as user "Person1"
    And I go to the compare page
    And I add the user "Person2"
    And I click compare
    Then I should see 5 park cards

  Scenario: Navigate to Search page from compare
    Given I am logged in as user "Person1"
    And I go to the compare page
    And I click "Search" on the navbar
    Then I should be on the "Search" page from compare

  Scenario: Navigate to Favorites page from compare
    Given I am logged in as user "Person1"
    And I go to the compare page
    And I click "Favorites" on the navbar
    Then I should be on the "Favorites" page from compare

  Scenario: Navigate to Suggest page from compare
    Given I am logged in as user "Person1"
    And I go to the compare page
    And I click "Suggest" on the navbar
    Then I should be on the "Suggest" page from compare

  Scenario: Successfully log out of the compare page and can't go back
    Given I am logged in as user "Person1"
    And I go to the compare page
    And I click the Logout button for compare
    Then I should be on the login page for compare
    And I should not be able to go back to the compare page

  Scenario: Attempting to add user with empty favorites list
    Given I am logged in as user "Person1"
    And I go to the compare page
    And I add the private user "Person4"
    Then I should see an alert "The entered username belongs to a user with no favorited parks or an error occurred."








#  Scenario: Comparing favorite parks with a friend based on best match
#    Given I am on the friend comparison page
#    And I have public visibility
#    And I have favorited "Yosemite" and "Zion" park with "Yosemite" as my top favorite
#    And my friend has favorited "Yosemite" and "Yellowstone" with "Yosemite" as their top favorite and has public visibility
#    And I have entered my friend's username in the search bar
#    When I click the "Compare" button
#    Then the parks should be listed starting with "Yosemite" as it is the best match based on being everyone's list and having the highest average rank, followed by "Zion" and "Yellowstone" in any order
#
#  Scenario: Attempting to compare when a best match cannot be determined due to private visibility
#  Given I am on the friend comparison page
#    And I have public visibility
#    And I have favorited "Yosemite" and "Zion" park with "Yosemite" as my top favorite
#    And my friend has private visibility
#    And I have entered my friend's username in the search bar
#    When I click the "Compare" button
#    Then I should get an error message that at least one person has private visibility, preventing a best match comparison
#
#  Scenario: Comparing with a user who doesn’t exist
#    Given I am on the friend comparison page
#    And I have entered a name that doesn’t exist in the search bar
#    When I click the "Compare" button
#    Then I should get a user not found error
#
#  Scenario: Comparing with a user who has no favorites, preventing a best match determination
#    Given I am on the friend comparison page
#    And I have public visibility
#    And I have favorited "Yosemite"
#    And my friend has public visibility but no favorites
#    And I have entered my friend’s name in the search bar
#    When I click the "Compare" button
#    Then I should get an invalid comparison error as no best match can be determined
#
#  Scenario: Determining the best match from the union of three friends’ favorite parks list
#    Given I am on the friend comparison page
#    And I have public visibility
#    And I have favorited "Yosemite" as my top favorite
#    And my first friend with public visibility has favorited "Zion" and "Yosemite" with "Yosemite" as their top favorite
#    And my second friend with public visibility has favorited "Yellowstone" and "Yosemite" with "Yosemite" as their top favorite
#    And I have entered both my friends' names in the search bar
#    When I click the "Compare" button
#    Then "Yosemite" should be listed first as the best match based on being on everyone’s list and having the highest average rank, followed by "Zion" and "Yellowstone" in any order
#
#  Scenario: Union of three friends’ favorite parks list with a unique ranking based on the best match
#    Given I am on the friend comparison page
#    And I have public visibility
#    And I have favorited "Yosemite", "Zion", and "Yellowstone" with "Yosemite" as my top favorite
#    And my first friend with public visibility has favorited "Yosemite" and "Zion" with "Yosemite" as their top favorite
#    And my second friend with public visibility has favorited "Yosemite" and "Yellowstone" with "Yosemite" as their top favorite
#    And I have entered both my friends' names in the search bar
#    When I click the "Compare" button
#    Then the parks should be listed starting with "Yosemite" as the best match based on being on everyone's list and having the highest average rank, followed by the other parks in any order based on remaining preferences