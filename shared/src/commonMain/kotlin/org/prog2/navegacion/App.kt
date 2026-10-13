package org.prog2.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import org.prog2.navegacion.navigation.MainScreen
import org.prog2.navegacion.ui.theme.AppTheme

/**
 * Punto de entrada **común** de la aplicación para todas las plataformas
 * (Android, iOS, Desktop y Web), modelado como una **clase** con un método
 * composable [Content].
 *
 * Concentra el único estado global de la app —el modo claro/oscuro— y compone las
 * dos piezas principales de la interfaz:
 *
 * 1. [AppTheme]: aplica la paleta de Material 3 según `darkTheme`.
 * 2. [MainScreen]: dibuja la `Scaffold`, la BottomBar y el `NavHost`.
 *
 * El estado del tema se guarda con [rememberSaveable] para sobrevivir a cambios de
 * configuración (por ejemplo, una rotación en Android).
 *
 * Cada plataforma llama a `App().Content()` desde su propio punto de entrada:
 * `MainActivity` (Android), `main` (Desktop), `main` (Web) y `MainViewController` (iOS).
 */
class App {

    /**
     * Dibuja la aplicación completa. No recibe parámetros: es la raíz de la UI.
     */
    @Composable
    fun Content() {
        // Estado global del tema. Se inicia en modo claro y se eleva hasta Ajustes
        // para que toda la app reaccione al cambio.
        var darkTheme by rememberSaveable { mutableStateOf(false) }

        // Instancias creadas una sola vez por composición.
        val appTheme = remember { AppTheme() }
        val mainScreen = remember { MainScreen() }

        appTheme.Content(darkTheme = darkTheme) {
            mainScreen.Content(
                darkTheme = darkTheme,
                // Callback que la pantalla de Ajustes usa para alternar el modo oscuro.
                onDarkThemeChange = { darkTheme = it },
            )
        }
    }
}

/**
 * Previsualización de la app para la vista *Design* / *Preview* del IDE.
 * No se ejecuta en el dispositivo; solo renderiza [App.Content].
 */
@Preview
@Composable
fun AppPreview() {
    App().Content()
}
