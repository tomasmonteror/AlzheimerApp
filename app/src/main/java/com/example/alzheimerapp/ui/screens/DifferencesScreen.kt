package com.example.alzheimerapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.alzheimerapp.data.RewardImage
import com.example.alzheimerapp.model.DifferenceLevel
import com.example.alzheimerapp.ui.components.ImageGameContainer
import com.example.alzheimerapp.ui.components.SuccessOverlay
import kotlinx.coroutines.delay

@Composable
fun DifferencesScreen(
    levels: List<DifferenceLevel>,
    rewardImages: List<RewardImage>,
    onBack: () -> Unit
) {
    var currentLevelIndex by remember { mutableIntStateOf(0) }
    val currentLevel = levels[currentLevelIndex]

    var foundAreas by remember { mutableStateOf(setOf<Int>()) }
    val total = currentLevel.areas.size

    var showSuccess by remember { mutableStateOf(false) }
    var rewardImageUri by remember { mutableStateOf<String?>(null) }

    // Efecto tras completar un nivel
    LaunchedEffect(foundAreas.size) {
        if (foundAreas.size == total && total > 0) {
            rewardImageUri = rewardImages.randomOrNull()?.uri
            showSuccess = true
            
            delay(3000)
            
            showSuccess = false
            if (currentLevelIndex < levels.size - 1) {
                currentLevelIndex++
                foundAreas = emptySet()
            }
        }
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 3.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Nivel ${currentLevelIndex + 1} / ${levels.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Encontradas: ${foundAreas.size} de $total",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    TextButton(onClick = onBack) {
                        Text("Salir", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
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
                Spacer(Modifier.height(16.dp))
                ImageGameContainer(
                    image = currentLevel.imageRight,
                    areas = currentLevel.areas,
                    foundAreas = foundAreas,
                    onHit = { index -> foundAreas = foundAreas + index }
                )
            }

            if (showSuccess) {
                SuccessOverlay(rewardImageUri)
            }

            if (foundAreas.size == total && currentLevelIndex == levels.size - 1 && !showSuccess) {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text("¡Juego Terminado!") },
                    text = { Text("Has completado todos los niveles. ¡Excelente trabajo!") },
                    confirmButton = {
                        Button(onClick = onBack) { Text("Finalizar") }
                    }
                )
            }
        }
    }
}
