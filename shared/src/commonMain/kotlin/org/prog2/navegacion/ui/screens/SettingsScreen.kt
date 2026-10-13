package org.prog2.navegacion.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Pantalla de la pestaña "Ajustes".
 *
 * Está modelada como una **clase** cuyo método composable [Content] dibuja la UI.
 * La instancia la crea [org.prog2.navegacion.navigation.MainScreen] y se conserva
 * con `remember`; los valores que pueden cambiar (tema) se reciben como parámetros
 * de [Content], no del constructor, para que la instancia siga siendo estable.
 *
 * Ilustra dos formas de manejar estado:
 *
 * - **Estado elevado**: el interruptor de "Modo oscuro" no guarda su propio valor;
 *   recibe [darkTheme] y notifica cambios con [onDarkThemeChange]. El estado real
 *   vive en `App`, de modo que el tema de toda la app reacciona al cambio.
 * - **Estado local**: "Notificaciones" solo existe dentro de esta pantalla, por lo
 *   que se guarda aquí con [rememberSaveable].
 */
class SettingsScreen {

    /**
     * Dibuja el contenido de la pantalla.
     *
     * @param darkTheme estado actual del modo oscuro (controlado desde arriba).
     * @param onDarkThemeChange callback que solicita cambiar el modo oscuro.
     * @param modifier modificador aplicado al contenedor raíz de la pantalla.
     */
    @Composable
    fun Content(
        darkTheme: Boolean,
        onDarkThemeChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        // Preferencia puramente local de esta pantalla; no afecta a la app.
        var notificationsEnabled by rememberSaveable { mutableStateOf(true) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.height(16.dp))

            // Estado elevado: cambiarlo altera el tema global de la aplicación.
            ToggleRow(
                title = "Modo oscuro",
                checked = darkTheme,
                onCheckedChange = onDarkThemeChange,
            )
            HorizontalDivider()
            // Estado local: solo se conserva mientras vive esta pantalla.
            ToggleRow(
                title = "Notificaciones",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it },
            )
            HorizontalDivider()

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Acerca de",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "App de ejemplo con BottomBar y navegación entre tres pantallas, " +
                    "compartida para Android, iOS, Web y Desktop.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }

    /**
     * Fila reutilizable interna de esta pantalla: etiqueta a la izquierda (ocupa
     * el espacio disponible) y un [Switch] a la derecha.
     *
     * @param title texto descriptivo de la preferencia.
     * @param checked estado actual del interruptor.
     * @param onCheckedChange callback invocado con el nuevo valor al alternar.
     * @param modifier modificador opcional aplicado a la fila.
     */
    @Composable
    private fun ToggleRow(
        title: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                // `weight(1f)` empuja el Switch al extremo derecho.
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}
