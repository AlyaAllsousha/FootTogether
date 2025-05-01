package com.example.foodtogether

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Contacts : NavRoutes("contacts")
    object About : NavRoutes("about")
}