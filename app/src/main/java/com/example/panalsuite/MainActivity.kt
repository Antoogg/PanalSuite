package com.example.panalsuite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.panalsuite.ui.HomeScreen
import com.example.panalsuite.ui.LoginScreen
import com.example.panalsuite.ui.theme.PanalSuiteTheme
import com.example.panalsuite.viewmodel.TicketViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ticketViewModel = ViewModelProvider(this)[TicketViewModel::class.java]
        ticketViewModel.initializeDatabase(applicationContext)
        enableEdgeToEdge()
        setContent {
            PanalSuiteTheme {
                var loginExitoso by remember { mutableStateOf(false) }

                if (loginExitoso) {
                    HomeScreen(
                        viewModel = ticketViewModel,
                        onTicketClick = {}
                    )
                } else {
                    LoginScreen(
                        onLoginSuccess = { loginExitoso = true }
                    )
                }

            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PanalSuiteTheme {
        Greeting("Android")
    }
}