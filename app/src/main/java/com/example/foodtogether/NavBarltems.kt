package com.example.foodtogether

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart

object NavBarItems {
    val BarItems = listOf(
        BarItem(
            title = "Главная",
            image = Icons.Filled.Home,
            route = "home"
        ),
        BarItem(
            title = "Карта",
            image = Icons.Filled.LocationOn,
            route = "contacts"
        ),
        BarItem(
            title = "Корзина",
            image = Icons.Filled.ShoppingCart,
            route = "about"
        )
    )
}