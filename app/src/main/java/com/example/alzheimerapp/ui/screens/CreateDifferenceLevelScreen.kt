package com.example.alzheimerapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alzheimerapp.ui.components.ImageSelectorCard

@Composable
fun CreateDifferenceLevelScreen(onNavigateToEditor: (String, String) -> Unit) {
    var originalUri by remember { mutableStateOf<Uri?>(null) }
    var modifiedUri by remember { mutableStateOf<Uri?>(null) }

    val launcherOriginal = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { originalUri = it }
    val launcherModified = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { modifiedUri = it }

    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Configurar Nivel", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(32.dp))

        // Selector Imagen 1
        ImageSelectorCard(
            uri = originalUri,
            label = "Imagen Original",
            onClick = { launcherOriginal.launch("image/*") },
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.height(16.dp))

        // Selector Imagen 2
        ImageSelectorCard(
            uri = modifiedUri,
            label = "Imagen Modificada",
            onClick = { launcherModified.launch("image/*") },
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.height(32.dp))

        Button(
            enabled = originalUri != null && modifiedUri != null,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            onClick = { onNavigateToEditor(originalUri.toString(), modifiedUri.toString()) }
        ) {
            Text("Siguiente: Marcar Diferencias")
        }
    }
}