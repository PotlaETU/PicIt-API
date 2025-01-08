Feature: Posts feature

  Scenario: Create a post
    When I create a post
    Then The post should be created

  Scenario: Create a post using json
    When I create a post using json
    Then The post should be created

  Scenario: Like a post
    When I like a post
    Then The post should be liked

  Scenario: I get the posts by the user
    When I get the posts by the user
    Then I should get the posts by the user

  Scenario: Update a post
    When I update a post
    Then The post should be updated

  Scenario: Delete a post
    When I delete a post
    Then The post should be deleted