package com.example.panalsuite.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.panalsuite.data.local.EvidenceDao
import com.example.panalsuite.data.local.EvidenceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenceScreen(
    ticketId: String,
    evidenceDao: EvidenceDao,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val evidences by produceState(
        initialValue = emptyList<EvidenceEntity>(),
        key1 = ticketId
    ) {
        evidenceDao.getEvidencesByTicket(ticketId).collect { savedEvidences ->
            value = savedEvidences
        }
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Selector para Galería
    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                errorMessage = null
                try {
                    val imagePath = withContext(Dispatchers.IO) {
                        saveUriToInternalStorage(context, uri)
                    }
                    evidenceDao.insertEvidence(
                        EvidenceEntity(
                            ticketId = ticketId,
                            imagePath = imagePath
                        )
                    )
                    successMessage = "Imagen agregada desde galería"
                } catch (_: Exception) {
                    errorMessage = "No se pudo guardar la imagen"
                }
            }
        }
    }

    // Captura mediante Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            coroutineScope.launch {
                errorMessage = null
                try {
                    val imagePath = withContext(Dispatchers.IO) {
                        saveBitmapToInternalStorage(context, bitmap)
                    }
                    evidenceDao.insertEvidence(
                        EvidenceEntity(
                            ticketId = ticketId,
                            imagePath = imagePath
                        )
                    )
                    successMessage = "Foto tomada con éxito"
                } catch (_: Exception) {
                    errorMessage = "No se pudo guardar la foto de la cámara"
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Evidencias ($ticketId)") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Adjuntar Evidencias Fotográficas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { cameraLauncher.launch() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Tomar Foto")
                }

                OutlinedButton(
                    onClick = { galleryPicker.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Galería")
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (successMessage != null) {
                Text(
                    text = successMessage ?: "",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Evidencias guardadas: ${evidences.size}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            if (evidences.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "Esta solicitud aún no tiene evidencias adjuntas.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                evidences.forEachIndexed { index, evidence ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "📎 Evidencia ${index + 1}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            val bitmap = remember(evidence.imagePath) {
                                BitmapFactory.decodeFile(evidence.imagePath)
                            }

                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Evidencia ${index + 1}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                            } else {
                                Text(
                                    text = "No se pudo cargar la imagen",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}

private fun saveUriToInternalStorage(context: Context, imageUri: Uri): String {
    val folder = File(context.filesDir, "evidences")
    if (!folder.exists()) folder.mkdirs()

    val file = File(folder, "EVI_${UUID.randomUUID()}.jpg")
    context.contentResolver.openInputStream(imageUri)?.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return file.absolutePath
}

private fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String {
    val folder = File(context.filesDir, "evidences")
    if (!folder.exists()) folder.mkdirs()

    val file = File(folder, "CAM_${UUID.randomUUID()}.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return file.absolutePath
}