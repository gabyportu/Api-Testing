package runner;

import client.FactoryRequest;
import client.IRequest;
import client.RequestInfo;
import config.Configuration;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

/** Pasos Gherkin.*/
public class TodoLySteps {
    private final Map<String, String> variables = new HashMap<>();
    private Response response;
    private String testEmail;
    private String testPassword;
    private String token;
    private long userId = -1;
    private boolean projectDeleted;
    private boolean userDeleted;

    @Given("I create a temporary Todo.ly user")
    public void createTemporaryUser() {
        Configuration.validate();
        testEmail = Configuration.nextTestEmail();
        testPassword = Configuration.getTestPassword();

        String body = "{\"Email\":\"" + testEmail + "\","
                + "\"Password\":\"" + testPassword + "\","
                + "\"FullName\":\"Proyecto2 API Test User\"}";
        try {
            response = send("POST", "api/user.json", body);
        } catch (RuntimeException error) {
            throw new IllegalStateException(
                    "No se pudo conectar con Todo.ly al crear el usuario. Revisa la red y el certificado HTTPS. "
                            + "Detalle: " + error.getMessage(), error);
        }

        if (response.statusCode() != 200) {
            throw new AssertionError("Todo.ly respondio HTTP " + response.statusCode()
                    + " al crear el usuario. Respuesta: " + response.asString());
        }

        Object id = response.jsonPath().get("Id");
        if (id == null) {
            throw new AssertionError("La respuesta de Todo.ly no contiene Id. Respuesta: " + response.asString());
        }
        userId = Long.parseLong(id.toString());
        variables.put("USER_ID", Long.toString(userId));
        if (userId <= 0) {
            throw new AssertionError("Todo.ly no devolvio un Id de usuario valido.");
        }
    }

    @And("I obtain the token for that user")
    public void obtainToken() {
        String url = Configuration.BASE_URL + "api/authentication/token.json";
        RequestInfo info = RequestInfo.withBasicAuth(url, null, testEmail, testPassword);
        response = FactoryRequest.make("GET").send(info);
        response.then().statusCode(200);
        token = response.jsonPath().getString("TokenString");
        if (token == null || token.isBlank()) {
            throw new AssertionError("Todo.ly no devolvio TokenString.");
        }
    }

    /** Paso generico*/
    @When("send {word} request {string} with")
    public void sendRequestWithToken(String method, String path, String body) {
        String url = Configuration.BASE_URL + replaceVariables(path);
        response = FactoryRequest.make(method).send(RequestInfo.withToken(url, body, token));
        if ("DELETE".equalsIgnoreCase(method)
                && path.startsWith("api/projects/")
                && response.statusCode() >= 200 && response.statusCode() < 300) {
            projectDeleted = true;
        }
    }

    @When("I update the temporary user's full name to {string}")
    public void updateTemporaryUser(String fullName) {
        String body = "{\"FullName\":\"" + fullName + "\"}";
        String url = Configuration.BASE_URL + "api/user/" + userId + ".json";
        RequestInfo info = RequestInfo.withBasicAuth(url, body, testEmail, testPassword);
        response = FactoryRequest.make("PUT").send(info);
    }

    @When("I delete the temporary user")
    public void deleteTemporaryUser() {
        String url = Configuration.BASE_URL + "api/user/" + userId + ".json";
        RequestInfo info = RequestInfo.withBasicAuth(url, null, testEmail, testPassword);
        response = FactoryRequest.make("DELETE").send(info);
        userDeleted = response.statusCode() >= 200 && response.statusCode() < 300;
    }

    @Then("code should be {int}")
    public void verifyStatusCode(int expectedCode) {
        response.then().statusCode(expectedCode);
    }

    @And("the {string} should be {string}")
    public void verifyResponseField(String field, String expectedValue) {
        response.then().body(field, equalTo(expectedValue));
    }

    @And("save {string} in {string}")
    public void saveResponseValue(String field, String variable) {
        Object value = response.then().extract().path(field);
        if (value == null) {
            throw new AssertionError("La respuesta no contiene el campo " + field);
        }
        variables.put(variable, value.toString());
    }

    private Response send(String method, String path, String body) {
        String url = Configuration.BASE_URL + replaceVariables(path);
        IRequest request = FactoryRequest.make(method);
        return request.send(new RequestInfo(url, body));
    }

    private String replaceVariables(String text) {
        String result = text;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @After
    public void cleanupTemporaryResources() {
        if (!projectDeleted && token != null && variables.containsKey("PROJECT_ID")) {
            try {
                sendRequestWithToken("DELETE", "api/projects/PROJECT_ID.json", "");
            } catch (RuntimeException ignored) {
                // Limpieza de mejor esfuerzo: conserva el resultado original del escenario.
            }
        }
        if (!userDeleted && userId > 0 && testEmail != null && testPassword != null) {
            try {
                deleteTemporaryUser();
            } catch (RuntimeException ignored) {
                // Limpieza de mejor esfuerzo: conserva el resultado original del escenario.
            }
        }
    }
}
