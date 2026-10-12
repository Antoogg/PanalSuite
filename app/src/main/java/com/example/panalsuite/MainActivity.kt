package com.example.panalsuite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.panalsuite.ui.navigation.AppNavHost
import com.example.panalsuite.ui.theme.PanalSuiteTheme
import com.example.panalsuite.viewmodel.TicketViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ticketViewModel =
            ViewModelProvider(this)[TicketViewModel::class.java]

        ticketViewModel.initializeDatabase(applicationContext)

        enableEdgeToEdge()

        setContent {
            PanalSuiteTheme {
                AppNavHost(
                    ticketViewModel = ticketViewModel
                )
            }
        }
    }
}