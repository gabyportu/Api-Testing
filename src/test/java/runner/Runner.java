package runner;

import io.cucumber.java.After;
import io.cucumber.java.Before;

/** Hooks de Cucumber*/
public class Runner {
    @Before
    public void beforeScenario() {
        System.out.println("Iniciando escenario de Proyecto2");
    }

    @After
    public void afterScenario() {
        System.out.println("Finalizo escenario de Proyecto2");
    }
}
