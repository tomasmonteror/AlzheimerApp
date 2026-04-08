package com.example.alzheimerapp.model

import androidx.compose.runtime.*

object DifficultyManager {
    var level by mutableIntStateOf(2)

    fun increase() { if (level < 6) level++ }
    fun decrease() { if (level > 2) level-- }
    fun reset() { level = 2 }
}

class CardItem(
    val id: Int,
    val imageUri: String,
    initialRevealed: Boolean = false,
    initialMatched: Boolean = false
) {
    var isRevealed by mutableStateOf(initialRevealed)
    var isMatched by mutableStateOf(initialMatched)

    var isSelected by mutableStateOf(false)

    var isCorrect by mutableStateOf<Boolean?>(null)
}
