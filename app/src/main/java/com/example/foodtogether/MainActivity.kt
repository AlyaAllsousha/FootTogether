package com.example.foodtogether

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Text


import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodtogether.ui.theme.FoodTogetherTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodTogetherTheme(darkTheme = isSystemInDarkTheme()) {
                Main()
            }

        }
    }
}
@Composable
fun Main() {
    val navController = rememberNavController()
    Column() {
        Button(onClick = {}) {
            Text(text ="Text")
        }
        NavHost(navController, startDestination = NavRoutes.Home.route, modifier = Modifier.weight(1f)) {
            composable(NavRoutes.Home.route) { Home() }
            composable(NavRoutes.Contacts.route) { Contacts()  }
            composable(NavRoutes.About.route) { About() }
        }
        BottomNavigationBar(navController = navController)
    }
}
@Preview(showBackground = true)
@Composable
fun LightPreview() {
    FoodTogetherTheme(darkTheme = false) {
        Main()
    }
}

@Preview(showBackground = true)
@Composable
fun DarkPreview() {
    FoodTogetherTheme(darkTheme = true) {
        Main()
    }
}
