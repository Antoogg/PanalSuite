
package com.example.panalsuite.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.panalsuite.model.User
import com.example.panalsuite.viewmodel.LoginViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val LoginPurple = Color(0xFF6650A4)
private val LoginBackground = Color(0xFFF0EDFF)
private val LoginFieldBackground = Color(0xFFFAF9FF)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (User) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val loggedUser by viewModel.loggedUser.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(loggedUser) {
        loggedUser?.let { user ->
            onLoginSuccess(user)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginBackground)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        LogoMantenimiento()

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "PanalSuite",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = LoginPurple
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Iniciar sesión",
            fontSize = 23.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF292438)
        )

        Spacer(modifier = Modifier.height(42.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            placeholder = { Text("tu@correo.com") },
            leadingIcon = {
                IconoSobre()
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LoginPurple,
                unfocusedBorderColor = LoginPurple.copy(alpha = 0.65f),
                focusedLabelColor = LoginPurple,
                focusedContainerColor = LoginFieldBackground,
                unfocusedContainerColor = LoginFieldBackground
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = {
                IconoCandado()
            },
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            passwordVisible = !passwordVisible
                        },
                    contentAlignment = Alignment.Center
                ) {
                    IconoOjo(
                        visible = passwordVisible
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LoginPurple,
                unfocusedBorderColor = LoginPurple.copy(alpha = 0.65f),
                focusedLabelColor = LoginPurple,
                focusedContainerColor = LoginFieldBackground,
                unfocusedContainerColor = LoginFieldBackground
            )
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.login(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LoginPurple,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Ingresar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LogoMantenimiento() {
    Canvas(
        modifier = Modifier.size(124.dp)
    ) {
        val center = Offset(
            size.width / 2f,
            size.height / 2f
        )

        val outerRadius = size.minDimension * 0.47f
        val innerRadius = size.minDimension * 0.36f


        val gearPath = Path()

        for (i in 0 until 32) {
            val angle = (i * 2.0 * PI / 32.0) - PI / 2.0
            val radius = when (i % 4) {
                0, 3 -> outerRadius
                else -> innerRadius
            }

            val x = center.x + cos(angle).toFloat() * radius
            val y = center.y + sin(angle).toFloat() * radius

            if (i == 0) {
                gearPath.moveTo(x, y)
            } else {
                gearPath.lineTo(x, y)
            }
        }

        gearPath.close()

        drawPath(
            path = gearPath,
            color = LoginPurple
        )

        drawCircle(
            color = LoginBackground,
            radius = size.minDimension * 0.26f,
            center = center
        )


        val stroke = size.minDimension * 0.065f

        drawLine(
            color = LoginPurple,
            start = Offset(
                center.x - size.width * 0.15f,
                center.y - size.height * 0.15f
            ),
            end = Offset(
                center.x + size.width * 0.15f,
                center.y + size.height * 0.15f
            ),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = LoginPurple,
            start = Offset(
                center.x + size.width * 0.15f,
                center.y - size.height * 0.15f
            ),
            end = Offset(
                center.x - size.width * 0.15f,
                center.y + size.height * 0.15f
            ),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )


        drawCircle(
            color = LoginPurple,
            radius = size.minDimension * 0.075f,
            center = Offset(
                center.x - size.width * 0.15f,
                center.y - size.height * 0.15f
            ),
            style = Stroke(width = stroke * 0.65f)
        )
    }
}

@Composable
private fun IconoSobre() {
    Canvas(
        modifier = Modifier.size(24.dp)
    ) {
        val stroke = 2.dp.toPx()

        drawRoundRect(
            color = LoginPurple,
            topLeft = Offset(
                size.width * 0.08f,
                size.height * 0.20f
            ),
            size = Size(
                size.width * 0.84f,
                size.height * 0.60f
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                3.dp.toPx()
            ),
            style = Stroke(width = stroke)
        )

        drawLine(
            color = LoginPurple,
            start = Offset(
                size.width * 0.10f,
                size.height * 0.25f
            ),
            end = Offset(
                size.width * 0.50f,
                size.height * 0.53f
            ),
            strokeWidth = stroke
        )

        drawLine(
            color = LoginPurple,
            start = Offset(
                size.width * 0.90f,
                size.height * 0.25f
            ),
            end = Offset(
                size.width * 0.50f,
                size.height * 0.53f
            ),
            strokeWidth = stroke
        )
    }
}

@Composable
private fun IconoCandado() {
    Canvas(
        modifier = Modifier.size(24.dp)
    ) {
        val stroke = 2.dp.toPx()

        drawRoundRect(
            color = LoginPurple,
            topLeft = Offset(
                size.width * 0.18f,
                size.height * 0.43f
            ),
            size = Size(
                size.width * 0.64f,
                size.height * 0.47f
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                3.dp.toPx()
            ),
            style = Stroke(width = stroke)
        )

        val arco = Path().apply {
            moveTo(
                size.width * 0.31f,
                size.height * 0.43f
            )
            lineTo(
                size.width * 0.31f,
                size.height * 0.30f
            )
            cubicTo(
                size.width * 0.31f,
                size.height * 0.05f,
                size.width * 0.69f,
                size.height * 0.05f,
                size.width * 0.69f,
                size.height * 0.30f
            )
            lineTo(
                size.width * 0.69f,
                size.height * 0.43f
            )
        }

        drawPath(
            path = arco,
            color = LoginPurple,
            style = Stroke(
                width = stroke,
                cap = StrokeCap.Round
            )
        )

        drawCircle(
            color = LoginPurple,
            radius = size.minDimension * 0.045f,
            center = Offset(
                size.width * 0.50f,
                size.height * 0.65f
            )
        )
    }
}

@Composable
private fun IconoOjo(
    visible: Boolean
) {
    Canvas(
        modifier = Modifier.size(26.dp)
    ) {
        val stroke = 2.dp.toPx()

        val ojo = Path().apply {
            moveTo(
                size.width * 0.08f,
                size.height * 0.50f
            )

            cubicTo(
                size.width * 0.28f,
                size.height * 0.15f,
                size.width * 0.72f,
                size.height * 0.15f,
                size.width * 0.92f,
                size.height * 0.50f
            )

            cubicTo(
                size.width * 0.72f,
                size.height * 0.85f,
                size.width * 0.28f,
                size.height * 0.85f,
                size.width * 0.08f,
                size.height * 0.50f
            )

            close()
        }

        drawPath(
            path = ojo,
            color = LoginPurple,
            style = Stroke(
                width = stroke,
                cap = StrokeCap.Round
            )
        )

        drawCircle(
            color = LoginPurple,
            radius = size.minDimension * 0.12f,
            center = Offset(
                size.width * 0.50f,
                size.height * 0.50f
            )
        )

        if (!visible) {
            drawLine(
                color = LoginFieldBackground,
                start = Offset(
                    size.width * 0.10f,
                    size.height * 0.10f
                ),
                end = Offset(
                    size.width * 0.90f,
                    size.height * 0.90f
                ),
                strokeWidth = stroke * 2.5f,
                cap = StrokeCap.Round
            )

            drawLine(
                color = LoginPurple,
                start = Offset(
                    size.width * 0.10f,
                    size.height * 0.10f
                ),
                end = Offset(
                    size.width * 0.90f,
                    size.height * 0.90f
                ),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
