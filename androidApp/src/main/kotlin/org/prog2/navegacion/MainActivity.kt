package org.prog2.navegacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

/**
 * Punto de entrada de la aplicación en Android.
 *
 * Es una `ComponentActivity` mínima: prepara el modo *edge-to-edge* y delega toda
 * la interfaz en la clase [App] (compartida con el resto de plataformas). Mantener
 * esta clase delgada garantiza que Android no tenga lógica de UI propia.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Permite que el contenido dibuje detrás de las barras del sistema;
        // el `Scaffold` de MainScreen se encarga de los insets.
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App().Content()
        }
    }
}

/**
 * Previsualización de la app para la vista *Design* / *Preview* de Android Studio.
 * No se ejecuta en el dispositivo; solo renderiza [App.Content].
 */
@Preview
@Composable
fun AppAndroidPreview() {
    App().Content()
}
