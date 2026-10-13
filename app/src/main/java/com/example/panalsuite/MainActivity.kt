package com.example.panalsuite

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.ui.CreateTicketScreen
import com.example.panalsuite.ui.HomeScreen
import com.example.panalsuite.ui.TicketDetailScreen
import com.example.panalsuite.viewmodel.TicketUiState
import com.example.panalsuite.viewmodel.TicketViewModel

class MainActivity : ComponentActivity() {

    private val ticketViewModel: TicketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainApp(viewModel = ticketViewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: TicketViewModel) {
    val navController = rememberNavController()
    val currentUser by viewModel.currentUser.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { }
        )
        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Probador de Roles (Rol activo: ${currentUser.role.name})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserRole.entries.forEach { role ->
                        FilterChip(
                            selected = currentUser.role == role,
                            onClick = { viewModel.setCurrentUserRole(role) },
                            label = { Text(role.name) }
                        )
                    }
                }
            }
        }

        // Navegación entre pantallas
        Box(modifier = Modifier.weight(1f)) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                // Pantalla principal de la bandeja de solicitudes
                composable("home") {
                    HomeScreen(
                        viewModel = viewModel,
                        onTicketClick = { ticketId ->
                            navController.navigate("detail/$ticketId")
                        },
                        onCreateTicketClick = {
                            navController.navigate("create")
                        }
                    )
                }

                composable("create") {
                    CreateTicketScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = "detail/{ticketId}",
                    arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val ticketId = backStackEntry.arguments?.getString("ticketId") ?: ""
                    val tickets = (uiState as? TicketUiState.Success)?.tickets ?: emptyList()
                    val ticket = tickets.find { it.id == ticketId }

                    if (ticket != null) {
                        TicketDetailScreen(
                            ticket = ticket,
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onAddEvidenceClick = { }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Solicitud no encontrada")
                        }
                    }
                }
            }
        }
    }
}