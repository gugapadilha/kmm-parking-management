package com.example.gestodeestacionamento

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.CanvasBasedWindow
import com.example.gestodeestacionamento.di.appModule
import com.example.gestodeestacionamento.platform.rememberNavController
import com.example.gestodeestacionamento.presentation.navigation.NavGraph
import com.example.gestodeestacionamento.presentation.navigation.Screen
import com.example.gestodeestacionamento.ui.theme.GestãoDeEstacionamentoTheme
import org.koin.core.context.startKoin

fun main() {
    // Inicializar Koin
    startKoin {
        modules(appModule)
    }
    
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        App()
    }
}

@Composable
fun App() {
    GestãoDeEstacionamentoTheme {
        val navController = rememberNavController()
        NavGraph(
            navController = navController,
            startDestination = Screen.Splash.route
        )
    }
}

