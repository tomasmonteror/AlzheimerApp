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

    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(3000)
            DifficultyManager.increase()
            resetKey++
            selectedCards.clear()
            showSuccess = false
            rewardImage = null
            moves = 0
            matchedPairs = 0
        }
    }

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
                            "Visual",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Nivel: $currentLevel | Parejas: $matchedPairs / $totalPairs",
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
                            RecognitionCardView(card) {
                                if (card.isMatched || card.isSelected || selectedCards.size >= 2) return@RecognitionCardView

                                card.isSelected = true
                                selectedCards.add(card)

                                if (selectedCards.size == 2) {
                                    moves++
                                    val c1 = selectedCards[0]
                                    val c2 = selectedCards[1]

                                    if (c1.imageUri == c2.imageUri) {
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
}
