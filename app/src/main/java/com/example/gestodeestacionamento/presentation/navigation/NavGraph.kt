package com.example.gestodeestacionamento.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gestodeestacionamento.presentation.screen.*
import com.example.gestodeestacionamento.presentation.viewmodel.*
import org.koin.androidx.compose.koinViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object VehicleEntry : Screen("vehicle_entry")
    object VehicleList : Screen("vehicle_list")
    object VehicleDetail : Screen("vehicle_detail/{vehicleId}") {
        fun createRoute(vehicleId: Long) = "vehicle_detail/$vehicleId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateNext = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = koinViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    // A sincronização será feita automaticamente pelo HomeViewModel
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = koinViewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToVehicleEntry = {
                    navController.navigate(Screen.VehicleEntry.route)
                },
                onNavigateToVehicleList = {
                    navController.navigate(Screen.VehicleList.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.VehicleEntry.route) {
            val viewModel: VehicleEntryViewModel = koinViewModel()
            VehicleEntryScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEntrySuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.VehicleList.route) {
            val viewModel: VehicleListViewModel = koinViewModel()
            VehicleListScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVehicleClick = { vehicleId ->
                    navController.navigate(Screen.VehicleDetail.createRoute(vehicleId))
                }
            )
        }

        composable(Screen.VehicleDetail.route) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId")?.toLongOrNull() ?: 0L
            val viewModel: VehicleDetailViewModel = koinViewModel()
            VehicleDetailScreen(
                viewModel = viewModel,
                vehicleId = vehicleId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onExitSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}

