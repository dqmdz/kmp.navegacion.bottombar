package org.prog2.navegacion.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.prog2.navegacion.ui.components.AppBottomBar
import org.prog2.navegacion.ui.screens.HomeScreen
import org.prog2.navegacion.ui.screens.SearchScreen
import org.prog2.navegacion.ui.screens.SettingsScreen

/**
 * Contenedor principal de la aplicación, modelado como una **clase** con un método
 * composable [Content]. Es el **único** punto donde se conoce el `NavController`;
 * aquí se unen la [AppBottomBar] y el `NavHost`.
 *
 * La composición es:
 *
 * 1. `Scaffold` reserva el espacio de la barra inferior y aplica los insets.
 * 2. [AppBottomBar] recibe los destinos, la ruta activa y el callback de selección.
 * 3. `NavHost` resuelve cada [AppDestination] a su pantalla.
 *
 * Las pantallas ([HomeScreen], [SearchScreen], [SettingsScreen]) son clases cuyas
 * instancias se crean aquí una sola vez con `remember`. Ninguna pantalla recibe el
 * `NavController`: solo datos y callbacks, de modo que la navegación queda
 * centralizada y las pantallas siguen siendo reutilizables y previsualizables.
 *
 * La instancia la crea [org.prog2.navegacion.App] con `remember`.
 */
class MainScreen {

    /**
     * Dibuja el contenedor principal (BottomBar + contenido navegable).
     *
     * @param darkTheme estado actual del modo oscuro; se reenvía a la pantalla de Ajustes.
     * @param onDarkThemeChange callback para cambiar el modo oscuro desde Ajustes.
     * @param modifier modificador opcional para el contenedor raíz.
     */
    @Composable
    fun Content(
        darkTheme: Boolean,
        onDarkThemeChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        // Controlador de navegación que gestiona el back stack entre pestañas.
        val navController = rememberNavController()

        // Observa el back stack para conocer la ruta activa y poder resaltar la pestaña.
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        // Instancias creadas una sola vez por composición y reutilizadas en cada
        // recomposición (no se recrean los objetos en cada pasada).
        val bottomBar = remember { AppBottomBar() }
        val homeScreen = remember { HomeScreen() }
        val searchScreen = remember { SearchScreen() }
        val settingsScreen = remember { SettingsScreen() }

        Scaffold(
            modifier = modifier,
            bottomBar = {
                bottomBar.Content(
                    // `entries` devuelve todas las constantes del enum en orden de declaración.
                    destinations = AppDestination.entries,
                    currentRoute = currentRoute,
                    onSelect = { destination -> navigateToTab(navController, destination) },
                )
            },
        ) { innerPadding ->
            // El padding interno evita que el contenido quede debajo de la BottomBar
            // o de las barras del sistema (estado/navegación).
            NavHost(
                navController = navController,
                // Primera pantalla mostrada al abrir la app.
                startDestination = AppDestination.Home.route,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(AppDestination.Home.route) {
                    homeScreen.Content()
                }
                composable(AppDestination.Search.route) {
                    searchScreen.Content()
                }
                composable(AppDestination.Settings.route) {
                    // Ajustes necesita conocer y poder modificar el tema global.
                    settingsScreen.Content(
                        darkTheme = darkTheme,
                        onDarkThemeChange = onDarkThemeChange,
                    )
                }
            }
        }
    }

    /**
     * Navega a una pestaña aplicando el comportamiento **estándar de una BottomBar**.
     *
     * Opciones usadas:
     *
     * - `popUpTo(graph.startDestinationId)`: deja la pila en un solo nivel, de modo
     *   que pulsar atrás sale de la app en lugar de recorrer las pestañas visitadas.
     * - `launchSingleTop = true`: evita crear copias del mismo destino si el usuario
     *   pulsa dos veces la misma pestaña.
     * - `saveState` / `restoreState`: conservan el estado de cada pestaña (scroll,
     *   texto escrito, etc.) al alternar entre ellas.
     *
     * @param navController controlador de navegación sobre el que se actúa.
     * @param destination pestaña a la que se desea navegar.
     */
    private fun navigateToTab(
        navController: NavHostController,
        destination: AppDestination,
    ) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}
