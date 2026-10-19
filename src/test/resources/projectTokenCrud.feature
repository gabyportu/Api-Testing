Feature: CRUD de proyecto con autenticacion por token

  @Regression @project-crud
  Scenario: Crear un usuario y gestionar un proyecto con su token
    Given I create a temporary Todo.ly user
    And I obtain the token for that user
    # Create: el token se envia en el encabezado HTTP Token.
    When send POST request "api/projects.json" with
      """
      {"Content":"CucumberAPI"}
      """
    Then code should be 200
    And the "Content" should be "CucumberAPI"
    And save "Id" in "PROJECT_ID"

    # Read
    When send GET request "api/projects/PROJECT_ID.json" with
      """
      """
    Then code should be 200
    And the "Content" should be "CucumberAPI"

    # Update
    When send PUT request "api/projects/PROJECT_ID.json" with
      """
      {"Content":"CucumberAPIUpdate"}
      """
    Then code should be 200
    And the "Content" should be "CucumberAPIUpdate"

    # Delete
    When send DELETE request "api/projects/PROJECT_ID.json" with
      """
      """
    Then code should be 200

