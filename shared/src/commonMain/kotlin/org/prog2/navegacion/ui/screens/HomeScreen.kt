package org.prog2.navegacion.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bottombar.shared.generated.resources.Res
import bottombar.shared.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.prog2.navegacion.platform.getPlatform

/**
 * Pantalla de la pestaña "Inicio".
 *
 * Está modelada como una **clase** cuyo método composable [Content] dibuja la UI.
 * La instancia la crea [org.prog2.navegacion.navigation.MainScreen] y se conserva
 * con `remember`, por lo que no se recrea en cada recomposición.
 *
 * Demuestra dos ideas clave de Compose Multiplatform:
 *
 * - Lectura de información específica de la plataforma con [getPlatform], que es
 *   una declaración `expect`/`actual` resuelta en cada target.
 * - Estado local que sobrevive a recomposiciones y a cambios de configuración
 *   gracias a [rememberSaveable].
 */
class HomeScreen {

    /**
     * Dibuja el contenido de la pantalla.
     *
     * @param modifier modificador aplicado al contenedor raíz de la pantalla.
     */
    @Composable
    fun Content(modifier: Modifier = Modifier) {
        // Contador local. `mutableIntStateOf` evita el autoboxing de Int y
        // `rememberSaveable` conserva el valor (por ejemplo, al rotar en Android).
        var counter by rememberSaveable { mutableIntStateOf(0) }

        // Nombre de la plataforma. `remember` evita volver a consultarlo en cada
        // recomposición, ya que no cambia durante la vida de la pantalla.
        val platformName = remember { getPlatform().name }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Recurso compartido generado por Compose Resources (`composeResources/`).
            Image(
                painter = painterResource(Res.drawable.compose_multiplatform),
                contentDescription = "Logo de Compose Multiplatform",
                modifier = Modifier.size(96.dp),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Inicio",
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "App multiplataforma con navegación inferior entre tres pantallas.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(16.dp))
            // "Chip" tonal que muestra la plataforma detectada.
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    text = "Plataforma: $platformName",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = { counter++ }) {
                Text("Contador: $counter")
            }
        }
    }
}
