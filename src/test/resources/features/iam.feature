Feature: IAM feature

  Scenario: Create IAM user
    When I create an account
    Then the account should be created

  Scenario: Login IAM user
    When I login to the account
    Then The account should be logged in