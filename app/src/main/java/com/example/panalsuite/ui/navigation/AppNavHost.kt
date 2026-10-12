package com.example.panalsuite.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.panalsuite.data.local.DatabaseProvider
import com.example.panalsuite.ui.CreateTicketScreen
import com.example.panalsuite.ui.EvidenceScreen
import com.example.panalsuite.ui.LoginScreen
import com.example.panalsuite.ui.HomeScreen
import com.example.panalsuite.ui.TicketDetailScreen
import com.example.panalsuite.viewmodel.TicketViewModel

@Composable
fun AppNavHost(
    ticketViewModel: TicketViewModel
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                viewModel = ticketViewModel,
                onTicketClick = { ticketId ->
                    navController.navigate("ticket_detail/$ticketId")
                },
                onCreateTicketClick = {
                    navController.navigate("create_ticket")
                }
            )
        }

        composable("create_ticket") {
            CreateTicketScreen(
                dao = DatabaseProvider
                    .getDatabase(context)
                    .ticketDao(),
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "ticket_detail/{ticketId}",
            arguments = listOf(
                navArgument("ticketId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val ticketId = backStackEntry.arguments
                ?.getString("ticketId")
                .orEmpty()

            TicketDetailScreen(
                ticketId = ticketId,
                viewModel = ticketViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onAddEvidence = {
                    navController.navigate("evidence/$ticketId")
                }

            )

        }
        composable(
            route = "evidence/{ticketId}",
            arguments = listOf(
                navArgument("ticketId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val ticketId = backStackEntry.arguments
                ?.getString("ticketId")
                .orEmpty()

            EvidenceScreen(
                ticketId = ticketId,
                evidenceDao = DatabaseProvider
                    .getDatabase(context)
                    .evidenceDao(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}