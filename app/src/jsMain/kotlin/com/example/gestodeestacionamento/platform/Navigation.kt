package com.example.gestodeestacionamento.platform

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
actual fun rememberNavController(): NavHostController = rememberNavController()

