package com.example.panalsuite.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.panalsuite.data.local.DatabaseProvider
import com.example.panalsuite.ui.CreateTicketScreen
import com.example.panalsuite.ui.EvidenceScreen
import com.example.panalsuite.ui.HomeScreen
import com.example.panalsuite.ui.LoginScreen
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
        startDestination = Routes.LOGIN
    ) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                viewModel = ticketViewModel,
                onTicketClick = { ticketId ->
                    navController.navigate(Routes.ticketDetail(ticketId))
                },
                onCreateTicketClick = {
                    navController.navigate(Routes.CREATE_TICKET)
                }
            )
        }

        composable(Routes.CREATE_TICKET) {
            CreateTicketScreen(
                viewModel = ticketViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.TICKET_DETAIL,
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
                    navController.navigate(Routes.evidence(ticketId))
                }
            )
        }

        composable(
            route = Routes.EVIDENCE,
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