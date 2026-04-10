package com.example.alzheimerapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alzheimerapp.data.RewardImage
import com.example.alzheimerapp.model.CardItem
import com.example.alzheimerapp.model.DifficultyManager
import com.example.alzheimerapp.model.generateCards
import com.example.alzheimerapp.ui.components.CardView
import com.example.alzheimerapp.ui.components.SuccessOverlay
import kotlinx.coroutines.delay

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
    val currentLevel = DifficultyManager.level
    val cards = remember(resetKey, currentLevel) {
        generateCards(objectImages)
    }

    var levelIndex by remember { mutableIntStateOf(0) }
    val selectedCards = remember { mutableStateListOf<CardItem>() }

    var moves by remember { mutableIntStateOf(0) }
    val totalPairs = cards.size / 2

    var showSuccess by remember { mutableStateOf(false) }
    var rewardImage by remember { mutableStateOf<String?>(null) }

    var pendingMismatch by remember { mutableStateOf<Pair<CardItem, CardItem>?>(null) }

    LaunchedEffect(pendingMismatch) {
        pendingMismatch?.let { (c1, c2) ->
            delay(800)
            c1.isRevealed = false
            c2.isRevealed = false
            selectedCards.clear()
            pendingMismatch = null
        }
    }

    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(3000)
            moves = 0
            resetKey++
            selectedCards.clear()
            showSuccess = false
            rewardImage = null

            if (levelIndex >= rewardImages.size) {
                levelIndex = 0
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
                            "Memoria",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Nivel: $currentLevel parejas | Movimientos: $moves",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    TextButton(onClick = { showExitDialog = true }) {
                        Text("Salir", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

            if (showSuccess) {
                SuccessOverlay(rewardImage)
            }
        }
    }
}
