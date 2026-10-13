package org.prog2.navegacion.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Pruebas del catálogo de destinos ([AppDestination]).
 *
 * Son tests puros (sin UI) que se ejecutan en **todos** los targets y protegen
 * las invariantes de las que dependen la BottomBar y el `NavHost`: un número
 * fijo de pestañas, rutas únicas y una resolución correcta de rutas.
 */
class AppDestinationTest {

    /** La BottomBar y el `NavHost` asumen exactamente tres pantallas. */
    @Test
    fun tieneExactamenteTresDestinos() {
        assertEquals(3, AppDestination.entries.size)
    }

    /** Dos destinos con la misma ruta romperían la navegación; deben ser únicas. */
    @Test
    fun lasRutasSonUnicas() {
        val routes = AppDestination.entries.map { it.route }
        assertEquals(routes.size, routes.toSet().size)
    }

    /** Cada pestaña debe mostrar una etiqueta no vacía. */
    @Test
    fun lasEtiquetasNoEstanVacias() {
        assertTrue(AppDestination.entries.all { it.label.isNotBlank() })
    }

    /** [AppDestination.fromRoute] debe resolver todas las rutas declaradas. */
    @Test
    fun fromRouteResuelveLasRutasConocidas() {
        AppDestination.entries.forEach { destination ->
            assertEquals(destination, AppDestination.fromRoute(destination.route))
        }
    }

    /** Rutas inexistentes (o `null`) deben devolver `null`, sin lanzar excepción. */
    @Test
    fun fromRouteDevuelveNullParaRutasDesconocidas() {
        assertNull(AppDestination.fromRoute("ruta-inexistente"))
        assertNull(AppDestination.fromRoute(null))
    }
}
