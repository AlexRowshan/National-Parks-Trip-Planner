Feature: Create and store a new user's data
  Background:
    Given I have a database cleared for create user

  Scenario: I access the create user page without SSL
    Given I am on the create user page using HTTP
    Then I should see a SSL error for create user

  Scenario: a normal user creation
    Given I am on the create user page
    And I put username "Andy" in Username box
    And I put password "Trojan123" in Password box
    And I put password "Trojan123" in Confirm Password box
    When I click Create User button
    Then I should be on the search page from the create user page

  Scenario: a normal cancel user creation case and confirm cancellation
    Given I am on the create user page
    And I put username "Bob" in Username box
    And I put password "Trojan" in Password box
    And I put password "Trojan" in Confirm Password box
    And I click Cancel button
    And I confirm Yes
    Then I should go to back to the login page

  Scenario: a normal cancel user creation case and deny cancellation
    Given I am on the create user page
    And I put username "Jim" in Username box
    And I put password "Trojan" in Password box
    And I put password "Trojan" in Confirm Password box
    And I click Cancel button
    And I say No
    Then I should stay on the create user page

  Scenario: incorrect confirm password
    Given I am on the create user page
    And I put username "Jim" in Username box
    And I put password "Trojan123" in Password box
    And I put password "Bruin123" in Confirm Password box
    When I click Create User button
    Then I should see an error message for wrong password

  Scenario: a normal user creation, followed by taken username
    Given I am on the create user page
    And I put username "Joe" in Username box
    And I put password "Trojan123" in Password box
    And I put password "Trojan123" in Confirm Password box
    And I click Create User button
    And I should be on the search page from the create user page
    And I go to the create user page
    And I put username "Joe" in Username box
    And I put password "Trojan123" in Password box
    And I put password "Trojan123" in Confirm Password box
    When I click Create User button
    Then I should an error message for taken username

  Scenario: a normal user creation from the Login Page
    Given I am on the initial login page
    And I click the Create account button
    And I put username "Lebron" in Username box
    And I put password "James123" in Password box
    And I put password "James123" in Confirm Password box
    When I click Create User button
    Then I should be on the search page from the create user page

  Scenario: Only one password is entered
    Given I am on the create user page
    And I put username "Jim" in Username box
    And I put password "Trojan123" in Password box
    When I click Create User button
    Then I should see an error message for wrong password

  Scenario: invalid password format
    Given I am on the create user page
    And I put username "John" in Username box
    And I put an invalid password "invalidpassword" in Password box
    And I put password "invalidpassword" in Confirm Password box
    When I click Create User button
    Then I should see an error message for invalid password format
