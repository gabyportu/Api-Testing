# Proyecto 2 - Seccion 2 - REST API Todo.ly

Este proyecto sigue el patron del profesor en `api_v6`: `FactoryRequest` selecciona un request, `RequestInfo` guarda URL/body/autenticacion, y `RequestGet`, `RequestPost`, `RequestPut` y `RequestDelete` ejecutan el verbo HTTP. Cucumber lee los escenarios y `TodoLySteps` conecta cada frase con esa fabrica.

## Los dos flujos

1. `projectTokenCrud.feature`: crea un usuario temporal, obtiene su token y hace Create, Read, Update y Delete de un proyecto. El token viaja en el encabezado `Token`.
2. `userCrud.feature`: crea un usuario temporal, actualiza su `FullName` y elimina ese usuario.

## Como viaja una solicitud

`feature` (pasos legibles) -> `TodoLySteps` (interpreta el paso) -> `FactoryRequest` (elige GET/POST/PUT/DELETE) -> `RequestInfo` (URL/body/autenticacion) -> Todo.ly. REST Assured envia la solicitud y recibe la respuesta; Cucumber compara el status y los campos esperados. Es el mismo patron de `api_v6` (`MyStepdefsClean`, `FactoryRequest`, `RequestInfo` y clases `Request*`). No necesitas Postman para correr estos casos: Postman sirve para probar a mano; aqui la prueba se dispara desde Java con Gradle.

## Que hace cada carpeta

- `build.gradle`: dependencias y tarea `cucumberCli`.
- `src/test/java/client`: `FactoryRequest`, `IRequest`, `RequestInfo`, soporte de autenticacion y una clase por verbo HTTP.
- `src/test/java/config/Configuration.java`: URL de Todo.ly, variables de entorno y correo unico temporal.
- `src/test/java/runner/TodoLySteps.java`: pasos Gherkin, respuestas, variables de Id y limpieza.
- `src/test/java/runner/Runner.java`: hooks `Before`/`After`, como en `api_v6`.
- `src/test/resources`: los dos escenarios Cucumber.

`api_v6` es solo la referencia. Los cambios de esta tarea se ejecutan desde la carpeta `Proyecto2`.

## Como probarlo desde IntelliJ IDEA

1. Abre la carpeta `Proyecto2` como proyecto Gradle.
2. Ve a **File > Settings > Build, Execution, Deployment > Build Tools > Gradle** y selecciona **JDK 21** en **Gradle JVM**. Este proyecto usa Gradle 8.8.
3. Abre la terminal integrada de IntelliJ (**View > Tool Windows > Terminal**) y confirma que estás en la carpeta `Proyecto2`.
4. En esa misma terminal, detén el daemon anterior para que la siguiente ejecución de Gradle lea las variables de esta sesión:

```powershell
$env:GRADLE_USER_HOME = "$env:USERPROFILE\.gradle"
.\gradlew.bat --stop
```

5. En esa misma terminal de PowerShell, configura el correo y la contraseña. La contraseña se solicita de forma oculta y no se guarda en el código:

```powershell
$env:GRADLE_USER_HOME = "$env:USERPROFILE\.gradle"
$env:TODO_LY_EMAIL = Read-Host "Correo de Todo.ly"
$securePassword = Read-Host "Contraseña de Todo.ly" -AsSecureString
$bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
try {
    $env:TODO_LY_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
}
```

6. Ejecuta los dos escenarios sin reutilizar un daemon anterior:

```powershell
.\gradlew.bat --no-daemon clean cucumberCli -Psuite="@Regression"
```

Para probar solo uno, usa `-Psuite="@project-crud"` o `-Psuite="@user-crud"`. La prueba pasa cuando Gradle termina con `BUILD SUCCESSFUL` y Cucumber informa los escenarios como `passed`. El reporte queda en `report/cucumber-report.html`.

## Resultado comprobado

La ultima ejecucion real en Todo.ly termino con `BUILD SUCCESSFUL`: 2 escenarios y 26 pasos aprobados. El reporte se genero en `report/cucumber-report.html`. En el texto de consola pueden aparecer fallos de intentos anteriores; revisa el resultado al final, despues del ultimo comando.

Ejecuta el comando desde esa terminal, no desde el panel de tareas Gradle: el daemon del panel puede haberse iniciado antes de definir las variables y no verlas.

### Si aparece `PKIX path building failed`

Ese error ya significa que las variables llegaron al test y este intentó conectarse; Java no confía en la cadena del certificado HTTPS que recibe para Todo.ly. No desactives la validación TLS. Confirma que la fecha de Windows es correcta y que el navegador abre `https://todo.ly`. Si el navegador lo abre pero Java no, puede haber inspección HTTPS de antivirus/proxy o falta una CA en el truststore del JDK 21 elegido en IntelliJ. En una red institucional, pide a soporte la CA y el procedimiento oficial para ese JDK. El paso que lee el Id también informa ahora el status/respuesta si la API no logra crear el usuario.

## Importante sobre las cuentas temporales

El correo ingresado se usa como base para crear un alias unico para cada prueba. La cuenta personal no se modifica ni se elimina. El hook `@After` trata de borrar el usuario y el proyecto temporales si el escenario falla antes de llegar a su paso DELETE. La cuenta de Todo.ly debe permitir registrar el alias generado.

## Repositorio

Referencia publica `api_v6`: https://github.com/eynar-pari/api_v6. El codigo de Proyecto2 aun no esta publicado; esta carpeta no tiene un remoto Git configurado. Reemplaza el enlace de referencia por el repositorio publico de Proyecto2 cuando lo subas.

No compartas ni subas contrasenas o tokens a GitHub.

