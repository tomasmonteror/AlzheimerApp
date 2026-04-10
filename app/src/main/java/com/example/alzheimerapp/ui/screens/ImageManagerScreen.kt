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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    onSaveAll: () -> Unit,
    onBack: () -> Unit
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
        topBar = {
            Surface(shadowElevation = 3.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gestión",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onBack) {
                        Text("Volver", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
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

            Spacer(Modifier.height(16.dp))

            ExtendedFloatingActionButton(
                onClick = {
                    if (selectedTabIndex < 2) {
                        launcher.launch(arrayOf("image/*"))
                    } else {
                        onAddDifferenceLevel()
                    }
                },
                icon = { Icon(Icons.Default.Add, null) },
                text = {
                    Text(
                        when (selectedTabIndex) {
                            0 -> "Añadir objetos"
                            1 -> "Añadir recompensas"
                            else -> "Crear nivel"
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )

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
            Text("No hay niveles de diferencias.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(levels) { level ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${level.areas.size} Diferencias", fontWeight = FontWeight.SemiBold)
                            IconButton(onClick = {
                                levels.remove(level)
                                onChanged()
                            }) {
                                Text("X", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.height(120.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AsyncImage(
                                model = level.imageLeft,
                                contentDescription = null,
                                modifier = Modifier.weight(1f).shadow(2.dp, RoundedCornerShape(8.dp))
                            )
                            AsyncImage(
                                model = level.imageRight,
                                contentDescription = null,
                                modifier = Modifier.weight(1f).shadow(2.dp, RoundedCornerShape(8.dp))
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
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(objectImages) { uri ->
                ImageCard(
                    uri = uri,
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
            Text("No hay recompensas.", color = Color.Gray)
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = rewards,
                key = { it.uri }
            ) { item ->
                ReorderableItem(state, key = item.uri) { isDragging ->
                    val elevation by animateDpAsState(if (isDragging) 8.dp else 2.dp)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "☰",
                                modifier = Modifier
                                    .detectReorderAfterLongPress(state)
                                    .padding(end = 12.dp),
                                style = MaterialTheme.typography.headlineSmall
                            )

                            AsyncImage(
                                model = item.uri,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp).shadow(1.dp, RoundedCornerShape(4.dp))
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(onClick = {
                                rewards.remove(item)
                                onChange(rewards.toList())
                            }) {
                                Text("X", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun normalizeOrder(list: List<RewardImage>): List<RewardImage> {
    return list.mapIndexed { index, item ->
        item.copy(order = index)
    }
}

@Composable
fun ImageCard(uri: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 8.dp)
                    .shadow(1.dp, RoundedCornerShape(8.dp))
            )
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar")
            }
        }
    }
}
