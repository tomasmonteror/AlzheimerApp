package com.example.alzheimerapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alzheimerapp.data.RewardImage
import com.example.alzheimerapp.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavController,
    objectImages: MutableList<String>,
    rewardImages: MutableList<RewardImage>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Menú Principal", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate(Screen.Recognition.route) },
            modifier = Modifier.fillMaxWidth().height(100.dp)
        ) {
            Text("Emparejar-visible", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate(Screen.Match.route) },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            enabled = true
        ) {
            Text("Emparejar-memory", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate(Screen.ImageManager.route) },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Gestionar imágenes")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate(Screen.Differences.route) },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Juego de Diferencias")
        }
    }
}