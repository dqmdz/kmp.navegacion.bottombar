package org.prog2.navegacion.platform

/**
 * Implementación de [Platform] para el target WebAssembly (Kotlin/Wasm).
 *
 * No hay una API de navegador sencilla para deducir el entorno, así que devuelve
 * un valor fijo que identifica el target.
 */
class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

/** `actual` de [getPlatform] para el target WasmJs. */
actual fun getPlatform(): Platform = WasmPlatform()
