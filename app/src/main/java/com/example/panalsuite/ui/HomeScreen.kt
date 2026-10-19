
package com.example.panalsuite.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.viewmodel.TicketUiState
import com.example.panalsuite.viewmodel.TicketViewModel

private val PanalPurple = Color(0xFF6650A4)
private val PanalBackground = Color(0xFFF0EDFF)
private val PanalCard = Color(0xFFF8F6FF)

@Composable
fun HomeScreen(
    viewModel: TicketViewModel,
    onTicketClick: (String) -> Unit,
    onCreateTicketClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.initializeDatabase(context)
    }

    HomeScreenContent(
        uiState = uiState,
        canCreateTicket = viewModel.canCreateTicket(),
        onTicketClick = onTicketClick,
        onCreateTicketClick = onCreateTicketClick,
        onLogoutClick = onLogoutClick,
        currentUserRole = currentUser.role,
        currentUserName = currentUser.name,
        currentUserEmail = currentUser.email
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: TicketUiState,
    canCreateTicket: Boolean,
    onTicketClick: (String) -> Unit,
    onCreateTicketClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    currentUserRole: UserRole = UserRole.APPLICANT,
    currentUserName: String = "",
    currentUserEmail: String = ""
) {
    val isApplicant = currentUserRole == UserRole.APPLICANT
    val isResolver = currentUserRole == UserRole.RESOLVER
    val isManagement = currentUserRole == UserRole.MANAGEMENT

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFilter by remember { mutableStateOf("Todas") }
    var searchQuery by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }

    val allTickets = (uiState as? TicketUiState.Success)?.tickets.orEmpty()

    val visibleTickets = if (isApplicant) {
        allTickets.filter { ticket ->
            currentUserEmail.isNotBlank() &&
                    ticket.applicantEmail.trim().equals(
                        currentUserEmail.trim(),
                        ignoreCase = true
                    )
        }
    } else {
        allTickets
    }

    val filters = if (isResolver) {
        listOf(
            "Asignadas a mí",
            "Todas",
            "Pendientes",
            "En Proceso",
            "Resueltas"
        )
    } else {
        listOf(
            "Todas",
            "Pendientes",
            "En Proceso",
            "Resueltas"
        )
    }

    fun openHome() {
        selectedTab = 0
        selectedFilter = "Todas"
        searchQuery = ""
        menuExpanded = false
    }

    fun openTickets(filter: String = "Todas") {
        selectedTab = 1
        selectedFilter = filter
        searchQuery = ""
        menuExpanded = false
    }

    Scaffold(
        containerColor = PanalBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PanalSuite",
                        color = PanalPurple,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    Box {
                        Text(
                            text = "☰",
                            modifier = Modifier
                                .clickable { menuExpanded = true }
                                .padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (isManagement) {
                                            "Panel gerencial"
                                        } else {
                                            "Inicio"
                                        }
                                    )
                                },
                                onClick = { openHome() }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when {
                                            isManagement -> "Todas las solicitudes"
                                            isResolver -> "Mi bandeja"
                                            isApplicant -> "Mis solicitudes"
                                            else -> "Todas las solicitudes"
                                        }
                                    )
                                },
                                onClick = {
                                    openTickets(
                                        if (isResolver) "Asignadas a mí"
                                        else "Todas"
                                    )
                                }
                            )

                            HorizontalDivider()

                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                onClick = {
                                    menuExpanded = false
                                    onLogoutClick()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PanalBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = PanalCard) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { openHome() },
                    icon = { Text("⌂") },
                    label = {
                        Text(
                            if (isManagement) "Panel"
                            else "Inicio"
                        )
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        openTickets(
                            if (isResolver) "Asignadas a mí"
                            else "Todas"
                        )
                    },
                    icon = { Text("☷") },
                    label = {
                        Text(
                            when {
                                isManagement -> "Solicitudes"
                                isResolver -> "Mi bandeja"
                                isApplicant -> "Mis solicitudes"
                                else -> "Solicitudes"
                            }
                        )
                    }
                )
            }
        },
        floatingActionButton = {
            if (canCreateTicket) {
                FloatingActionButton(
                    onClick = onCreateTicketClick,
                    containerColor = PanalPurple,
                    contentColor = Color.White
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (selectedTab == 0) {
                if (isManagement) {
                    ManagementDashboard(
                        tickets = allTickets,
                        onViewAll = { openTickets("Todas") },
                        onViewPending = { openTickets("Pendientes") }
                    )
                } else {
                    InicioSolicitante(
                        uiState = uiState,
                        canCreateTicket = canCreateTicket,
                        onNuevaSolicitud = onCreateTicketClick,
                        onMisSolicitudes = {
                            openTickets(
                                if (isResolver) "Asignadas a mí"
                                else "Todas"
                            )
                        },
                        onVerPendientes = {
                            openTickets("Pendientes")
                        },
                        currentUserRole = currentUserRole,
                        currentUserName = currentUserName,
                        currentUserEmail = currentUserEmail
                    )
                }
            } else {
                Text(
                    text = when {
                        isManagement -> "Todas las solicitudes"
                        isResolver -> "Bandeja de trabajo"
                        isApplicant -> "Mis solicitudes"
                        else -> "Todas las solicitudes"
                    },
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PanalPurple
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Buscar solicitudes...")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PanalCard,
                        unfocusedContainerColor = PanalCard,
                        focusedBorderColor = PanalPurple,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        val selected = selectedFilter == filter

                        Surface(
                            modifier = Modifier.clickable {
                                selectedFilter = filter
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = if (selected) PanalPurple else PanalCard
                        ) {
                            Text(
                                text = filter,
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 9.dp
                                ),
                                color = if (selected) Color.White else PanalPurple,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (uiState) {
                    is TicketUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is TicketUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    is TicketUiState.Success -> {
                        val filteredTickets = visibleTickets.filter { ticket ->

                            val matchesSearch =
                                ticket.id.contains(
                                    searchQuery,
                                    ignoreCase = true
                                ) ||
                                        ticket.category.contains(
                                            searchQuery,
                                            ignoreCase = true
                                        ) ||
                                        ticket.location.contains(
                                            searchQuery,
                                            ignoreCase = true
                                        ) ||
                                        ticket.description.contains(
                                            searchQuery,
                                            ignoreCase = true
                                        )

                            val matchesFilter = when (selectedFilter) {
                                "Pendientes" ->
                                    ticket.state.equals(
                                        "Pendiente",
                                        ignoreCase = true
                                    )

                                "En Proceso" ->
                                    ticket.state.equals(
                                        "En Proceso",
                                        ignoreCase = true
                                    )

                                "Resueltas" ->
                                    ticket.state.equals(
                                        "Resuelto",
                                        ignoreCase = true
                                    ) ||
                                            ticket.state.equals(
                                                "Resuelta",
                                                ignoreCase = true
                                            )

                                else -> true
                            }

                            val matchesAssignment =
                                selectedFilter != "Asignadas a mí" ||
                                        (
                                                isResolver &&
                                                        ticket.responsible.trim().equals(
                                                            currentUserName.trim(),
                                                            ignoreCase = true
                                                        )
                                                )

                            matchesSearch &&
                                    matchesFilter &&
                                    matchesAssignment
                        }

                        if (filteredTickets.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (
                                        selectedFilter == "Asignadas a mí"
                                    ) {
                                        "No tienes solicitudes asignadas."
                                    } else if (isApplicant) {
                                        "No tienes solicitudes para mostrar."
                                    } else {
                                        "No hay solicitudes para mostrar."
                                    }
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 90.dp
                                ),
                                verticalArrangement =
                                    Arrangement.spacedBy(10.dp)
                            ) {
                                items(
                                    items = filteredTickets,
                                    key = { it.id }
                                ) { ticket ->
                                    TicketItemCard(
                                        ticket = ticket,
                                        onClick = {
                                            onTicketClick(ticket.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ManagementDashboard(
    tickets: List<Ticket>,
    onViewAll: () -> Unit,
    onViewPending: () -> Unit
) {
    val total = tickets.size

    val pendientes = tickets.count {
        it.state.equals("Pendiente", ignoreCase = true)
    }

    val enProceso = tickets.count {
        it.state.equals("En Proceso", ignoreCase = true)
    }

    val resueltas = tickets.count {
        it.state.equals("Resuelto", ignoreCase = true) ||
                it.state.equals("Resuelta", ignoreCase = true)
    }

    val alta = tickets.count {
        it.priority.equals("Alta", ignoreCase = true)
    }

    val media = tickets.count {
        it.priority.equals("Media", ignoreCase = true)
    }

    val baja = tickets.count {
        it.priority.equals("Baja", ignoreCase = true)
    }

    val sinAsignar = tickets.count {
        it.responsible.isBlank() ||
                it.responsible.equals("Sin asignar", ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Panel gerencial",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PanalPurple
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Resumen general de las solicitudes registradas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            DashboardSectionTitle("Indicadores generales")
        }

        item {
            ResumenCard(
                titulo = "Total de solicitudes",
                cantidad = total,
                color = PanalPurple
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SmallMetricCard(
                        titulo = "Pendientes",
                        cantidad = pendientes,
                        color = Color(0xFF2457C5)
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    SmallMetricCard(
                        titulo = "En proceso",
                        cantidad = enProceso,
                        color = Color(0xFFF9A825)
                    )
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SmallMetricCard(
                        titulo = "Resueltas",
                        cantidad = resueltas,
                        color = Color(0xFF2E7D32)
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    SmallMetricCard(
                        titulo = "Sin asignar",
                        cantidad = sinAsignar,
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        }

        item {
            DashboardSectionTitle("Solicitudes por prioridad")
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PanalCard
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    PriorityRow(
                        titulo = "Alta",
                        cantidad = alta,
                        total = total,
                        color = Color(0xFFD32F2F)
                    )

                    PriorityRow(
                        titulo = "Media",
                        cantidad = media,
                        total = total,
                        color = Color(0xFFF9A825)
                    )

                    PriorityRow(
                        titulo = "Baja",
                        cantidad = baja,
                        total = total,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }

        item {
            DashboardSectionTitle("Consulta de solicitudes")
        }

        item {
            AccionInicioCard(
                titulo = "Ver todas las solicitudes",
                descripcion = "Consulta el detalle y estado de los tickets.",
                simbolo = "☷",
                onClick = onViewAll
            )
        }

        item {
            AccionInicioCard(
                titulo = "Ver solicitudes pendientes",
                descripcion = "Revisa las solicitudes que requieren atención.",
                simbolo = "◷",
                onClick = onViewPending
            )
        }
    }
}

@Composable
private fun DashboardSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = PanalPurple
    )
}

@Composable
private fun SmallMetricCard(
    titulo: String,
    cantidad: Int,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanalCard
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = cantidad.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun PriorityRow(
    titulo: String,
    cantidad: Int,
    total: Int,
    color: Color
) {
    val progress = if (total > 0) {
        cantidad.toFloat() / total.toFloat()
    } else {
        0f
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Prioridad $titulo",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = cantidad.toString(),
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = PanalBackground
        )
    }
}

@Composable
fun InicioSolicitante(
    uiState: TicketUiState,
    canCreateTicket: Boolean,
    onNuevaSolicitud: () -> Unit,
    onMisSolicitudes: () -> Unit,
    onVerPendientes: () -> Unit,
    currentUserRole: UserRole = UserRole.APPLICANT,
    currentUserName: String = "",
    currentUserEmail: String = ""
) {
    val tickets =
        (uiState as? TicketUiState.Success)?.tickets.orEmpty()

    val isResolver = currentUserRole == UserRole.RESOLVER
    val isApplicant = currentUserRole == UserRole.APPLICANT

    val summaryTickets = when {
        isApplicant -> tickets.filter { ticket ->
            currentUserEmail.isNotBlank() &&
                    ticket.applicantEmail.trim().equals(
                        currentUserEmail.trim(),
                        ignoreCase = true
                    )
        }

        isResolver -> tickets.filter { ticket ->
            ticket.responsible.trim().equals(
                currentUserName.trim(),
                ignoreCase = true
            )
        }

        else -> tickets
    }

    val pendientes = summaryTickets.count {
        it.state.equals("Pendiente", ignoreCase = true)
    }

    val enProceso = summaryTickets.count {
        it.state.equals("En Proceso", ignoreCase = true)
    }

    val resueltas = summaryTickets.count {
        it.state.equals("Resuelto", ignoreCase = true) ||
                it.state.equals("Resuelta", ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = if (isResolver) {
                    "¡Bienvenido a tu bandeja!"
                } else {
                    "¡Bienvenido a PanalSuite!"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PanalPurple
            )
        }

        item {
            Text(
                text = if (isResolver) {
                    "Consulta y gestiona tus tareas asignadas."
                } else {
                    "¿Qué deseas hacer hoy?"
                },
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (canCreateTicket) {
            item {
                AccionInicioCard(
                    titulo = "Nueva solicitud",
                    descripcion = "Reporta un problema o requerimiento.",
                    simbolo = "+",
                    onClick = onNuevaSolicitud
                )
            }
        }

        item {
            AccionInicioCard(
                titulo = if (isResolver) {
                    "Mis tareas asignadas"
                } else if (isApplicant) {
                    "Mis solicitudes"
                } else {
                    "Todas las solicitudes"
                },
                descripcion = if (isResolver) {
                    "Consulta los tickets que tienes asignados."
                } else if (isApplicant) {
                    "Consulta tus tickets y revisa su estado."
                } else {
                    "Consulta los tickets y revisa su estado."
                },
                simbolo = "☷",
                onClick = onMisSolicitudes
            )
        }

        item {
            AccionInicioCard(
                titulo = "Ver solicitudes pendientes",
                descripcion = "Revisa los tickets que aún están pendientes.",
                simbolo = "◷",
                onClick = onVerPendientes
            )
        }

        item {
            DashboardSectionTitle(
                when {
                    isResolver -> "Resumen de mis tareas"
                    isApplicant -> "Resumen de mis solicitudes"
                    else -> "Resumen de solicitudes"
                }
            )
        }

        item {
            ResumenCard(
                titulo = when {
                    isResolver -> "Total asignadas"
                    isApplicant -> "Total de mis solicitudes"
                    else -> "Total de solicitudes"
                },
                cantidad = summaryTickets.size,
                color = PanalPurple
            )
        }

        item {
            ResumenCard(
                titulo = "Pendientes",
                cantidad = pendientes,
                color = Color(0xFF2457C5)
            )
        }

        item {
            ResumenCard(
                titulo = "En proceso",
                cantidad = enProceso,
                color = Color(0xFFF9A825)
            )
        }

        item {
            ResumenCard(
                titulo = "Resueltas",
                cantidad = resueltas,
                color = Color(0xFF2E7D32)
            )
        }
    }
}

@Composable
private fun AccionInicioCard(
    titulo: String,
    descripcion: String,
    simbolo: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanalCard
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PanalPurple.copy(alpha = 0.12f)
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = simbolo,
                        color = PanalPurple,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "›",
                color = PanalPurple,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
private fun ResumenCard(
    titulo: String,
    cantidad: Int,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanalCard
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = cantidad.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun TicketItemCard(
    ticket: Ticket,
    onClick: () -> Unit
) {
    val priorityColor = when (ticket.priority.lowercase()) {
        "alta" -> Color(0xFFD32F2F)
        "media" -> Color(0xFFF9A825)
        "baja" -> Color(0xFF2E7D32)
        else -> PanalPurple
    }

    val statusColor = when (ticket.state.lowercase()) {
        "pendiente" -> Color(0xFF2457C5)
        "en proceso" -> Color(0xFF2457C5)
        "resuelto", "resuelta" -> Color(0xFF2E7D32)
        else -> PanalPurple
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanalCard
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(112.dp)
                    .background(priorityColor)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ticket.id,
                        modifier = Modifier.weight(1f, fill = false),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = priorityColor.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = ticket.priority,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                            color = priorityColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = ticket.category,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = ticket.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(7.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = ticket.state,
                            modifier = Modifier.padding(
                                horizontal = 7.dp,
                                vertical = 3.dp
                            ),
                            color = statusColor,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = ticket.creationDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "›",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 12.dp),
                style = MaterialTheme.typography.headlineMedium,
                color = PanalPurple
            )
        }
    }
}
