package org.prog2.navegacion.platform

/**
 * Información básica de la plataforma donde se ejecuta la aplicación.
 *
 * Es el contrato del patrón `expect`/`actual`: se declara aquí, en `commonMain`,
 * y cada target (`androidMain`, `jvmMain`, `iosMain`, `jsMain`, `wasmJsMain`)
 * aporta su propia implementación. El código común consume el resultado sin
 * saber en qué plataforma corre.
 */
interface Platform {
    /** Nombre legible de la plataforma (por ejemplo, `"Android 35"` o `"iOS 18.0"`). */
    val name: String
}

/**
 * Devuelve la implementación de [Platform] correspondiente a la plataforma actual.
 *
 * Cada target debe proporcionar su `actual fun getPlatform(): Platform`.
 */
expect fun getPlatform(): Platform
