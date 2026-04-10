package com.example.alzheimerapp.ui.screens

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.alzheimerapp.data.RewardImage
import com.example.alzheimerapp.model.DifferenceLevel
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.*

@Composable
fun ImageManagerScreen(
    objectImages: MutableList<String>,
    rewardImages: MutableList<RewardImage>,
    differenceLevels: MutableList<DifferenceLevel>,
    onAddDifferenceLevel: () -> Unit,
    onSaveAll: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris.forEach { uri ->
            val uriString = uri.toString()

            if (selectedTabIndex == 0) { // Objetos
                if (!objectImages.contains(uriString)) {
                    try {
                        context.contentResolver.takePersistableUriPermission(
                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        objectImages.add(uriString)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("No se añade la imagen porque ya está entre los objetos cargados.")
                    }
                }
            } else if (selectedTabIndex == 1) { // Recompensas
                if (rewardImages.none { it.uri == uriString }) {
                    try {
                        context.contentResolver.takePersistableUriPermission(
                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        rewardImages.add(RewardImage(uriString, rewardImages.size))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Esta recompensa ya está en la lista.")
                    }
                }
            }
        }
        onSaveAll()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Text(
                text = "Gestión de imágenes",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                    Text("Objetos", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                    Text("Recompensas", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTabIndex == 2, onClick = { selectedTabIndex = 2 }) {
                    Text("Diferencias", modifier = Modifier.padding(16.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            // BOTÓN AÑADIR (Adaptado según la pestaña)
            Button(
                onClick = {
                    if (selectedTabIndex < 2) {
                        launcher.launch(arrayOf("image/*"))
                    } else {
                        onAddDifferenceLevel()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    when (selectedTabIndex) {
                        0 -> "Añadir objetos"
                        1 -> "Añadir recompensas"
                        else -> "Crear nuevo nivel de diferencias"
                    }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTabIndex) {
                    0 -> ObjectGrid(objectImages, onSaveAll)
                    1 -> RewardListReorderable(rewardImages) { updated ->
                        val normalized = normalizeOrder(updated)
                        rewardImages.clear()
                        rewardImages.addAll(normalized)
                        onSaveAll()
                    }
                    2 -> DifferenceLevelsList(differenceLevels) {
                        onSaveAll()
                    }
                }
            }
        }
    }
}

@Composable
fun DifferenceLevelsList(
    levels: MutableList<DifferenceLevel>,
    onChanged: () -> Unit
) {
    if (levels.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay niveles de diferencias creados.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(levels) { level ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Nivel con ${level.areas.size} diferencias", style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = {
                                levels.remove(level)
                                onChanged()
                            }) {
                                Text("X", color = Color.Red)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.height(100.dp)) {
                            AsyncImage(
                                model = level.imageLeft,
                                contentDescription = null,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            AsyncImage(
                                model = level.imageRight,
                                contentDescription = null,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ObjectGrid(
    objectImages: MutableList<String>,
    onChanged: () -> Unit
) {
    if (objectImages.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay objetos cargados.", color = Color.Gray)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(objectImages) { uri ->
                ImageCard(
                    uri = uri,
                    label = null,
                    onDelete = {
                        objectImages.remove(uri)
                        onChanged()
                    }
                )
            }
        }
    }
}

@Composable
fun RewardListReorderable(
    rewards: MutableList<RewardImage>,
    onChange: (List<RewardImage>) -> Unit
) {
    if (rewards.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay recompensas cargadas.", color = Color.Gray)
        }
    } else {
        val state = rememberReorderableLazyListState(
            onMove = { from, to ->
                rewards.add(to.index, rewards.removeAt(from.index))
                onChange(rewards.toList())
            }
        )

        LazyColumn(
            state = state.listState,
            modifier = Modifier
                .fillMaxSize()
                .reorderable(state),
            contentPadding = PaddingValues(12.dp)
        ) {
            items(
                items = rewards,
                key = { it.uri }
            ) { item ->
                ReorderableItem(state, key = item.uri) { isDragging ->
                    val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp, label = "")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .shadow(elevation)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, _ -> change.consume() }
                                    }
                                    .padding(end = 8.dp)
                            ) {
                                Text(text = "≡", style = MaterialTheme.typography.headlineMedium)
                            }

                            AsyncImage(
                                model = item.uri,
                                contentDescription = null,
                                modifier = Modifier.size(100.dp)
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Column {
                                Row {
                                    IconButton(onClick = {
                                        val index = rewards.indexOf(item)
                                        if (index > 0) {
                                            rewards.removeAt(index)
                                            rewards.add(index - 1, item)
                                            onChange(rewards.toList())
                                        }
                                    }) { Text("↑") }
                                    IconButton(onClick = {
                                        val index = rewards.indexOf(item)
                                        if (index < rewards.lastIndex) {
                                            rewards.removeAt(index)
                                            rewards.add(index + 1, item)
                                            onChange(rewards.toList())
                                        }
                                    }) { Text("↓") }
                                }
                                Button(onClick = {
                                    rewards.remove(item)
                                    onChange(rewards.toList())
                                }) { Text("Eliminar") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageCard(
    uri: String,
    label: String?,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Box {
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            label?.let {
                Text(
                    text = it,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd)
            ) { Text("X", color = Color.Red) }
        }
    }
}

fun normalizeOrder(list: List<RewardImage>): List<RewardImage> {
    return list.mapIndexed { index, item ->
        item.copy(order = index)
    }
}
