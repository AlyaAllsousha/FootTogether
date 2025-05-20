package com.example.foodtogether

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodtogether.Location.Contacts
import com.example.foodtogether.Groups.About
import com.example.foodtogether.loginSignup.LogIn
import com.example.foodtogether.loginSignup.SignUp
import com.example.foodtogether.ui.theme.FoodTogetherTheme
import com.yandex.mapkit.MapKitFactory

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("8b81bd28-ac43-4df2-9d03-8d76eaffd5b3")
        MapKitFactory.initialize(this)
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

        NavHost(navController, startDestination = NavRoutes.Home.route, modifier = Modifier.weight(1f)) {
            composable(NavRoutes.Home.route) { Home( navController)}
            composable(NavRoutes.Contacts.route) { Contacts(navController)  }
            composable(NavRoutes.About.route) { About(navController) }
            composable(NavRoutes.Login.route) { LogIn(navController) }
            composable(NavRoutes.SignUp.route) { SignUp(navController) }

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
