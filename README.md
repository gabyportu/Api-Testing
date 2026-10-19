# Proyecto 2 - Seccion 2 - REST API Todo.ly

## Los dos flujos

1. `projectTokenCrud.feature`: crea un usuario temporal, obtiene su token y hace Create, Read, Update y Delete de un proyecto. El token viaja en el encabezado `Token`.
2. `userCrud.feature`: crea un usuario temporal, actualiza su `FullName` y elimina ese usuario.

## Como viaja una solicitud

`feature` (pasos legibles) -> `TodoLySteps` (interpreta el paso) -> `FactoryRequest` (elige GET/POST/PUT/DELETE) -> `RequestInfo` (URL/body/autenticacion) -> Todo.ly. REST Assured envia la solicitud y recibe la respuesta; Cucumber compara el status y los campos esperados. Es el mismo patron de `api_v6` (`MyStepdefsClean`, `FactoryRequest`, `RequestInfo` y clases `Request*`). No necesitas Postman para correr estos casos: Postman sirve para probar a mano; aqui la prueba se dispara desde Java con Gradle.

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


