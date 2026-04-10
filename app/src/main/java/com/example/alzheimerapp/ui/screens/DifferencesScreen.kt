package com.example.alzheimerapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            // Seleccionar recompensa aleatoria
            rewardImageUri = rewardImages.randomOrNull()?.uri
            showSuccess = true
            
            delay(3000) // Ver la recompensa 3 segundos
            
            showSuccess = false
            if (currentLevelIndex < levels.size - 1) {
                currentLevelIndex++
                foundAreas = emptySet()
            } else {
                // Si era el último nivel, podemos mostrar el diálogo final o salir
                // Por ahora dejamos que el diálogo de victoria se encargue si queremos uno final,
                // pero el usuario pidió ver la recompensa entre niveles.
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
        }

        // Overlay de Recompensa
        if (showSuccess) {
            SuccessOverlay(rewardImageUri)
        }

        // Diálogo final solo cuando se completan TODOS los niveles
        if (foundAreas.size == total && currentLevelIndex == levels.size - 1 && !showSuccess) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("¡Juego Terminado!") },
                text = { Text("Has completado todos los niveles de diferencias. ¡Excelente trabajo!") },
                confirmButton = {
                    Button(onClick = onBack) { Text("Finalizar") }
                }
            )
        }
    }
}
