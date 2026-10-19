Feature: CRUD de usuario en Todo.ly

  @Regression @user-crud
  Scenario: Crear, actualizar y eliminar un usuario temporal
    Given I create a temporary Todo.ly user
    When I update the temporary user's full name to "API V6 Updated User"
    Then code should be 200
    And the "FullName" should be "API V6 Updated User"
    When I delete the temporary user
    Then code should be 200

