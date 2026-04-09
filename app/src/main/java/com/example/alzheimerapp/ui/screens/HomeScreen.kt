package com.example.alzheimerapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    rewardImages: MutableList<RewardImage>,
    hasDifferenceLevel: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Menú Principal", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(48.dp))

        // Opción principal de Emparejar
        Button(
            onClick = { navController.navigate(Screen.MatchSelection.route) },
            modifier = Modifier.fillMaxWidth().height(100.dp)
        ) {
            Text("Emparejar", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Juego de Diferencias
        Button(
            onClick = { navController.navigate(Screen.Differences.route) },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            enabled = hasDifferenceLevel,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Juego de Diferencias")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Crear diferencias
        Button(
            onClick = { navController.navigate("difference_creator") },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("Crear diferencias")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Gestionar imágenes
        Button(
            onClick = { navController.navigate(Screen.ImageManager.route) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("Gestionar imágenes")
        }
    }
}
