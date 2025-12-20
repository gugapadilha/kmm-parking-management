package com.example.gestodeestacionamento

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.gestodeestacionamento.di.appModule
import com.example.gestodeestacionamento.presentation.navigation.NavGraph
import com.example.gestodeestacionamento.presentation.navigation.Screen
import com.example.gestodeestacionamento.ui.theme.GestãoDeEstacionamentoTheme
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.context.GlobalContext.startKoin

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar Koin se ainda não foi inicializado
        if (org.koin.core.context.GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@MainActivity)
                modules(appModule)
            }
        }

        enableEdgeToEdge()
        setContent {
            KoinAndroidContext {
                GestãoDeEstacionamentoTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        NavGraph(
                            navController = navController,
                            startDestination = Screen.Splash.route
                        )
                    }
                }
            }
        }
    }
}
