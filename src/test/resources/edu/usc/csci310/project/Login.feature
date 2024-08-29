Feature: test the login functionality of the camping app
  Background:
    Given I have a database cleared and users created for login

  Scenario: I can't login for 30 seconds after three failed attempts in a minute
    Given I am on the login page
    When I enter the username "a"
    And I enter the password "wrong"
    And I press the Login button
    Then I should fail see an incorrect password message
    And I confirm the message
    And I press the Login button
    Then I should fail see an incorrect password message
    And I confirm the message
    When I press the Login button
    Then I should fail and see an account locked out message
    And I confirm the message
    And I press the Login button
    Then I should fail and see a temporary lockout message

  Scenario: I can log back in after 30 seconds
    Given I am on the login page
    When I enter the username "b"
    And I enter the password "wrong"
    And I press the Login button
    And I confirm the message
    And I press the Login button
    And I confirm the message
    When I press the Login button
    And I confirm the message
    And I press the Login button
    And I confirm the message
    And I wait 30 seconds
    And I am on the login page
    When I enter the username "b"
    And I enter the password "Aa1"
    And I press the Login button
    Then I should go to the search page from the login page

  Scenario: I can still login if I make three failed attempts in over a minute
    Given I am on the login page
    When I enter the username "c"
    And I enter the password "wrong"
    And I press the Login button
    And I confirm the message
    And I wait 30 seconds
    And I press the Login button
    And I confirm the message
    And I wait 30 seconds
    And I am on the login page
    When I enter the username "c"
    And I enter the password "Aa1"
    And I press the Login button
    Then I should go to the search page from the login page

  Scenario: I access the login page without SSL
    Given I am on the login page using HTTP
    Then I should see a SSL error for login

  Scenario: a normal successful login
    Given I am on the login page
    When I enter the username "a"
    And I enter the password "Aa1"
    And I press the Login button
    Then I should go to the search page from the login page

  Scenario: invalid login username
    Given I am on the login page
    When I enter the username "notAUser"
    And I enter the password "Aa1"
    And I press the Login button
    Then I should see user not found message

  Scenario: invalid login password
    Given I am on the login page
    When I enter the username "a"
    And I enter the password "wrong"
    And I press the Login button
    Then I should fail see an incorrect password message

  Scenario: empty username login
    Given I am on the login page
    When I enter the username ""
    And I enter the password "Aa1"
    And I press the Login button
    Then I should fail to login with empty username message

  Scenario: empty password login
    Given I am on the login page
    When I enter the username "a"
    And I enter the password ""
    And I press the Login button
    Then I should fail to login with empty password message
