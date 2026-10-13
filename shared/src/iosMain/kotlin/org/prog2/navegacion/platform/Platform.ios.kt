package org.prog2.navegacion.platform

import platform.UIKit.UIDevice

/**
 * Implementación de [Platform] para iOS/iPadOS.
 *
 * Combina el nombre del sistema con su versión, por ejemplo `"iOS 18.0"`.
 */
class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

/** `actual` de [getPlatform] para los targets de iOS. */
actual fun getPlatform(): Platform = IOSPlatform()
