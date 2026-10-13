package org.prog2.navegacion.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.prog2.navegacion.navigation.AppDestination

/**
 * Barra de navegación inferior (Material 3 `NavigationBar`), modelada como una
 * **clase** con un método composable [Content].
 *
 * Es un componente **presentacional** (o "tonto"): no conoce el `NavController`
 * ni el `NavHost`; solo recibe datos y notifica la selección. Gracias a esa
 * separación se puede reutilizar, previsualizar y testear de forma aislada.
 *
 * La instancia la crea [org.prog2.navegacion.navigation.MainScreen] con `remember`.
 */
class AppBottomBar {

    /**
     * Dibuja la barra inferior.
     *
     * @param destinations pestañas a mostrar (normalmente `AppDestination.entries`).
     * @param currentRoute ruta activa; determina qué pestaña aparece seleccionada.
     * @param onSelect callback invocado al tocar una pestaña.
     * @param modifier modificador opcional para la barra completa.
     */
    @Composable
    fun Content(
        destinations: List<AppDestination>,
        currentRoute: String?,
        onSelect: (AppDestination) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        NavigationBar(modifier = modifier) {
            destinations.forEach { destination ->
                NavigationBarItem(
                    // Se resalta el ítem cuyo `route` coincide con la ruta activa.
                    selected = currentRoute == destination.route,
                    onClick = { onSelect(destination) },
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.label,
                        )
                    },
                    label = { Text(destination.label) },
                )
            }
        }
    }
}
