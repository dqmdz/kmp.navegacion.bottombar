package org.prog2.navegacion.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Paletas de color de la aplicación.
 *
 * Se apoyan en los valores por defecto de Material 3 (`lightColorScheme` /
 * `darkColorScheme`) y solo sobrescriben los roles principales para dar identidad
 * visual, sin tener que definir cada rol manualmente.
 */

/**
 * Paleta clara. `primary` en azul, `secondary` en verde azulado y `tertiary`
 * en violeta; el resto de roles conservan los valores por defecto de Material 3.
 */
internal val LightColors = lightColorScheme(
    primary = Color(0xFF1565C0),   // Azul 800
    secondary = Color(0xFF00897B), // Verde azulado 600
    tertiary = Color(0xFF6A4C93),  // Violeta
)

/**
 * Paleta oscura. Usa tonos claros de los mismos acentos para mantener el
 * contraste sobre fondos oscuros.
 */
internal val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),   // Azul 200
    secondary = Color(0xFF80CBC4), // Verde azulado 200
    tertiary = Color(0xFFB39DDB),  // Violeta 200
)
