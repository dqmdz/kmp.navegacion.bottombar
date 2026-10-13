package org.prog2.navegacion

import androidx.compose.ui.window.ComposeUIViewController

/**
 * Punto de entrada de la aplicación en iOS.
 *
 * Expone un `UIViewController` de UIKit que aloja el composable compartido
 * `App().Content()`. Desde Swift (`ContentView.swift`) se envuelve en un
 * `UIViewControllerRepresentable` para usarlo dentro de SwiftUI. El framework se
 * exporta como `Shared`.
 *
 * Se mantiene como **función** (no como clase) porque es una fábrica que Swift
 * invoca por su nombre (`MainViewControllerKt.MainViewController()`).
 */
fun MainViewController() = ComposeUIViewController { App().Content() }
