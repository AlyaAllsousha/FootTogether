package com.example.foodtogether.loginSignup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
 fun AuthScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Логин",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Для начала использования приложения, пожалуйста, войдите или зарегистрируйтесь",
            fontSize = 24.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 24.sp,
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo("contacts") { inclusive = false }
                }
            },
            modifier = Modifier.size(210.dp, 50.dp)
        ) {
            Text("Войти")
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                navController.navigate("signup") {
                    popUpTo("contacts") { inclusive = false }
                }
            },
            modifier = Modifier.size(210.dp, 50.dp)
        ) {
            Text("Зарегистрироваться")
        }
    }
}