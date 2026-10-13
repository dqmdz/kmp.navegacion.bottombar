package org.prog2.navegacion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Tema central de la aplicación, modelado como una **clase** con un método
 * composable [Content].
 *
 * Envuelve [MaterialTheme] para que todas las pantallas compartan la misma paleta
 * y tipografía. Es el único lugar que decide entre [LightColors] y [DarkColors].
 *
 * La instancia la crea [org.prog2.navegacion.App] y se conserva con `remember`.
 */
class AppTheme {

    /**
     * Aplica el tema a [content].
     *
     * @param darkTheme indica si se usa la variante oscura. Por defecto sigue la
     *   preferencia del sistema ([isSystemInDarkTheme]); el usuario puede forzarla
     *   desde la pantalla de Ajustes.
     * @param content contenido al que se aplica el tema.
     */
    @Composable
    fun Content(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            content = content,
        )
    }
}
