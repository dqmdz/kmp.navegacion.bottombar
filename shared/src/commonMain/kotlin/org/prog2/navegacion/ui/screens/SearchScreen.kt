package org.prog2.navegacion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Pantalla de la pestaña "Buscar".
 *
 * Está modelada como una **clase** cuyo método composable [Content] dibuja la UI.
 * La instancia la crea [org.prog2.navegacion.navigation.MainScreen] y se conserva
 * con `remember`.
 *
 * Demuestra tres patrones habituales de Compose:
 *
 * - Estado local de un campo de texto que sobrevive a cambios de configuración
 *   ([rememberSaveable]).
 * - Cálculo derivado y memoizado con [remember], recalculado solo cuando cambia
 *   la consulta: el filtrado de [topics].
 * - Renderizado eficiente de listas con `LazyColumn`.
 */
class SearchScreen {

    /**
     * Datos de ejemplo (inmutables) sobre los que trabaja el filtrado.
     * Al vivir dentro de la clase, la pantalla queda autocontenida.
     */
    private val topics = listOf(
        "Kotlin",
        "Compose Multiplatform",
        "Corrutinas",
        "Navegación",
        "Material 3",
        "Android",
        "iOS",
        "Web (JS + Wasm)",
        "Desktop (JVM)",
    )

    /**
     * Dibuja el contenido de la pantalla.
     *
     * @param modifier modificador aplicado al contenedor raíz de la pantalla.
     */
    @Composable
    fun Content(modifier: Modifier = Modifier) {
        // Texto escrito por el usuario. Se conserva entre recomposiciones/cambios de config.
        var query by rememberSaveable { mutableStateOf("") }

        // Resultado del filtrado. Se recalcula únicamente cuando `query` cambia,
        // ignorando mayúsculas y espacios sobrantes al inicio/final.
        val results = remember(query) {
            topics.filter { it.contains(query.trim(), ignoreCase = true) }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Text(
                text = "Buscar",
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Filtrar temas") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))

            if (results.isEmpty()) {
                // Estado vacío explícito para dar retroalimentación al usuario.
                Text(
                    text = "Sin resultados para \"$query\".",
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                // Lista perezosa: solo compone los elementos visibles en pantalla.
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(results) { topic ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = topic,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}
