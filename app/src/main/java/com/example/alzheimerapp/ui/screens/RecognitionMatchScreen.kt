package com.example.alzheimerapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alzheimerapp.data.RewardImage
import com.example.alzheimerapp.model.CardItem
import com.example.alzheimerapp.model.DifficultyManager
import com.example.alzheimerapp.ui.components.RecognitionCardView
import com.example.alzheimerapp.ui.components.SuccessOverlay
import kotlinx.coroutines.delay

@Composable
fun RecognitionMatchScreen(
    objectImages: List<String>,
    rewardImages: List<RewardImage>,
    onBack: () -> Unit
) {
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("¿Salir del juego?") },
            text = { Text("Si sales ahora, el progreso se reiniciará al nivel inicial (2 parejas).") },
            confirmButton = {
                TextButton(onClick = {
                    DifficultyManager.reset()
                    onBack()
                }) {
                    Text("Sí, salir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("No, continuar")
                }
            }
        )
    }

    // Usamos resetKey y el nivel actual como claves para regenerar las cartas
    var resetKey by remember { mutableIntStateOf(0) }
    val currentLevel = DifficultyManager.level

    val cards = remember(resetKey, currentLevel) {
        generateCards(objectImages)
    }

    val selectedCards = remember { mutableStateListOf<CardItem>() }

    var moves by remember { mutableIntStateOf(0) }
    var matchedPairs by remember { mutableIntStateOf(0) }
    val totalPairs = cards.size / 2

    var showSuccess by remember { mutableStateOf(false) }
    var rewardImage by remember { mutableStateOf<String?>(null) }

    // Reset tras victoria e incremento de nivel
    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(3000) // Tiempo para ver la recompensa

            // INCREMENTAR NIVEL PARA LA PRÓXIMA PANTALLA
            DifficultyManager.increase()

            resetKey++ // Dispara la regeneración de cartas
            selectedCards.clear()
            showSuccess = false
            rewardImage = null
            moves = 0
            matchedPairs = 0
        }
    }

    // Efecto para limpiar la selección visual (rojo/verde) tras un breve delay
    LaunchedEffect(selectedCards.size) {
        if (selectedCards.size == 2) {
            delay(800)
            selectedCards.forEach {
                it.isCorrect = null
                it.isSelected = false
            }
            selectedCards.clear()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Movimientos: $moves", fontSize = 20.sp)
            Text("Parejas: $matchedPairs / $totalPairs", fontSize = 16.sp)
            Text("Nivel Actual: $currentLevel parejas", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)

            Spacer(modifier = Modifier.height(16.dp))

            // Dibujamos las filas de cartas
            cards.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { card ->
                        RecognitionCardView(card) {
                            if (card.isMatched || card.isSelected || selectedCards.size >= 2) return@RecognitionCardView

                            card.isSelected = true
                            selectedCards.add(card)

                            if (selectedCards.size == 2) {
                                moves++
                                val c1 = selectedCards[0]
                                val c2 = selectedCards[1]

                                if (c1.imageUri == c2.imageUri) {
                                    // ACIERTO: Se marcan en verde (isCorrect = true) y se quedan fijas (isMatched)
                                    c1.isMatched = true
                                    c2.isMatched = true
                                    c1.isCorrect = true
                                    c2.isCorrect = true
                                    matchedPairs++

                                    if (matchedPairs == totalPairs) {
                                        rewardImage = rewardImages.randomOrNull()?.uri
                                        showSuccess = true
                                    }
                                } else {
                                    // ERROR: Se marcan en rojo (isCorrect = false) temporalmente
                                    c1.isCorrect = false
                                    c2.isCorrect = false
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showSuccess) {
            SuccessOverlay(rewardImage)
        }
    }
}
