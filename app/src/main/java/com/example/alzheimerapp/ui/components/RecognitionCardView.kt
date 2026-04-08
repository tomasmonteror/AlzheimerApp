package com.example.alzheimerapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.alzheimerapp.model.CardItem

@Composable
fun RecognitionCardView(
    card: CardItem,
    onClick: () -> Unit
) {
    val successColor = Color(0xFF4CAF50)

    val bgColor by animateColorAsState(
        targetValue = when {
            card.isMatched -> successColor
            card.isCorrect == true -> successColor
            card.isCorrect == false -> MaterialTheme.colorScheme.error
            card.isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.secondaryContainer
        },
        animationSpec = tween(300),
        label = "bgColor"
    )

    // Escala con secuencia: Normal -> Pequeña (0.7f) -> Oculta (0f)
    val scaleValue by animateFloatAsState(
        targetValue = when {
            card.isMatched -> 0f
            card.isSelected -> 1.1f
            else -> 1f
        },
        animationSpec = if (card.isMatched) {
            keyframes {
                durationMillis = 1500
                1.0f at 0
                0.7f at 500 // Se hace pequeña tras ponerse verde
                0.7f at 1000 // Mantiene el tamaño pequeño un momento
                0f at 1500 // Desaparece
            }
        } else {
            tween(300)
        },
        label = "scale"
    )

    // Opacidad: se desvanece al final de la animación de escala
    val alphaValue by animateFloatAsState(
        targetValue = if (card.isMatched) 0f else 1f,
        animationSpec = if (card.isMatched) {
            tween(durationMillis = 400, delayMillis = 1100)
        } else {
            tween(300)
        },
        label = "alpha"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .size(120.dp)
            .scale(scaleValue)
            .alpha(alphaValue),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            disabledContainerColor = bgColor
        ),
        elevation = if (card.isMatched) null else ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        contentPadding = PaddingValues(4.dp),
        enabled = !card.isMatched
    ) {
        AsyncImage(
            model = card.imageUri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
