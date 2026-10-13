package org.prog2.navegacion

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

/**
 * Punto de entrada de la aplicación en Desktop (JVM).
 *
 * Abre una ventana nativa con Compose Desktop y monta dentro el composable
 * compartido `App().Content()`. Al cerrar la ventana se termina la aplicación.
 */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "bottombar",
    ) {
        App().Content()
    }
}
