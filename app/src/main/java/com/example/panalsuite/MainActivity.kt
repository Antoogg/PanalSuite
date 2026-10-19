
package com.example.panalsuite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.example.panalsuite.ui.navigation.AppNavHost
import com.example.panalsuite.viewmodel.TicketViewModel

class MainActivity : ComponentActivity() {

    private val ticketViewModel: TicketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ticketViewModel.testApiConnection()

        setContent {
            MaterialTheme {
                AppNavHost(ticketViewModel = ticketViewModel)
            }
        }
    }
}
