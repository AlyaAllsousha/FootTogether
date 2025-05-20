package com.example.foodtogether

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.foodtogether.loginSignup.LogIn
import com.example.foodtogether.loginSignup.SignUp
import com.example.foodtogether.shops.shops
import com.example.foodtogether.ui.theme.FoodTogetherTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import kotlin.math.abs


@Composable


fun Home(  navController: NavController?) {
    val auth = Firebase.auth
    var name = remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    val email = auth.currentUser?.email ?: ""

    LaunchedEffect(Unit) {
        if (userId != "") {
            val user = hashMapOf(
                "userId" to userId,
                "email" to email,
                "position" to GeoPoint(0.0, 0.0),
                "timestamp" to FieldValue.serverTimestamp(),
            )
            db.collection("users_location")
                .document(userId)
                .set(user, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("Firestore", "Документ успешно добавлен")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Ошибка добавления документа", e)
                }
        }
    }
    if (auth.currentUser == null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
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
                    navController?.navigate("login") {
                        popUpTo("home") { inclusive = false } // Очистка стека навигации
                    }
                },
                modifier = Modifier.size(210.dp, 50.dp)
            ) {
                Text("Войти")
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    navController?.navigate("signup") {
                        popUpTo("home") { inclusive = false } // Очистка стека навигации
                    }
                },
                modifier = Modifier.size(210.dp, 50.dp)
            ) {

                Text("Зарегестрироваться")
            }
        }
    } else {
            getName(auth.currentUser!!.uid, db, onSucc = {
                name.value = it
            })



        Column(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.secondary)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Log.d("nameAtHome =", name.value)
                Text(text = name.value)
                Button(onClick = {
                    auth.signOut()
                    navController?.navigate("home") {
                        popUpTo("home") { inclusive = false } // Очистка стека навигации
                    }
                }) {
                    Text(text = "Выйти")
                }
            }
            shops()
        }

    }
}
     fun getName(userId: String, db: FirebaseFirestore, onSucc: (String) -> Unit) {
        val nameRef = db.collection("user_names")
        nameRef.document(userId)
            .get()
            .addOnSuccessListener { doc ->
                onSucc(doc.getString("name").toString())
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting name", e)
            }
    }

