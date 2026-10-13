package org.prog2.navegacion

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

/**
 * Punto de entrada de la aplicación en Web (JavaScript y Wasm).
 *
 * [ComposeViewport] monta la UI de Compose sobre el `<canvas>`/DOM de la página
 * y renderiza el composable compartido `App().Content()`. El HTML que la hospeda
 * está en `webApp/src/webMain/resources/index.html`.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        App().Content()
    }
}
