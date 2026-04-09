package com.example.alzheimerapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alzheimerapp.navigation.Screen

@Composable
fun MatchSelectionScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Selecciona Modo de Juego", fontSize = 28.sp)

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { navController.navigate(Screen.Recognition.route) },
            modifier = Modifier.fillMaxWidth().height(100.dp)
        ) {
            Text("Emparejar Visible", fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate(Screen.Match.route) },
            modifier = Modifier.fillMaxWidth().height(100.dp)
        ) {
            Text("Emparejar Memory", fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(48.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }
    }
}
