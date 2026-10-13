package org.prog2.navegacion.platform

/**
 * Implementación de [Platform] para Desktop (JVM).
 *
 * Incluye la versión de Java en uso, por ejemplo `"Java 21.0.11"`.
 */
class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

/** `actual` de [getPlatform] para el target JVM (Desktop). */
actual fun getPlatform(): Platform = JVMPlatform()
