package org.prog2.navegacion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Punto único de verdad de las pestañas navegables desde la BottomBar.
 *
 * Cada entrada describe una pantalla con todo lo que la barra y el `NavHost`
 * necesitan conocer:
 *
 * - [route]: identificador interno que usa `navigation-compose`. **Debe ser único**.
 * - [label]: texto visible en la pestaña.
 * - [icon]: vector mostrado como icono de la pestaña.
 *
 * Añadir una pantalla nueva es tan simple como agregar una entrada aquí y su
 * `composable(...)` correspondiente en [MainScreen]. La BottomBar se genera
 * automáticamente a partir de `AppDestination.entries`, sin tocar ningún otro
 * archivo.
 *
 * @property route identificador de navegación. Debe ser único entre destinos.
 * @property label etiqueta visible de la pestaña.
 * @property icon vector mostrado como icono de la pestaña.
 */
enum class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    /** Pestaña y pantalla de Inicio (información de plataforma + contador). */
    Home(route = "home", label = "Inicio", icon = Icons.Filled.Home),

    /** Pestaña y pantalla de Buscar (filtrado en vivo de una lista de ejemplo). */
    Search(route = "search", label = "Buscar", icon = Icons.Filled.Search),

    /** Pestaña y pantalla de Ajustes (tema y preferencias). */
    Settings(route = "settings", label = "Ajustes", icon = Icons.Filled.Settings),
    ;

    companion object {
        /**
         * Busca el destino cuya [route] coincide con [route].
         *
         * Es útil para obtener el icono/etiqueta de la pestaña activa a partir de
         * la ruta que expone el `NavController`.
         *
         * @param route ruta a resolver; puede ser `null` (por ejemplo, mientras el
         *   `NavHost` todavía no ha expuesto el primer *back stack entry*).
         * @return el [AppDestination] correspondiente, o `null` si no existe.
         */
        fun fromRoute(route: String?): AppDestination? =
            entries.firstOrNull { it.route == route }
    }
}
