package com.example.alzheimerapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.alzheimerapp.model.CardItem

@Composable
fun CardView(card: CardItem, onClick: () -> Unit) {
    val successColor = Color(0xFF4CAF50)

    val bgColor by animateColorAsState(
        targetValue = when {
            card.isMatched -> successColor
            card.isRevealed -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.secondaryContainer
        },
        animationSpec = tween(300),
        label = "bgColor"
    )

    val scaleValue by animateFloatAsState(
        targetValue = when {
            card.isMatched -> 0.8f
            card.isRevealed -> 1.05f
            else -> 1f
        },
        animationSpec = tween(300),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scaleValue)
    ) {
        Button(
            onClick = if (card.isRevealed || card.isMatched) ({}) else onClick,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = bgColor,
                disabledContainerColor = bgColor
            ),
            elevation = if (card.isMatched) null else ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            when {
                card.isMatched -> Text("✓", fontSize = 40.sp)

                card.isRevealed -> {
                    if (card.imageUri.startsWith("content://") || card.imageUri.startsWith("http")) {
                        AsyncImage(
                            model = card.imageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(card.imageUri, fontSize = 40.sp)
                    }
                }

                else -> Box(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
