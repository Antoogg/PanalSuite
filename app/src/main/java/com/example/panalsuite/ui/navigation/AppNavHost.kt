package com.example.panalsuite.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
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
import com.example.panalsuite.viewmodel.TicketUiState
import com.example.panalsuite.viewmodel.TicketViewModel

@Composable
fun AppNavHost(
    ticketViewModel: TicketViewModel
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        enterTransition = { androidx.compose.animation.EnterTransition.None },
        exitTransition = { androidx.compose.animation.ExitTransition.None },
        popEnterTransition = { androidx.compose.animation.EnterTransition.None },
        popExitTransition = { androidx.compose.animation.ExitTransition.None }
    ) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { user ->
                    ticketViewModel.setCurrentUser(user)

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
                },
                onLogoutClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                    ticketViewModel.logout()





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

            val uiState by ticketViewModel.uiState.collectAsState()

            val ticket = (uiState as? TicketUiState.Success)
                ?.tickets
                ?.find { it.id == ticketId }

            if (ticket != null) {
                TicketDetailScreen(
                    ticket = ticket,
                    viewModel = ticketViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onAddEvidenceClick = {
                        navController.navigate(Routes.evidence(ticketId))
                    }
                )
            } else {
                Text("Solicitud no encontrada")
            }
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