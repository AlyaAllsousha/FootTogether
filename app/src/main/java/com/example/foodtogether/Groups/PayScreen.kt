package com.example.foodtogether.Groups

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodtogether.Groups.Products.ProductList
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

@Composable
fun PayScreen(
    navController: NavController,
    groupId: String,
    viewModel: ProductList = viewModel(),

    ) {
    val db = FirebaseFirestore.getInstance()
    val uid = Firebase.auth.currentUser?.uid ?: ""
    val productsState by viewModel.productsState.collectAsState()
    val userId = remember { mutableStateOf(uid) }
    val lastUi = remember { mutableStateOf("") }
    var userSum = 0.0
    var commonSum = 0.0
    if (userId.value != lastUi.value && lastUi.value != "") {
        navController.navigate("about") {
            popUpTo("about")
        }
    }
    LaunchedEffect(Unit) {
        viewModel.setupGroupListener(groupId, uid)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp)
    ) {
        Text(
            text = "Оплата",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 24.sp,
        )

        Spacer(Modifier.height(16.dp))

        // Состояние загрузки/отображения групп
        when (productsState) {
            is ProductList.ProductState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            is ProductList.ProductState.Error -> {
                Text(
                    text = "Ошибка загрузки групп",
                    color = MaterialTheme.colorScheme.error
                )
            }

            is ProductList.ProductState.Success -> {
                lastUi.value = uid
                val products = (productsState as ProductList.ProductState.Success).products
                if (products.isEmpty()) {
                    Text("Нет товаров для оплаты")
                } else {

                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(0.9f),
                    ) {
                        items(products) { group ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(22.dp)
                                ) {
                                    // Заголовок с именем пользователя
                                    Text(
                                        style = MaterialTheme.typography.titleLarge,
                                        text = group.userName,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Log.d(
                                        "Firestore",
                                        "PayScreen: у пользователя ${group.userName}"
                                    )

                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.1f
                                        )
                                    )

                                    // Список продуктов
                                    Column(
                                        modifier = Modifier.padding(top = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (group.prodList.isNullOrEmpty()) {
                                            Text(
                                                text = "Товаров нет"
                                            )
                                        } else {
                                            group.prodList.forEach { prod ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Text(
                                                        text = prod.prodName,
                                                    )

                                                    Text(
                                                        text = prod.prodPrice.toString(),
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    userSum = userSum + prod.prodPrice
                                                    commonSum += prod.prodPrice
                                                }
                                            }


                                        }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "Итог",
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                "${userSum}₽",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        userSum = 0.0
                                    }
                                }
                            }
                            Spacer(Modifier.height(24.dp))
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Общая сумма",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${commonSum}₽",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onPayment(db, groupId)
                            navController.navigate("paySucc") {
                                popUpTo("paySucc") { inclusive = true }
                            }
                        }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "корзина",

                            )

                        Text(
                            "Оплатить",
                            Modifier.padding(start = 10.dp)
                        )

                    }
                }
            }
        }
    }
}

fun onPayment(db: FirebaseFirestore, groupId: String) {
    val productList = hashMapOf(
        "ActiveProdList" to FieldValue.delete()
    )
    db.collection("user_group")
        .document(groupId)
        .update(productList as Map<String, Any>)
        .addOnSuccessListener {
            Log.d("Firestore", "onPayment: success")
        }
        .addOnFailureListener {
            Log.d("Firestore", "onPayment: error", it)
        }

}
