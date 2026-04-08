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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alzheimerapp.data.RewardImage
import com.example.alzheimerapp.model.CardItem
import com.example.alzheimerapp.model.DifficultyManager
import com.example.alzheimerapp.ui.components.CardView
import com.example.alzheimerapp.ui.components.SuccessOverlay
import kotlinx.coroutines.delay

// ----------- Juego -----------
@Composable
fun MatchScreen(
    objectImages: List<String>,
    rewardImages: MutableList<RewardImage>,
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

    var resetKey by remember { mutableIntStateOf(0) }
    val cards = remember(resetKey, DifficultyManager.level) {
        generateCards(objectImages)
    }

    var levelIndex by remember { mutableIntStateOf(0) }
    val selectedCards = remember { mutableStateListOf<CardItem>() }

    var moves by remember { mutableIntStateOf(0) }
    val totalPairs = cards.size / 2

    var showSuccess by remember { mutableStateOf(false) }
    var rewardImage by remember { mutableStateOf<String?>(null) }

    var pendingMismatch by remember { mutableStateOf<Pair<CardItem, CardItem>?>(null) }

    // ✅ EFECTO: ocultar cartas incorrectas
    LaunchedEffect(pendingMismatch) {
        pendingMismatch?.let { (c1, c2) ->
            delay(800)
            c1.isRevealed = false
            c2.isRevealed = false
            selectedCards.clear()
            pendingMismatch = null
        }
    }

    // ✅ EFECTO: victoria
    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(3000)
            moves = 0
            resetKey++
            selectedCards.clear()
            showSuccess = false
            rewardImage = null

            if (levelIndex >= rewardImages.size) {
                levelIndex = 0 // 🔁 reinicio ciclo
            }
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

            val level by remember { derivedStateOf { DifficultyManager.level } }
            Text("Nivel: $level parejas", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(16.dp))

            cards.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    row.forEach { card ->
                        CardView(card) {

                            if (card.isMatched || card.isRevealed || selectedCards.size >= 2) return@CardView

                            card.isRevealed = true
                            selectedCards.add(card)

                            if (selectedCards.size == 2) {
                                moves++

                                val c1 = selectedCards[0]
                                val c2 = selectedCards[1]

                                if (c1.imageUri == c2.imageUri) {
                                    c1.isMatched = true
                                    c2.isMatched = true
                                    selectedCards.clear()

                                    if (cards.all { it.isMatched }) {
                                        showSuccess = true

                                        val sorted = rewardImages.sortedBy { it.order }

                                        if (sorted.isNotEmpty()) {
                                            val safeIndex = levelIndex % sorted.size
                                            rewardImage = sorted[safeIndex].uri
                                            levelIndex++
                                        } else {
                                            rewardImage = null
                                        }

                                        if (moves <= totalPairs * 2) {
                                            DifficultyManager.increase()
                                        } else {
                                            DifficultyManager.decrease()
                                        }
                                    }

                                } else {
                                    pendingMismatch = Pair(c1, c2)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // overlay victoria
        if (showSuccess) {
            SuccessOverlay(rewardImage)
        }
    }
}



// ----------- Generar cartas -----------

fun generateCards(images: List<String>): List<CardItem> {

    val defaultIcons = listOf(
        "🍎","🚗","🐶","🏠","⭐","⚽","🎵","📱","🍕","🚀","🎲","🎁"
    )

    val level = DifficultyManager.level

    // 1. Coge imágenes del usuario (máximo = nivel)
    val selectedImages = images.shuffled().take(level)

    // 2. Calcula cuántas faltan
    val missing = level - selectedImages.size

    // 3. Rellena con emojis si faltan
    val filler = defaultIcons
        .shuffled()
        .take(missing)

    // 4. Mezcla todo
    val finalList = (selectedImages + filler)

    // 5. Duplica para parejas y mezcla
    return (finalList + finalList)
        .shuffled()
        .mapIndexed { i, value ->
            CardItem(i, value)
        }
}
