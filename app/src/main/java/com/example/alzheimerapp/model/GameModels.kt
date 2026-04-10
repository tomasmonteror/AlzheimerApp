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

fun generateCards(images: List<String>): List<CardItem> {
    val level = DifficultyManager.level
    val selectedImages = if (images.size >= level) {
        images.shuffled().take(level)
    } else if (images.isNotEmpty()) {
        List(level) { images[it % images.size] }
    } else {
        emptyList()
    }
    
    val cardImages = (selectedImages + selectedImages).shuffled()
    return cardImages.mapIndexed { index, uri ->
        CardItem(id = index, imageUri = uri)
    }
}

data class DifferenceArea(
    val x: Float, // Porcentaje 0.0f a 1.0f relativo al contenedor
    val y: Float, // Porcentaje 0.0f a 1.0f relativo al contenedor
    val radiusDp: Float = 35f // Radio generoso para facilitar el toque
)

data class DifferenceLevel(
    val imageLeft: String,
    val imageRight: String,
    val areas: List<DifferenceArea>
)
