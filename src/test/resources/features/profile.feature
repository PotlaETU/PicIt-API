Feature: User profile

  Scenario: Create my profile
    When I create my profile
    Then I should see my created profile

  Scenario: Get my profile
    When I get my profile
    Then I should see my profile

  Scenario: Get a profile
    When I create a new user with username "justo.vd2" and email "j.vallindetrez_sainte_union@gmail.com"
    When I get the profile of "justo.vd2"
    Then I should see the profile of "justo.vd2"

  Scenario: Search for a profile
    When I search for the profile "justo.vd2"
    Then I should see the profile "justo.vd2"