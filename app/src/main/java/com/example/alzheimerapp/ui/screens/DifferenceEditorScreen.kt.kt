package com.example.alzheimerapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alzheimerapp.model.DifferenceArea
import com.example.alzheimerapp.model.DifferenceLevel
import com.example.alzheimerapp.ui.components.ImageEditorContainer

@Composable
fun DifferenceEditorScreen(imageLeft: String, imageRight: String, onSave: (DifferenceLevel) -> Unit) {
    var areas by remember { mutableStateOf(listOf<DifferenceArea>()) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Toca la imagen INFERIOR para marcar diferencias", style = MaterialTheme.typography.titleMedium)
        Text("Diferencias marcadas: ${areas.size}/10", style = MaterialTheme.typography.bodySmall)

        // Usamos weight(1f) para que esta columna ocupe el espacio central
        // y empuje los botones hacia abajo.
        Column(Modifier.weight(1f).padding(vertical = 12.dp), verticalArrangement = Arrangement.Center) {

            ImageEditorContainer(
                image = imageLeft,
                areas = areas,
                onAddArea = { }, // AHORA LA SUPERIOR ES SOLO VISUAL
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.height(12.dp))

            ImageEditorContainer(
                image = imageRight,
                areas = areas,
                onAddArea = { if (areas.size < 10) areas = areas + it }, // AHORA MARCAS EN LA INFERIOR
                modifier = Modifier.weight(1f)
            )
        }

        // Esta fila siempre quedará en la parte inferior de la pantalla
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = { if (areas.isNotEmpty()) areas = areas.dropLast(1) }) {
                Text("Deshacer")
            }
            Button(
                onClick = { onSave(DifferenceLevel(imageLeft, imageRight, areas)) },
                enabled = areas.isNotEmpty()
            ) {
                Text("Guardar Nivel")
            }
        }
    }
}