Feature: Search page functionality
  Background:
    Given I have a database cleared and users created for search
    And I am logged in as user "a" for search
    And I am on the search page
    And I set session storage for "a" for search

  Scenario: AFK logout after a minute
    Given I am on the search page
    And I wait 31 seconds
    Then I should be on the login page for search

  Scenario: I access the search page without SSL
    Given I am on the search page using HTTP
    Then I should see a SSL error for search

  Scenario: Successfully log out of the search page
    Given I click the logout button for search
    Then I should be on the login page for search

  Scenario: SearchPage is in accessible if logged out
    Given I click the logout button for search
    And I should be on the login page for search
    When I am on the search page
    Then I should not have access for search

  Scenario: Successfully add park to favorites
    Given I put "stone" in the search box
    And I click the search button
    And I hover over the first park
    And I click the plus button to add to favorites
    Then I should see an alert that it was successful

  Scenario: Successfully add park to favorites when park details are expanded
    Given I put "stone" in the search box
    And I click the search button
    And I hover over the first park
    And I click on the first park’s name to expand the box
    And I click the plus button to add to favorites
    Then I should see an alert that it was successful

  Scenario: Fail to add park to favorites when already added
    Given I put "yosemite" in the search box
    And I click the search button
    And I hover over the first park
    And I click the plus button to add to favorites
    And I dismiss the alert by pressing ok
    And I click the plus button to add to favorites again
    Then I should see an alert that the park is already in favorites

  Scenario: Fail to add park to favorites when park details are expanded when already added
    Given I put "yosemite" in the search box
    And I click the search button
    And I hover over the first park
    And I click the plus button to add to favorites
    And I dismiss the alert by pressing ok
    And I click on the first park’s name to expand the box
    And I click the plus button to add to favorites again
    Then I should see an alert that the park is already in favorites

  Scenario: Making a default search should search by name
    Given I put "yosemite" in the search box
    When I click the search button
    Then I should see at least one search result named "Yosemite National Park"

  Scenario: Open a park's details that isn't in favorites
    Given I put "CA" in the search box
    And select the "state" radio button
    When I click the search button
    Then I should see at least one search result
    When I click on the first park’s name to expand the box
    Then i should see the park favorite question set to "No"

  Scenario: Searching by park name
    Given I put "yosemite" in the search box
    And select the "name" radio button
    When I click the search button
    Then I should see at least one search result named "Yosemite National Park"

  Scenario: Searching by state
    Given I put "TX" in the search box
    And select the "state" radio button
    When I click the search button
    Then I should see at least one search result
    When I click on the first park’s name to expand the box
    Then I should see at least one search result with a stateCode of "TX"

  Scenario: Searching by activities
    Given I put "surfing" in the search box
    And select the "activity" radio button
    When I click the search button
    Then I should see at least one search result
    When I click on the first park’s name to expand the box
    Then I should see at least one search result with an activity of "surfing"

  Scenario: Searching by amenities
    Given I put "Wheelchair Accessible" in the search box
    And select the "amenities" radio button
    When I click the search button
    Then I should see at least one search result
    When I click on the first park’s name to expand the box
    Then I should see a search result with a "Wheelchair Accessible" amenity

  Scenario: Getting 10 more park results
    Given I put "CA" in the search box
    And select the "state" radio button
    When I click the search button
    Then I should see a show more button
    When I click the show more button
    Then I should see 10 more search results

  Scenario: Expanding a search results box accordion style
    Given I put "yosemite" in the search box
    And I click the search button
    And I should see at least one search result
    When I click on the first park’s name to expand the box
    Then I should see the image and location of park at the top of the box

  Scenario: Details has clickable URL
    Given I put "yosemite" in the search box
    And I click the search button
    And I should see at least one search result
    And I click on the first park’s name to expand the box
    Then I should see a URL for the park
    When I click the URL for the park
    Then I should go the park’s webpage

  Scenario: Details has picture
    Given I put "yosemite" in the search box
    And I click the search button
    And I should see at least one search result
    When I click on the first park’s name to expand the box
    Then I should see an image of the park

  Scenario: Details has description
    Given I put "yosemite" in the search box
    And I click the search button
    And I should see at least one search result
    When I click on the first park’s name to expand the box
    Then I should see an description of the park

  Scenario: Details has entrance fee
    Given I put "yosemite" in the search box
    And I click the search button
    And I should see at least one search result
    When I click on the first park’s name to expand the box
    Then I should see the entrance fee of the park

  Scenario: Details has location information
    Given I put "yosemite" in the search box
    And I click the search button
    And I should see at least one search result
    When I click on the first park’s name to expand the box
    Then I should see the location of the park

  Scenario: Details has amenities
    Given I put "yosemite" in the search box
    And I click the search button
    And I should see at least one search result
    When I click on the first park’s name to expand the box
    Then I should see the amenities at the park

  Scenario: Details has activities
    Given I put "yosemite" in the search box
    And I click the search button
    And I should see at least one search result
    When I click on the first park’s name to expand the box
    Then I should see the activities at the park

  Scenario: Details has favorite indicator
    Given I put "yosemite" in the search box
    And I click the search button
    And I should see at least one search result
    When I click on the first park’s name to expand the box
    Then I should see a favorite indicator

  Scenario: Performing an empty search
    Given I put "" in the search box
    When I click the search button
    Then I should see an alert that no results were found

  Scenario: Performing a search with no results found
    Given I put "asdf" in the search box
    When I click the search button
    Then I should see an alert that no results were found

  Scenario: Closing the no results found alert
    Given I put "asdf" in the search box
    And I click the search button
    And I should see an alert that no results were found
    When I close the alert
    Then the alert should be closed

  Scenario: Navigate to favorites page
  Given I click the "Favorites" button in the nav bar
  Then I should be on the "Favorites" page

  Scenario: Navigate to compare page
  Given I click the "Compare" button in the nav bar
  Then I should be on the "Compare" page

  Scenario: Navigate to suggest page
  Given I click the "Suggest" button in the nav bar
  Then I should be on the "Suggest" page

  Scenario: 10 results are displayed after a search
    Given I put "CA" in the search box
    And select the "state" radio button
    When I click the search button
    Then I should see 10 search results

  Scenario: Closing an expanded search results box accordion style
    Given I put "yosemite" in the search box
    And I click the search button
    And I should see at least one search result
    When I click on the first park’s name to expand the box
    Then I should see the image and location of park at the top of the box
    When I click on the first park’s name again to collapse the box
    Then the first park's name should no longer be green

  Scenario: Clicking on StateCode in park details should search by state
    Given I put "yosemite" in the search box
    And I click the search button
    And I should see at least one search result
    When I click on the first park’s name to expand the box
    And I click on the first park’s stateCode which is "CA"
    Then I should see at least one search result with the same stateCode as the previous first park which is "CA"

  Scenario: Clicking on Activity in park details should search by activity
    Given I put "yosemite" in the search box
    And I click the search button
    And I should see at least one search result
    When I click on the first park’s name to expand the box
    And I click on the first activity in the activities list which is "Arts and Culture"
    Then I should see at least one search result with the same activity as the previous first park which is "Arts and Culture"

  Scenario: Clicking on Amenities in park details should search by amenities
    Given I put "yosemite" in the search box
    And I click the search button
    And I should see at least one search result
    When I click on the first park’s name to expand the box
    And I click on the first amenity in the amenities list which is "Accessible Rooms"
    Then I should see at least one search result with the same amenity as the previous first park which is "Accessible Rooms"
