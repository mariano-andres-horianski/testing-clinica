# testing-clinica

Trabajo final de **Calidad de Software** del **Grupo 7**: testing del sistema de gestión de la clínica (`TPE2026`).

El código del sistema es el provisto por la cátedra; el trabajo consiste en diseñar y ejecutar las pruebas. Los tests que fallan lo hacen a propósito: señalan los defectos encontrados, que se documentan en el informe.

## Contenido

| Punto | Qué se prueba | Clases de test |
|---|---|---|
| 1. Caja negra | `AsociadoDTO` | `TestAsociadoDTO` |
| 2. Integración sin mocks | `DataAccessObject` y `BaseDeDatos` (con MySQL) | `TestDataAccessObject`, `TestBaseDeDatosConDatos`, `TestBaseDeDatosSinDatos`, `TestBaseDeDatosSinConexion` |
| 3. Integración con mocks | `Ambulancia`, los estados (`State`) y `RetornoAutomatico` | `TestAmbulanciaMock`, `TestAmbulanciaIntegracion` |

Los tests están en `TPE2026/src/main/java/test/`, junto con las clases `EscenarioBD*` que preparan la base de datos para los tests del punto 2.

## Requisitos

- Java 8 o superior
- Maven (dependencias: JUnit 4.13.2, Mockito 3.2.4, MySQL Connector/J 8.4.0)
- Para el punto 2: un servidor MySQL en `localhost:3306` con usuario y contraseña `progra_c`

## Cómo ejecutar

1. En Eclipse: **File → Import → Maven → Existing Maven Projects** y elegir la carpeta `TPE2026`.
2. Clic derecho sobre la clase de test → **Run As → JUnit Test** (runner JUnit 4).

El detalle de escenarios, clases de equivalencia, casos de prueba, resultados y fallos detectados está en el informe del trabajo.
