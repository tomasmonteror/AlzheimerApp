package com.example.alzheimerapp.ui.components

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.alzheimerapp.model.DifferenceArea
import kotlin.math.hypot

@Composable
fun ImageGameContainer(image: String, areas: List<DifferenceArea>, foundAreas: Set<Int>, onHit: (Int) -> Unit) {
    val currentOnHit by rememberUpdatedState(onHit)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.33f) // 4:3 Garantiza que las coordenadas % coincidan siempre
            .background(Color.Black.copy(alpha = 0.05f))
            .border(1.dp, Color.LightGray)
            .pointerInput(areas) {
                detectTapGestures { offset ->
                    areas.forEachIndexed { index, area ->
                        val areaXPx = area.x * size.width
                        val areaYPx = area.y * size.height

                        // Radio de colisión basado en el tamaño real del contenedor
                        val radiusPx = area.radiusDp * (size.width / 400f)

                        if (hypot(offset.x - areaXPx, offset.y - areaYPx) < radiusPx) {
                            currentOnHit(index)
                        }
                    }
                }
            }
    ) {
        AsyncImage(model = image, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        Canvas(Modifier.fillMaxSize()) {
            foundAreas.forEach { index ->
                if (index < areas.size) {
                    val area = areas[index]
                    drawCircle(
                        color = Color(0xFF4CAF50),
                        radius = 45f,
                        center = Offset(area.x * size.width, area.y * size.height),
                        alpha = 0.6f
                    )
                }
            }
        }
    }
}

@Composable
fun ImageEditorContainer(image: String, areas: List<DifferenceArea>, onAddArea: (DifferenceArea) -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.33f)
            .background(Color.Black.copy(alpha = 0.05f))
            .border(1.dp, Color.LightGray)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onAddArea(DifferenceArea(x = offset.x / size.width, y = offset.y / size.height))
                }
            }
    ) {
        AsyncImage(model = image, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        Canvas(Modifier.fillMaxSize()) {
            areas.forEach { area ->
                drawCircle(
                    color = Color.Red,
                    radius = 35f,
                    center = Offset(area.x * size.width, area.y * size.height),
                    alpha = 0.5f
                )
            }
        }
    }
}

// --- Componentes de Apoyo ---

@Composable
fun ImageSelectorCard(uri: Uri?, label: String, onClick: () -> Unit, modifier: Modifier) {
    OutlinedCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (uri != null) {
                AsyncImage(model = uri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
            } else {
                Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            }
            // Indicador visual de que es clickable
            Surface(
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text("Cambiar", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}