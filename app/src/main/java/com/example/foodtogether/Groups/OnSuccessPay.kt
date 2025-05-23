package com.example.foodtogether.Groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
fun OnSuccessPay (navController: NavController){
    Column (
        modifier = Modifier.fillMaxSize()
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ){
        Text(
            text = "Оплата прошла успешно!",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 24.sp,
        )

        Spacer(Modifier.height(24.dp))
        Text(
            textAlign = TextAlign.Center,
            text = "Скоро ваш заказ приедет, а пока можете ознакомится с ассортиментом товаров на главной странице",
            fontWeight = FontWeight.Light
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            navController.navigate("home/")
        }) {
            Text("Домой")
        }
    }
}