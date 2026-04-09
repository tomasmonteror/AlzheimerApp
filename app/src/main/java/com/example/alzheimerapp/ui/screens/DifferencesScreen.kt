package com.example.alzheimerapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.alzheimerapp.model.DifferenceArea
import com.example.alzheimerapp.model.DifferenceLevel
import com.example.alzheimerapp.ui.components.ImageEditorContainer
import com.example.alzheimerapp.ui.components.ImageGameContainer
import kotlin.math.hypot

// En DifferencesScreen.kt

@Composable
fun DifferencesScreen(levels: List<DifferenceLevel>, onBack: () -> Unit) {
    var currentLevelIndex by remember { mutableIntStateOf(0) }
    val currentLevel = levels[currentLevelIndex]

    var foundAreas by remember { mutableStateOf(setOf<Int>()) }
    val total = currentLevel.areas.size

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Surface(shadowElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Nivel ${currentLevelIndex + 1} de ${levels.size}", style = MaterialTheme.typography.titleMedium)
                    Text("Encontradas: ${foundAreas.size} de $total", color = MaterialTheme.colorScheme.primary)
                }
                TextButton(onClick = onBack) { Text("Salir") }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageGameContainer(
                image = currentLevel.imageLeft,
                areas = currentLevel.areas,
                foundAreas = foundAreas,
                onHit = { index -> foundAreas = foundAreas + index }
            )
            Spacer(Modifier.height(12.dp))
            ImageGameContainer(
                image = currentLevel.imageRight,
                areas = currentLevel.areas,
                foundAreas = foundAreas,
                onHit = { index -> foundAreas = foundAreas + index }
            )
        }

        if (foundAreas.size == total && total > 0) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(if (currentLevelIndex < levels.size - 1) "¡Nivel Completado!" else "¡Juego Terminado!") },
                text = { Text(if (currentLevelIndex < levels.size - 1) "¿Pasar al siguiente nivel?" else "Has completado todos los niveles.") },
                confirmButton = {
                    Button(onClick = {
                        if (currentLevelIndex < levels.size - 1) {
                            currentLevelIndex++
                            foundAreas = emptySet()
                        } else {
                            onBack()
                        }
                    }) {
                        Text(if (currentLevelIndex < levels.size - 1) "Siguiente Nivel" else "Finalizar")
                    }
                }
            )
        }
    }
}


