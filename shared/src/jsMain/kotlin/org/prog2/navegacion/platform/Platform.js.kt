package org.prog2.navegacion.platform

import web.navigator.navigator

/**
 * Implementación de [Platform] para el target JavaScript (navegador).
 *
 * Deduce el navegador a partir del `userAgent`; si no reconoce ninguno de la
 * lista conocida, devuelve `"Unknown"`.
 */
class JsPlatform : Platform {
    private val userAgent = navigator.userAgent
    private val browserList = listOf("Chrome", "Firefox", "Safari", "Edge")

    override val name: String = userAgent.findAnyOf(browserList, ignoreCase = true)
        // Recorta desde el nombre del navegador hasta el siguiente espacio.
        ?.let { (startIndex) -> userAgent.substring(startIndex).substringBefore(" ") }
        ?: "Unknown"
}

/** `actual` de [getPlatform] para el target JavaScript. */
actual fun getPlatform(): Platform = JsPlatform()
