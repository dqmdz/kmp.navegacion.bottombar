# Secuencia de creación de archivos y su justificación

Este documento describe **el orden en que se crean (o refactorizan) los archivos**
del proyecto y **por qué** existe cada uno. Sirve como guía para reconstruir la
aplicación desde cero y para entender su diseño.

> Alcance: solo el **código y la configuración propios** de la app. Los recursos
> de plantilla puramente gráficos (iconos `mipmap`, wrappers de Gradle) se mencionan
> de forma resumida.

---

## Criterio de orden

El orden es **de abajo hacia arriba** (*bottom-up*), según las **dependencias**:
primero lo que no depende de nada y al final lo que depende de todo.

```text
Configuración de build
        │
        ▼
Plataforma (expect/actual)  →  Tema  →  Destinos  →  Componentes  →  Pantallas
        │                                                                    │
        └────────────────────────────► Ensamblaje (MainScreen) ◄────────────┘
                                                │
                                                ▼
                                           Raíz (App)
                                                │
                                                ▼
                                     Puntos de entrada por plataforma
                                                │
                                                ▼
                                    Tests → Limpieza → Documentación
```

La regla es simple: **no se puede escribir un archivo que importe otro que todavía
no existe**. Por eso el tema va antes que las pantallas, y las pantallas antes que
`MainScreen`.

---

## Fase 0 — Configuración del proyecto (build)

### 1. `settings.gradle.kts`

- **Rol:** configuración raíz de Gradle.
- **Contenido:** `rootProject.name`, `pluginManagement`/`dependencyResolutionManagement`
  (repositorios `google` filtrado por grupos + `mavenCentral`), el plugin
  `foojay-resolver-convention` y los `include(":androidApp", ":desktopApp", ":shared", ":webApp")`.
- **Justificación:** es el **primer** archivo que Gradle lee. Define de dónde salen
  los plugins y las dependencias (centralizado, no repetido por módulo) y qué
  módulos forman el build. El plugin de *foojay* permite que Gradle **descargue el
  JDK 21** automáticamente, evitando depender de la JDK del sistema.

### 2. `gradle.properties`

- **Rol:** propiedades que afectan a todo el build.
- **Contenido:** memoria de la JVM del daemon, `org.gradle.configuration-cache=true`,
  `org.gradle.caching=true` y flags de Android (`android.useAndroidX`, `android.nonTransitiveRClass`).
- **Justificación:** mejora el **rendimiento** (cache de configuración y de tareas)
  y habilita el comportamiento esperado de AndroidX. Se declara aquí para no
  repetirlo en cada módulo.

### 3. `gradle/libs.versions.toml`

- **Rol:** **catálogo de versiones** (Version Catalog).
- **Contenido:** secciones `[versions]` (una versión por nombre), `[libraries]`
  (coordenadas `grupo:artefacto` + `version.ref`) y `[plugins]` (plugins de Gradle).
- **Justificación:** es la **única fuente de verdad** de versiones. Evita números
  repetidos y permite escribir `libs.compose.material3` en vez de la coordenada
  literal. Aquí se **agregaron** las dos dependencias de la refactorización,
  aisladas con comentarios:
  - `navigation` → `org.jetbrains.androidx.navigation:navigation-compose` (navegación).
  - `material-icons` → `org.jetbrains.compose.material:material-icons-core` (iconos).

### 4. `build.gradle.kts` (raíz)

- **Rol:** declara los plugins del proyecto.
- **Contenido:** los `alias(libs.plugins.…)` con **`apply false`**.
- **Justificación:** centraliza la **versión** de los plugins y evita que cada
  subproyecto los cargue en su propio *classloader* (lo que provocaría errores de
  plugins duplicados). `apply false` significa "resérvalo, pero no lo apliques aquí".

### 5. `shared/build.gradle.kts`

- **Rol:** es el **núcleo**; compila la UI y la lógica compartidas.
- **Contenido:** plugins KMP + Android-KMP library + Compose; *targets* (`iosArm64`,
  `iosSimulatorArm64`, `jvm`, `js`, `wasmJs`, `android`); dependencias por *source set*.
- **Justificación:** declara **una sola vez** la UI para todas las plataformas. El
  framework de iOS se exporta como `Shared`. Aquí se **agregaron** en `commonMain`
  las dependencias de navegación e iconos, en un bloque comentado y separado.

### 6. `androidApp/build.gradle.kts`

- **Rol:** configura el APK de Android.
- **Contenido:** plugin `com.android.application` (con **Kotlin integrado de AGP 9**),
  `applicationId`, `compileSdk`/`minSdk`/`targetSdk`, `buildFeatures.compose = true`.
- **Justificación:** aísla lo específico de Android (identidad de la app, versión de
  SDK, firma). No aplica `kotlin-android` porque **AGP 9 lo integra por defecto**.

### 7. `desktopApp/build.gradle.kts`

- **Rol:** configura la app de escritorio (JVM).
- **Contenido:** plugins `kotlinJvm` + Compose; `compose.desktop` con `mainClass`,
  `packageName` y formatos de distribución (Dmg/Msi/Deb).
- **Justificación:** Compose Desktop necesita su propio bloque `compose.desktop` para
  generar ejecutables nativos; por eso vive en su módulo, no en `shared`.

### 8. `webApp/build.gradle.kts`

- **Rol:** configura la app web.
- **Contenido:** *targets* `js` y `wasmJs` (browser) y dependencias de `:shared` + `compose.ui`.
- **Justificación:** el target web necesita su propio módulo ejecutable con
  `binaries.executable()`; mantenerlo aparte de `shared` deja el núcleo limpio.

---

## Fase 1 — Plataforma (patrón `expect`/`actual`)

Código que cambia según la plataforma, encapsulado tras un contrato común.

### 9. `shared/src/commonMain/.../platform/Platform.kt`

- **Rol:** **contrato** de información de plataforma.
- **Contenido:** `interface Platform { val name: String }` y `expect fun getPlatform(): Platform`.
- **Justificación:** en `commonMain` no hay APIs de sistema. Con `expect` se declara
  *qué* se necesita y cada target aporta el *cómo*. `getPlatform()` **debe** ser
  una función de nivel superior (restricción de `expect/actual`).

### 10–14. `Platform.android.kt`, `Platform.jvm.kt`, `Platform.ios.kt`, `Platform.js.kt`, `Platform.wasmJs.kt`

- **Rol:** implementaciones `actual` una por plataforma.
- **Contenido:** cada una lee la info del sistema (`Build.VERSION.SDK_INT`, `java.version`,
  `UIDevice`, `navigator.userAgent`, o un texto fijo para Wasm).
- **Justificación:** separa el **detalle de plataforma** del código común. La app
  común llama a `getPlatform().name` sin saber en qué sistema corre; agregar un
  target nuevo = un archivo `actual` más.

---

## Fase 2 — Tema

### 15. `shared/src/commonMain/.../ui/theme/Color.kt`

- **Rol:** paletas de color.
- **Contenido:** `LightColors` y `DarkColors` con `lightColorScheme(...)`/`darkColorScheme(...)`,
  sobrescribiendo solo los roles principales.
- **Justificación:** centraliza los **colores** en un solo lugar. Se apoya en los
  valores por defecto de Material 3 para no redefinir cada rol.

### 16. `shared/src/commonMain/.../ui/theme/AppTheme.kt`

- **Rol:** aplicar el tema a toda la UI.
- **Contenido:** `class AppTheme { @Composable fun Content(darkTheme, content) }` que
  envuelve `MaterialTheme` y elige `LightColors`/`DarkColors`.
- **Justificación:** es el **único** punto que decide claro/oscuro; el resto consume
  `MaterialTheme.colorScheme`. Se crea **después** de `Color.kt` porque depende de él.

---

## Fase 3 — Modelo de navegación (destinos)

### 17. `shared/src/commonMain/.../navigation/AppDestination.kt`

- **Rol:** **punto único de verdad** de las pestañas.
- **Contenido:** `enum class AppDestination(route, label, icon)` con `Home`, `Search`,
  `Settings` y `fromRoute(route)` en el `companion object`.
- **Justificación:** cada pestaña reúne sus datos (ruta, etiqueta, icono) en un tipo.
  Al ser un `enum`, la BottomBar se genera sola (`entries`) y el compilador impide
  rutas inválidas. Se crea **antes** que los componentes y `MainScreen` porque ambos
  dependen de él.

---

## Fase 4 — Componentes de UI

### 18. `shared/src/commonMain/.../ui/components/AppBottomBar.kt`

- **Rol:** barra de navegación inferior.
- **Contenido:** `class AppBottomBar { @Composable fun Content(destinations, currentRoute, onSelect, modifier) }`
  construida con `NavigationBar` + `NavigationBarItem`.
- **Justificación:** es un componente **presentacional**: no conoce la navegación,
  solo recibe datos y emite el callback `onSelect`. Así es reutilizable y testeable.
  Depende de `AppDestination` (Fase 3) y de los iconos (`material-icons-core`).

---

## Fase 5 — Pantallas

Cada pantalla es una **clase** con un método composable `Content(...)` (patrón
aplicado de forma uniforme en todo el proyecto).

### 19. `shared/src/commonMain/.../ui/screens/HomeScreen.kt`

- **Rol:** pestaña "Inicio".
- **Contenido:** logo, `getPlatform().name` y un **contador** con `rememberSaveable`.
- **Justificación:** demuestra el uso de `expect/actual` desde la UI y el **estado
  local persistente**. Usa `mutableIntStateOf` para evitar autoboxing.

### 20. `shared/src/commonMain/.../ui/screens/SearchScreen.kt`

- **Rol:** pestaña "Buscar".
- **Contenido:** un `OutlinedTextField` que filtra en vivo una lista interna
  (`private val topics`) y la muestra en un `LazyColumn`.
- **Justificación:** demuestra **estado local de entrada de texto**, **cálculo
  derivado memoizado** con `remember(query)` y **listas eficientes**.

### 21. `shared/src/commonMain/.../ui/screens/SettingsScreen.kt`

- **Rol:** pestaña "Ajustes".
- **Contenido:** `Content(darkTheme, onDarkThemeChange, modifier)` y el helper privado
  `ToggleRow(...)`.
- **Justificación:** contrasta **estado elevado** (el tema lo controla `App`) con
  **estado local** ("Notificaciones"). Es la pantalla que demuestra por qué el estado
  global vive en la raíz.

> Nota de diseño: los valores que cambian (tema) van como **parámetros de `Content()`**,
> no del constructor, para no recrear la instancia con `remember`.

---

## Fase 6 — Ensamblaje de la navegación

### 22. `shared/src/commonMain/.../navigation/MainScreen.kt`

- **Rol:** contenedor principal; **único** lugar que conoce el `NavController`.
- **Contenido:** `class MainScreen { @Composable fun Content(darkTheme, onDarkThemeChange, modifier) }`:
  `Scaffold` + `AppBottomBar` + `NavHost`; instancia las pantallas con `remember`; y
  el método privado `navigateToTab(navController, destination)` con
  `popUpTo`/`launchSingleTop`/`saveState`/`restoreState`.
- **Justificación:** centraliza **toda** la navegación (las pantallas no reciben el
  controller) y aplica el comportamiento estándar de una BottomBar. Se crea **después**
  de las pantallas y la barra porque las compone a todas.

---

## Fase 7 — Raíz de la aplicación

### 23. `shared/src/commonMain/.../App.kt`

- **Rol:** punto de entrada **común** de la UI.
- **Contenido:** `class App { @Composable fun Content() }` con el estado global
  `darkTheme` (`rememberSaveable`), que envuelve `AppTheme` y `MainScreen`. Incluye
  `AppPreview` (`@Preview`).
- **Justificación:** concentra el **estado global** (tema) y compone las dos piezas
  superiores (tema + contenedor). Se crea al final del núcleo porque depende de
  `AppTheme` y `MainScreen`. `AppPreview` se mantiene **como función** porque `@Preview`
  exige un composable de nivel superior.

---

## Fase 8 — Puntos de entrada por plataforma

Son **delgados**: crean el entorno de su plataforma y montan `App().Content()`.

### 24. `androidApp/src/main/kotlin/org/prog2/navegacion/MainActivity.kt`

- **Rol:** entrada de Android.
- **Contenido:** `ComponentActivity` que llama a `enableEdgeToEdge()` y
  `setContent { App().Content() }`, más `AppAndroidPreview` (`@Preview`).
- **Justificación:** Android necesita una `Activity` con `setContent`. Mantenerla
  mínima garantiza que no haya lógica de UI específica de Android.

> Acompañan a este archivo: `AndroidManifest.xml` (declara la Activity como
> *launcher*), `res/values/strings.xml` (`app_name`) y los iconos `mipmap` de
> plantilla.

### 25. `desktopApp/src/main/kotlin/org/prog2/navegacion/main.kt`

- **Rol:** entrada de escritorio.
- **Contenido:** `fun main() = application { Window(...) { App().Content() } }`.
- **Justificación:** Compose Desktop se lanza desde un `main` de JVM; **debe** ser
  `fun main()` (requisito del entorno). Abre la ventana nativa.

### 26. `webApp/src/webMain/kotlin/org/prog2/navegacion/main.kt`

- **Rol:** entrada web (JS y Wasm).
- **Contenido:** `fun main()` con `ComposeViewport { App().Content() }`.
- **Justificación:** monta Compose sobre el DOM de la página. Acompañado de
  `resources/index.html` (página anfitriona) y `resources/styles.css` (estilos y
  pantalla de carga).

### 27. `shared/src/iosMain/kotlin/org/prog2/navegacion/MainViewController.kt`

- **Rol:** entrada de iOS.
- **Contenido:** `fun MainViewController() = ComposeUIViewController { App().Content() }`.
- **Justificación:** iOS consume un `UIViewController` desde Swift. Se mantiene
  **como función** porque Swift la invoca por su nombre
  (`MainViewControllerKt.MainViewController()`); convertirla en clase cambiaría el
  símbolo exportado.
- **Acompañan:** `iosApp/iosApp/iOSApp.swift` (arranque SwiftUI) y
  `iosApp/iosApp/ContentView.swift` (envuelve el `UIViewController`).

---

## Fase 9 — Recursos compartidos

### 28. `shared/src/commonMain/composeResources/drawable/compose-multiplatform.xml`

- **Rol:** logo mostrado en la pantalla de Inicio.
- **Justificación:** Compose Multiplatform genera la clase `Res` a partir de
  `composeResources/`; así la imagen funciona en **todas** las plataformas sin
  duplicarla. Se referencia con `Res.drawable.compose_multiplatform`.

---

## Fase 10 — Tests

### 29. `shared/src/commonTest/.../navigation/AppDestinationTest.kt`

- **Rol:** pruebas de las invariantes de `AppDestination`.
- **Contenido:** tres destinos, rutas únicas, etiquetas no vacías, y resolución de
  `fromRoute` (incluido el caso `null`).
- **Justificación:** protege las reglas de las que dependen la barra y el `NavHost`.
  Al ser **puro** (sin UI), se ejecuta en **todos** los targets.

### 30. Tests de humo por target

- `SharedCommonTest.kt` (common), `SharedLogicDesktopTest.kt` (jvm),
  `SharedLogicAndroidHostTest.kt` (android host), `SharedLogicIOSTest.kt` (ios),
  `SharedLogicWebTest.kt` (web).
- **Rol:** verificar que la infraestructura de tests existe y corre en cada target.
- **Justificación:** cada plataforma tiene su propio *runner*; estos tests triviales
  actúan como "canario" de que la configuración de test funciona.

---

## Fase 11 — Limpieza del template

- **Se eliminaron:** `Greeting.kt`, `GreetingUtil.kt` y los `Platform.*.kt` antiguos
  (estaban en la raíz del paquete).
- **Justificación:** eran **sobras del template** (una función `sayHello` y un saludo
  de ejemplo) que ya no se usaban; `Platform` se **reubicó** en el paquete
  `platform/` para reflejar su capa. Eliminar código muerto mantiene el proyecto
  legible.

---

## Fase 12 — Documentación

### 31. `README.md`

- **Rol:** visión general del proyecto.
- **Contenido:** estructura, flujo, navegación, **dependencias con su justificación**,
  build por módulo, cómo ejecutar y tests.
- **Justificación:** punto de entrada para cualquiera que lea el repositorio.

### 32. `docs/CREACION-DE-ARCHIVOS.md` (este documento)

- **Rol:** registrar la **secuencia de creación** y la justificación de cada archivo.
- **Justificación:** documenta el *por qué* y el *orden*, información que se pierde
  con el tiempo y que es clave para reconstruir o extender el proyecto.

---

## Resumen de la secuencia

| # | Archivo | Fase | Depende de |
|---|---|---|---|
| 1 | `settings.gradle.kts` | Build | — |
| 2 | `gradle.properties` | Build | — |
| 3 | `gradle/libs.versions.toml` | Build | — |
| 4 | `build.gradle.kts` (raíz) | Build | 3 |
| 5 | `shared/build.gradle.kts` | Build | 3 |
| 6 | `androidApp/build.gradle.kts` | Build | 3 |
| 7 | `desktopApp/build.gradle.kts` | Build | 3 |
| 8 | `webApp/build.gradle.kts` | Build | 3 |
| 9 | `platform/Platform.kt` | Plataforma | — |
| 10–14 | `Platform.<target>.kt` | Plataforma | 9 |
| 15 | `ui/theme/Color.kt` | Tema | — |
| 16 | `ui/theme/AppTheme.kt` | Tema | 15 |
| 17 | `navigation/AppDestination.kt` | Destinos | — |
| 18 | `ui/components/AppBottomBar.kt` | Componentes | 17 |
| 19 | `ui/screens/HomeScreen.kt` | Pantallas | 16, 9 |
| 20 | `ui/screens/SearchScreen.kt` | Pantallas | 16 |
| 21 | `ui/screens/SettingsScreen.kt` | Pantallas | 16 |
| 22 | `navigation/MainScreen.kt` | Ensamblaje | 17, 18, 19, 20, 21 |
| 23 | `App.kt` | Raíz | 16, 22 |
| 24 | `androidApp/.../MainActivity.kt` | Entrada | 23 |
| 25 | `desktopApp/.../main.kt` | Entrada | 23 |
| 26 | `webApp/.../main.kt` | Entrada | 23 |
| 27 | `iosMain/.../MainViewController.kt` | Entrada | 23 |
| 28 | `composeResources/.../compose-multiplatform.xml` | Recursos | — |
| 29 | `commonTest/.../AppDestinationTest.kt` | Tests | 17 |
| 30 | Tests de humo (5) | Tests | build de cada target |
| 31 | `README.md` | Documentación | todo |
| 32 | `docs/CREACION-DE-ARCHIVOS.md` | Documentación | todo |
