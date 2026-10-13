# bottombar

Aplicación de ejemplo **Kotlin Multiplatform + Compose Multiplatform** con una
**BottomBar** que navega entre tres pantallas (**Inicio**, **Buscar** y
**Ajustes**), compartida para **Android, iOS, Desktop (JVM) y Web (JS + WasmJs)**.

El objetivo del proyecto es ser una base **simple y ordenada**: la UI es común, la
navegación está aislada en una sola capa y agregar una pantalla nueva son dos pasos.

---

## Tabla de contenidos

1. [Estructura del proyecto](#estructura-del-proyecto)
2. [Flujo de la aplicación](#flujo-de-la-aplicación)
3. [Navegación](#navegación)
4. [Dependencias](#dependencias)
5. [Configuración de build por módulo](#configuración-de-build-por-módulo)
6. [Ejecutar](#ejecutar)
7. [Tests](#tests)
8. [Documentación del código](#documentación-del-código)
9. [Notas](#notas)
10. [Creación de archivos y justificación](./docs/CREACION-DE-ARCHIVOS.md)

---

## Estructura del proyecto

Organización por **capas**, con la UI y la lógica compartidas en `shared` y un
módulo delgado por plataforma como punto de entrada.

```text
.
├── gradle/
│   └── libs.versions.toml            # Catálogo de versiones y dependencias
├── build.gradle.kts                  # Plugins raíz (declarados con `apply false`)
├── settings.gradle.kts               # Repositorios y módulos incluidos
├── gradle.properties                 # Propiedades de Kotlin/Gradle/Android
├── README.md                         # Documentación principal
├── docs/
│   └── CREACION-DE-ARCHIVOS.md       # Secuencia de creación y justificación
│
├── androidApp/                       # Punto de entrada Android (APK)
│   └── src/main/kotlin/org/prog2/navegacion/MainActivity.kt
├── desktopApp/                       # Punto de entrada Desktop (JVM)
│   └── src/main/kotlin/org/prog2/navegacion/main.kt
├── webApp/                           # Punto de entrada Web (JS + Wasm)
│   └── src/webMain/kotlin/org/prog2/navegacion/main.kt
├── iosApp/                           # Proyecto Xcode (punto de entrada iOS)
│   └── iosApp/ContentView.swift
│
└── shared/                           # UI + lógica compartida (el núcleo)
    └── src/
        ├── commonMain/kotlin/org/prog2/navegacion/
        │   ├── App.kt                        # class App → Content() (raíz + tema)
        │   ├── navigation/
        │   │   ├── AppDestination.kt         # enum de las 3 pestañas (route/label/icon)
        │   │   └── MainScreen.kt             # class MainScreen → Content()
        │   ├── ui/
        │   │   ├── components/
        │   │   │   └── AppBottomBar.kt       # class AppBottomBar → Content()
        │   │   ├── screens/
        │   │   │   ├── HomeScreen.kt         # class HomeScreen → Content()
        │   │   │   ├── SearchScreen.kt       # class SearchScreen → Content()
        │   │   │   └── SettingsScreen.kt     # class SettingsScreen → Content()
        │   │   └── theme/
        │   │       ├── AppTheme.kt           # class AppTheme → Content()
        │   │       └── Color.kt              # Paletas claro/oscuro
        │   └── platform/
        │       └── Platform.kt               # `expect` de info de plataforma
        ├── androidMain/.../platform/Platform.android.kt
        ├── jvmMain/.../platform/Platform.jvm.kt
        ├── iosMain/.../MainViewController.kt, platform/Platform.ios.kt
        ├── jsMain/.../platform/Platform.js.kt
        ├── wasmJsMain/.../platform/Platform.wasmJs.kt
        └── commonTest/.../navigation/AppDestinationTest.kt
```

### Capas dentro de `shared`

| Carpeta | Responsabilidad | Regla de dependencia |
|---|---|---|
| `navigation/` | Define las pestañas (`AppDestination`) y el contenedor (`MainScreen`). Es **el único lugar** que conoce el `NavController`. | Depende de `ui/`. |
| `ui/screens/` | Una **clase** por pestaña, con un método `Content()` que dibuja la UI. Reciben datos y callbacks; **no** conocen la navegación. | Depende de `ui/theme/` y `platform/`. |
| `ui/components/` | Componentes reutilizables y presentacionales (**clases** con `Content()`, p. ej. la barra). | Independientes de la navegación. |
| `ui/theme/` | Tema y paletas centralizadas. | Sin dependencias del proyecto. |
| `platform/` | Código específico de cada plataforma vía `expect`/`actual`. | Contrato en `commonMain`, implementaciones por target. |

### Módulos por plataforma

Cada módulo de aplicación es **delgado**: solo crea el punto de entrada y llama a
`App().Content()`.

| Módulo | Plataforma | Punto de entrada | Salida |
|---|---|---|---|
| `androidApp` | Android | `MainActivity` (Activity + `setContent`) | APK |
| `desktopApp` | Desktop (JVM) | `main.kt` (ventana de Compose Desktop) | App nativa |
| `webApp` | Web (JS + Wasm) | `main.kt` (`ComposeViewport`) | Bundle web |
| `iosApp` | iOS | `MainViewController` (desde Xcode/SwiftUI) | App iOS |

---

## Flujo de la aplicación

```text
Punto de entrada (según plataforma)
        │  llama a
        ▼
   App().Content()                   ← estado global: darkTheme (rememberSaveable)
        │
        ├─ AppTheme().Content(darkTheme)   ← aplica la paleta de Material 3
        │
        └─ MainScreen().Content(...)       ← Scaffold + BottomBar + NavHost
                │
                ├─ AppBottomBar().Content(AppDestination.entries, currentRoute, onSelect)
                │
                └─ NavHost(startDestination = "home")
                        ├─ "home"     → HomeScreen().Content()
                        ├─ "search"   → SearchScreen().Content()
                        └─ "settings" → SettingsScreen().Content(darkTheme, onDarkThemeChange)
```

El estado del **modo oscuro** nace en `App.Content()`, se pasa hacia abajo a
`MainScreen.Content()` y de ahí a `SettingsScreen.Content()`; al alternarlo en
Ajustes, `AppTheme` recompone toda la app. Es un ejemplo de **estado elevado**
(single source of truth).

### Patrón: cada componente de UI es una clase

Todo el código de la interfaz está encapsulado en **clases con un método composable
`Content(...)`**: la raíz, el contenedor, la barra, el tema y las pantallas.

| Clase | Método `Content(...)` | Rol |
|---|---|---|
| `App` | `Content()` | Raíz: estado del tema. |
| `MainScreen` | `Content(darkTheme, onDarkThemeChange, modifier)` | Scaffold + BottomBar + NavHost. |
| `AppBottomBar` | `Content(destinations, currentRoute, onSelect, modifier)` | Barra inferior. |
| `AppTheme` | `Content(darkTheme, content)` | Aplica Material 3. |
| `HomeScreen` / `SearchScreen` / `SettingsScreen` | `Content(...)` | Cada pestaña. |

Ejemplo:

```kotlin
class HomeScreen {
    @Composable
    fun Content(modifier: Modifier = Modifier) { /* UI */ }
}
```

Las instancias se crean **una sola vez** con `remember` y luego se invoca `Content()`:

```kotlin
val homeScreen = remember { HomeScreen() }
// ...
composable(AppDestination.Home.route) { homeScreen.Content() }
```

Ventajas de este estilo:

- **Orientado a objetos:** cada componente es un tipo con una responsabilidad clara.
- **Sin costo por recomposición:** `remember` conserva las instancias; no se recrean.
- **Estado estable:** los valores que cambian (p. ej. el tema) se pasan como parámetros
  de `Content()`, no del constructor, para no obligar a recrear la instancia.

#### Lo que se mantiene como función (y por qué)

No **todo** puede (ni debe) ser clase; estos casos son funciones por una restricción
del lenguaje o de la plataforma:

| Función | Motivo |
|---|---|
| `getPlatform()` (`platform/`) | Es una declaración `expect`/`actual`: **debe** ser función de nivel superior. |
| `main()` (`desktopApp`, `webApp`) | Requisito del entorno: el punto de entrada JVM/JS/Wasm es `fun main()`. |
| `MainViewController()` (iOS) | Fábrica que Swift invoca por su nombre (`MainViewControllerKt.MainViewController()`). |
| `AppPreview()` / `AppAndroidPreview()` | Son `@Preview` para el IDE: composables de nivel superior sin parámetros. |

---

## Navegación

Se usa **`navigation-compose` de JetBrains** (`org.jetbrains.androidx.navigation`),
la librería oficial de navegación para Compose Multiplatform. Toda la navegación
vive **exclusivamente** en la carpeta `navigation/`:

- **`AppDestination`** es el punto único de verdad: un `enum` donde cada entrada
  tiene `route` (identificador de navegación), `label` (texto de la pestaña) e
  `icon` (vector). Incluye `fromRoute(route)` para resolver un destino.
- **`MainScreen`** conecta `AppBottomBar` con el `NavHost` y aplica el patrón
  estándar de pestañas: `popUpTo(startDestination)` + `launchSingleTop` +
  `saveState`/`restoreState`, de modo que la pila queda a un nivel y cada pestaña
  conserva su estado.
- Las **pantallas nunca reciben el `NavController`**; solo datos y callbacks.

### Agregar una pantalla nueva

1. Añade una entrada al `enum AppDestination` (con una `route` **única**):

   ```kotlin
   Perfil(route = "profile", label = "Perfil", icon = Icons.Filled.Person),
   ```

2. Registra su `composable` en `MainScreen`:

   ```kotlin
   composable(AppDestination.Perfil.route) { ProfileScreen() }
   ```

La BottomBar se genera **automáticamente** desde `AppDestination.entries`, así que
no hay que tocarla.

---

## Dependencias

### Cómo se declaran (Version Catalog)

Todas las versiones y coordenadas viven en **`gradle/libs.versions.toml`**, que
Gradle expone como el catálogo `libs.*`:

- `[versions]` — versiones con nombre (una fuente, sin números repetidos).
- `[libraries]` — coordenadas `grupo:artefacto` + `version.ref`.
- `[plugins]` — plugins de Gradle con su versión.

Así, en un `build.gradle.kts` se escribe `implementation(libs.compose.material3)`
en lugar de la coordenada y versión literales.

### Dependencias agregadas en esta refactorización

Se agregaron **exactamente dos** dependencias nuevas, ambas al catálogo y al bloque
`commonMain` de `shared/build.gradle.kts`.

#### 1. `navigation-compose` — navegación entre pestañas

| | |
|---|---|
| **Versión** | `navigation = "2.10.0-rc01"` |
| **Alias catálogo** | `androidx-navigation-compose` |
| **Coordenada** | `org.jetbrains.androidx.navigation:navigation-compose` |
| **Archivo** | `gradle/libs.versions.toml`, `shared/build.gradle.kts` (`commonMain`) |

**Justificación.** Es la librería de navegación **oficial de JetBrains** para
Compose Multiplatform y aporta `NavHost`/`NavController` con una pila real de
navegación. El requisito era una BottomBar que cambie de pantalla **sin apilar
destinos** y **conservando el estado** de cada pestaña, además de funcionar igual
en Android, iOS, Desktop y Web. Se eligió frente a alternativas (navegación manual
con estado, o terceros como Voyager) por ser el estándar del ecosistema y estar
pensada para multiplataforma.

**Por qué la versión `2.10.0-rc01`.** Es la más reciente del catálogo de JetBrains
y, sobre todo, **alinea sus dependencias con las ya presentes en el proyecto**:
`org.jetbrains.androidx.lifecycle:2.11.0` y `androidx.savedstate:1.4.0`, lo que
evita conflictos de versiones al resolver. Publica variantes para **todos** los
targets del proyecto (Android, `iosArm64`/`iosSimulatorArm64`, JVM, JS y WasmJs).

**Qué habilita.** `rememberNavController()`, `NavHost`, `composable(route)` y
`currentBackStackEntryAsState()` — usados en `MainScreen.kt`.

> Nota: se usan **rutas tipo `String`** (no *type-safe routes* con `@Serializable`)
> para no añadir el plugin de serialización; `navigation-compose` ya trae
> `kotlinx-serialization-core` de forma transitiva si más adelante se desea migrar.

#### 2. `material-icons-core` — iconos de la BottomBar

| | |
|---|---|
| **Versión** | `material-icons = "1.7.3"` |
| **Alias catálogo** | `compose-material-iconsCore` |
| **Coordenada** | `org.jetbrains.compose.material:material-icons-core` |
| **Archivo** | `gradle/libs.versions.toml`, `shared/build.gradle.kts` (`commonMain`) |

**Justificación.** La barra necesita tres iconos (`Icons.Filled.Home`,
`Icons.Filled.Search`, `Icons.Filled.Settings`). En Compose Multiplatform,
**Material 3 no incluye iconos de forma transitiva**, por lo que hay que declarar
el artefacto explícitamente. Se prefirió esta solución a dibujar vectores a mano
(`ImageVector` inline) por simplicidad y por ofrecer el aspecto Material estándar.

**Por qué la versión `1.7.3`.** JetBrains **congeló** los artefactos de iconos en
la versión `1.7.3` (no existen `material-icons-*` en `1.12.x`), por lo que es la
única disponible compatible con el ecosistema actual. Aunque antigua, solo aporta
vectores (`ImageVector`), una API estable que convive sin problemas con el runtime
`1.12.1`. Sus variantes nativas se publican como `uikitArm64`/`uikitSimArm64`, cuyo
atributo `native.target` **mapea a `ios_arm64`/`ios_simulator_arm64`**, de modo que
resuelve también para los targets de iOS del proyecto.

**Qué habilita.** `Icon(imageVector = ...)` en `AppBottomBar.kt` y las entradas de
`AppDestination`.

> Alternativas descartadas: iconos por emoji/texto (menos profesional) y vectores
> propios (más código sin dependencia). Se puede volver a cualquiera de ellas
> eliminando esta dependencia.

### Contexto: otras dependencias relevantes (ya presentes)

Estas ya venían del template/refactor y se mantienen porque son la base de Compose
Multiplatform:

| Dependencia | Dónde | Para qué |
|---|---|---|
| `compose.runtime` / `foundation` / `ui` | `shared` | Núcleo de Compose (estado, layout, Canvas/DOM). |
| `compose.material3` (`1.12.0-alpha03`) | `shared` | `Scaffold`, `NavigationBar`, `Card`, `Switch`, etc. |
| `compose.components.resources` | `shared` | Recursos compartidos (`Res.drawable.compose_multiplatform`). |
| `compose.uiToolingPreview` / `uiTooling` | `shared`, `androidApp`, `desktopApp` | `@Preview` y herramientas de inspección. |
| `lifecycle-viewmodel-compose` / `runtime-compose` | `shared` | Base del ciclo de vida usada por `navigation-compose`. |
| `androidx.activity:activity-compose` | `androidApp` | `setContent` y `enableEdgeToEdge` en Android. |
| `kotlin-browser` (`kotlin-wrappers`) | `shared` (`jsMain`) | `navigator.userAgent` en `Platform.js.kt`. |
| `kotlinx-coroutines-swing` | `desktopApp` | Dispatcher de UI para Compose Desktop. |

### Qué cambió en cada archivo de build

| Archivo | Cambio |
|---|---|
| `gradle/libs.versions.toml` | `[versions]`: `navigation`, `material-icons`. `[libraries]`: `androidx-navigation-compose`, `compose-material-iconsCore`. |
| `shared/build.gradle.kts` | `commonMain.dependencies`: `+ androidx.navigation.compose`, `+ compose.material.iconsCore`. |
| `build.gradle.kts` (raíz) | Sin cambios (los plugins ya se declaran con `apply false`). |
| `androidApp` / `desktopApp` / `webApp` | Sin cambios (ya dependen de `:shared`). |

---

## Configuración de build por módulo

### `shared/build.gradle.kts` (el núcleo)

- Plugins: `kotlinMultiplatform`, `androidMultiplatformLibrary` (nuevo plugin KMP
  de Android), `composeMultiplatform` y `composeCompiler`.
- Targets: `iosArm64`, `iosSimulatorArm64`, `jvm`, `js` (browser), `wasmJs`
  (browser) y `android` con `androidResources.enable = true` y `namespace` propio.
- Dependencias de `commonMain`: ver [Dependencias](#dependencias).
- El framework de iOS se exporta como **`Shared`** (estático).

### `androidApp`, `desktopApp`, `webApp`

Cada uno aplica solo lo que necesita y depende de `project(":shared")`. No contienen
lógica de UI: son *entry points*.

> Con **AGP 9**, el módulo Android usa **Kotlin integrado** (no se aplica
> `org.jetbrains.kotlin.android`), y `shared` migra a
> `com.android.kotlin.multiplatform.library`. Requiere **Android Studio Otter 3
> (2025.2.3)** o superior para sincronizar y ejecutar correctamente.

---

## Ejecutar

Usa las configuraciones del IDE o los siguientes comandos:

- **Android:** `./gradlew :androidApp:assembleDebug`
  (instalar y lanzar: `./gradlew :androidApp:installDebug`)
- **Desktop:**
  - Hot reload: `./gradlew :desktopApp:hotRun --auto`
  - Normal: `./gradlew :desktopApp:run`
- **Web:**
  - Wasm (más rápido, navegadores modernos): `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
  - JS: `./gradlew :webApp:jsBrowserDevelopmentRun`
- **iOS:** abre la carpeta [`iosApp`](./iosApp) en Xcode y ejecuta desde ahí.

> Tras cambiar cualquier archivo de Gradle (`libs.versions.toml`, `build.gradle.kts`)
> hay que hacer **File → Sync Project with Gradle Files** en Android Studio; hasta
> entonces el IDE mantiene el modelo viejo y deshabilita el botón **Run**.

---

## Tests

- Común (incluye `AppDestinationTest`, corre en todos los targets): forma parte de
  cada suite.
- Android host: `./gradlew :shared:testAndroidHostTest`
- Desktop: `./gradlew :shared:jvmTest`
- Web: `./gradlew :shared:wasmJsTest` / `./gradlew :shared:jsTest`
- iOS: `./gradlew :shared:iosSimulatorArm64Test`

---

## Documentación del código

Todo el código está documentado con **KDoc** (`/** ... */`), siguiendo estas
convenciones:

- **Clases/enums/composables**: un bloque KDoc que explica el *qué* y el *por qué*.
- **Parámetros**: anotados con `@param` y `@property` (para propiedades).
- **Miembros de enum y funciones privadas**: documentados cuando su intención no
  es evidente.
- **Comentarios en línea** para explicar decisiones de Compose (por qué
  `rememberSaveable`, qué hace cada opción de `navigate`, etc.).

La documentación de las dependencias y de la estructura está en este README, junto
al código que afectan.

---

## Notas

- **Rutas por `String`:** la navegación usa rutas textuales por simplicidad. Migrar
  a *type-safe routes* con `@Serializable` es opcional y requiere el plugin de
  serialización.
- **`material-icons-core` está congelado en `1.7.3`:** es esperado; solo aporta
  vectores y no bloquea futuras actualizaciones del resto del stack.
- **Repositorios:** se resuelven en `settings.gradle.kts` (`google` filtrado por
  grupos + `mavenCentral`), de modo que todas las coordenadas se declaran en el
  catálogo.

---

Aprende más sobre [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
y [Compose Multiplatform](https://kotlinlang.org/compose-multiplatform/).
