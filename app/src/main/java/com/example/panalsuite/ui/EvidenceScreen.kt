package com.example.panalsuite.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.panalsuite.data.local.EvidenceDao
import com.example.panalsuite.data.local.EvidenceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID


@Composable
fun EvidenceScreen(
    ticketId: String,
    evidenceDao: EvidenceDao,
    onBack: () -> Unit = {}
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val evidences by produceState(
        initialValue = emptyList<EvidenceEntity>(),
        key1 = ticketId
    ) {
        evidenceDao.getEvidencesByTicket(ticketId).collect { savedEvidences ->
            value = savedEvidences
        }
    }

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {
            coroutineScope.launch {
                errorMessage = null

                try {
                    withContext(Dispatchers.IO) {

                        val imagePath = saveImageToInternalStorage(
                            context = context,
                            imageUri = uri
                        )

                        evidenceDao.insertEvidence(
                            EvidenceEntity(
                                ticketId = ticketId,
                                imagePath = imagePath
                            )
                        )
                    }

                    selectedImageUri = uri

                } catch (_: Exception) {
                    errorMessage = "No se pudo guardar la imagen"
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Evidencias de la solicitud")

        Button(
            onClick = {
                imagePicker.launch("image/*")
            }
        ) {
            Text(text = "Seleccionar imagen")
        }
        if (selectedImageUri != null) {
            Text(text = "Imagen seleccionada corectamente")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Evidencias guardadas: ${evidences.size}")

        if (evidences.isEmpty()) {
            Text("Esta solicitud todavía no tiene evidencias.")
        } else {
            evidences.forEachIndexed { index, evidence ->

                Text("📎 Evidencia ${index + 1}")

                val bitmap = remember(evidence.imagePath) {
                    BitmapFactory.decodeFile(evidence.imagePath)
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Evidencia ${index + 1}",
                        modifier = Modifier.size(200.dp)
                    )
                } else {
                    Text("No se pudo mostrar la imagen")
                }
            }
        }

        TextButton(
            onClick = onBack
        ) {
            Text("Volver")
        }
    }
}
private fun saveImageToInternalStorage(
    context: Context,
    imageUri: Uri
): String {

    val evidenceFolder = File(context.filesDir, "evidences")

    if (!evidenceFolder.exists()) {
        evidenceFolder.mkdirs()
    }

    val imageFile = File(
        evidenceFolder,
        "${UUID.randomUUID()}.jpg"
    )

    context.contentResolver.openInputStream(imageUri).use { inputStream ->
        requireNotNull(inputStream) {
            "No se pudo abrir la imagen seleccionada"
        }

        imageFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    return imageFile.absolutePath
}