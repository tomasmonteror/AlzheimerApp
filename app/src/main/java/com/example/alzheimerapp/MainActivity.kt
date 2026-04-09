package com.example.alzheimerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.alzheimerapp.data.ImageStorage
import com.example.alzheimerapp.data.RewardImage
import com.example.alzheimerapp.model.DifferenceLevel
import com.example.alzheimerapp.navigation.Screen
import com.example.alzheimerapp.ui.screens.*
import com.example.alzheimerapp.ui.theme.AlzheimerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlzheimerAppTheme {

                val context = this
                val navController = rememberNavController()

                val objectImages = remember { mutableStateListOf<String>() }
                val rewardImages = remember { mutableStateListOf<RewardImage>() }

                // Lista persistente de niveles de diferencias para la sesión actual
                val savedDifferenceLevels = remember { mutableStateListOf<DifferenceLevel>() }

                LaunchedEffect(Unit) {
                    objectImages.clear()
                    objectImages.addAll(ImageStorage.loadObjects(context))

                    rewardImages.clear()
                    rewardImages.addAll(ImageStorage.loadRewards(context))
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        composable(Screen.Home.route) {
                            HomeScreen(
                                navController = navController,
                                objectImages = objectImages,
                                rewardImages = rewardImages,
                                hasDifferenceLevel = savedDifferenceLevels.isNotEmpty()
                            )
                        }

                        // Selección de Modo (Visible vs Memory)
                        composable(Screen.MatchSelection.route) {
                            MatchSelectionScreen(navController)
                        }

                        composable(Screen.Recognition.route) {
                            RecognitionMatchScreen(
                                objectImages = objectImages,
                                rewardImages = rewardImages,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Match.route) {
                            MatchScreen(
                                objectImages = objectImages,
                                rewardImages = rewardImages,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // Flujo de creación incremental de niveles de diferencias
                        composable("difference_creator") {
                            var creationStep by remember { mutableStateOf("config") }
                            var tempImages by remember { mutableStateOf<Pair<String, String>?>(null) }

                            when (creationStep) {
                                "config" -> {
                                    CreateDifferenceLevelScreen(
                                        onNavigateToEditor = { original, modified ->
                                            tempImages = original to modified
                                            creationStep = "editor"
                                        }
                                    )
                                }
                                "editor" -> {
                                    DifferenceEditorScreen(
                                        imageLeft = tempImages!!.first,
                                        imageRight = tempImages!!.second,
                                        onSave = { level ->
                                            savedDifferenceLevels.add(level)
                                            creationStep = "ask_more"
                                        }
                                    )
                                }
                                "ask_more" -> {
                                    AlertDialog(
                                        onDismissRequest = { },
                                        title = { Text("Nivel Guardado") },
                                        text = { Text("¿Deseas añadir otro nivel con una nueva pareja de imágenes?") },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                tempImages = null
                                                creationStep = "config"
                                            }) { Text("Sí, añadir otro") }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = {
                                                navController.popBackStack(Screen.Home.route, false)
                                            }) { Text("No, finalizar") }
                                        }
                                    )
                                }
                            }
                        }

                        // Juego secuencial de niveles
                        composable(Screen.Differences.route) {
                            if (savedDifferenceLevels.isNotEmpty()) {
                                DifferencesScreen(
                                    levels = savedDifferenceLevels,
                                    onBack = { navController.popBackStack() }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.popBackStack()
                                }
                            }
                        }

                        composable(Screen.ImageManager.route) {
                            ImageManagerScreen(objectImages, rewardImages)
                        }
                    }
                }
            }
        }
    }
}