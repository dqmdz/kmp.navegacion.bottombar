package org.prog2.navegacion.platform

import android.os.Build

/**
 * Implementación de [Platform] para Android.
 *
 * Incluye la versión del SDK (`Build.VERSION.SDK_INT`), por ejemplo `"Android 35"`.
 */
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

/** `actual` de [getPlatform] para el target Android. */
actual fun getPlatform(): Platform = AndroidPlatform()
