package com.example.eventoscomunitarios.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eventoscomunitarios.ui.screens.*

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("event_list") { popUpTo("login") { inclusive = true } } },
                onGoToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("event_list") { popUpTo("register") { inclusive = true } } },
                onGoToLogin = { navController.navigate("login") }
            )
        }

        composable("event_list") {
            EventListScreen(onEventClick = { eventId -> navController.navigate("event_detail/$eventId") })
        }

        composable("event_detail/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(eventId = eventId)
        }

        composable("create_event") {
            CreateEventScreen(navBack = { navController.popBackStack() })
        }

        composable("profile") {
            ProfileScreen(onLogout = { navController.navigate("login") { popUpTo("event_list") { inclusive = true } } })
        }
    }
}
