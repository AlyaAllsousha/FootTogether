package com.example.foodtogether.Groups.Products

data class Products(
    var userId: String = "",
    var prodList: List<Items> = listOf(),
    var userName: String = ""
)
