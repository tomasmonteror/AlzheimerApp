package com.example.alzheimerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.alzheimerapp.data.ImageStorage
import com.example.alzheimerapp.data.RewardImage
import com.example.alzheimerapp.navigation.Screen
import com.example.alzheimerapp.ui.screens.DifferencesScreen
import com.example.alzheimerapp.ui.screens.HomeScreen
import com.example.alzheimerapp.ui.screens.ImageManagerScreen
import com.example.alzheimerapp.ui.screens.MatchScreen
import com.example.alzheimerapp.ui.screens.RecognitionMatchScreen
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
                            HomeScreen(navController, objectImages, rewardImages)
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
                        composable(Screen.Differences.route) {
                            DifferencesScreen()
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
