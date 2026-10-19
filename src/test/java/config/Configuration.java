package config;

import java.util.UUID;

public final class Configuration {
    public static final String BASE_URL = "https://todo.ly/";
    public static final String EMAIL = System.getenv("TODO_LY_EMAIL");
    public static final String PASSWORD = System.getenv("TODO_LY_PASSWORD");
    public static final String TEST_PASSWORD = valueOrDefault("TODO_LY_TEST_PASSWORD", PASSWORD);

    private Configuration() { }

    public static void validate() {
        if (EMAIL == null || EMAIL.isBlank() || PASSWORD == null || PASSWORD.isBlank()) {
            throw new IllegalStateException(
                    "Define TODO_LY_EMAIL y TODO_LY_PASSWORD en el entorno antes de ejecutar Cucumber.");
        }
        if (TEST_PASSWORD == null || TEST_PASSWORD.isBlank()) {
            throw new IllegalStateException("Define TODO_LY_TEST_PASSWORD o TODO_LY_PASSWORD.");
        }
        if (!EMAIL.contains("@")) {
            throw new IllegalStateException("TODO_LY_EMAIL debe tener formato de correo.");
        }
    }

    public static String nextTestEmail() {
        int at = EMAIL.lastIndexOf('@');
        String localPart = EMAIL.substring(0, at);
        String domain = EMAIL.substring(at + 1);
        return localPart + "+api-v6-" + UUID.randomUUID().toString().substring(0, 8) + "@" + domain;
    }

    public static String getTestPassword() {
        return TEST_PASSWORD;
    }

    private static String valueOrDefault(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}

