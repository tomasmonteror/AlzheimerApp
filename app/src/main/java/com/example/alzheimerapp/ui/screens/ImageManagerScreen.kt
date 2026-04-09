package com.example.alzheimerapp.ui.screens

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import com.example.alzheimerapp.data.ImageStorage
import kotlinx.coroutines.CoroutineScope
import androidx.compose.foundation.lazy.grid.*
import org.burnoutcrew.reorderable.*
import androidx.compose.animation.core.animateDpAsState

import androidx.compose.ui.draw.shadow

import com.example.alzheimerapp.data.RewardImage
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun ImageManagerScreen(
    objectImages: MutableList<String>,
    rewardImages: MutableList<RewardImage>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // 1. Estado para los mensajes
    val snackbarHostState = remember { SnackbarHostState() }

    var isObjectMode by remember { mutableStateOf(true) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris.forEach { uri ->
            val uriString = uri.toString()

            if (isObjectMode) {
                if (!objectImages.contains(uriString)) {
                    context.contentResolver.takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    objectImages.add(uriString)
                } else {
                    // 2. Mostrar aviso si ya existe
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "No se añade la imagen porque ya está entre los objetos cargados."
                        )
                    }
                }
            } else {
                if (rewardImages.none { it.uri == uriString }) {
                    context.contentResolver.takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    rewardImages.add(RewardImage(uriString, rewardImages.size))
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Esta recompensa ya está en la lista."
                        )
                    }
                }
            }
        }
        save(scope, context, objectImages, rewardImages)
    }

    // 3. Scaffold para el host de Snackbar
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // HEADER
            Text(
                text = "Gestión de imágenes",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            TabRow(selectedTabIndex = if (isObjectMode) 0 else 1) {
                Tab(selected = isObjectMode, onClick = { isObjectMode = true }) {
                    Text("Objetos", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = !isObjectMode, onClick = { isObjectMode = false }) {
                    Text("Recompensas", modifier = Modifier.padding(16.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { launcher.launch(arrayOf("image/*")) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(if (isObjectMode) "Añadir objetos" else "Añadir recompensas")
            }

            Box(modifier = Modifier.weight(1f)) {
                if (isObjectMode) {
                    ObjectGrid(objectImages, scope, context, rewardImages)
                } else {
                    RewardListReorderable(
                        rewards = rewardImages
                    ) { updated ->
                        val normalized = normalizeOrder(updated)
                        rewardImages.clear()
                        rewardImages.addAll(normalized)
                        save(scope, context, objectImages, rewardImages)
                    }
                }
            }
        }
    }
}

@Composable
fun ObjectGrid(
    objectImages: MutableList<String>,
    scope: CoroutineScope,
    context: Context,
    rewardImages: List<RewardImage>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize() // Asegura que use todo el espacio del Box
    ) {
        items(objectImages) { uri ->
            ImageCard(
                uri = uri,
                label = null,
                onDelete = {
                    objectImages.remove(uri)
                    save(scope, context, objectImages, rewardImages)
                }
            )
        }
    }
}

@Composable
fun RewardListReorderable(
    rewards: MutableList<RewardImage>,
    onChange: (List<RewardImage>) -> Unit
) {
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

                val elevation by animateDpAsState(
                    if (isDragging) 8.dp else 0.dp,
                    label = ""
                )

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

                        // HANDLE DRAG
                        Box(
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                    }
                                }
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = "≡",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }

                        // IMAGE
                        AsyncImage(
                            model = item.uri,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // CONTROLES
                        Column {

                            Row {

                                // ↑
                                IconButton(onClick = {
                                    val index = rewards.indexOf(item)
                                    if (index > 0) {
                                        rewards.removeAt(index)
                                        rewards.add(index - 1, item)
                                        onChange(rewards.toList())
                                    }
                                }) {
                                    Text("↑")
                                }

                                // ↓
                                IconButton(onClick = {
                                    val index = rewards.indexOf(item)
                                    if (index < rewards.lastIndex) {
                                        rewards.removeAt(index)
                                        rewards.add(index + 1, item)
                                        onChange(rewards.toList())
                                    }
                                }) {
                                    Text("↓")
                                }
                            }

                            // ELIMINAR
                            Button(onClick = {
                                rewards.remove(item)
                                onChange(rewards.toList())
                            }) {
                                Text("Eliminar")
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

            // ORDEN (solo recompensas)
            label?.let {
                Text(
                    text = it,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // BOTÓN BORRAR
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text("X")
            }
        }
    }
}

fun normalizeOrder(list: List<RewardImage>): List<RewardImage> {
    return list.mapIndexed { index, item ->
        item.copy(order = index)
    }.toMutableList()
}

fun save(
    scope: CoroutineScope,
    context: Context,
    objects: List<String>,
    rewards: List<RewardImage>
) {
    scope.launch {
        ImageStorage.saveImages(context, objects, rewards)
    }
}
