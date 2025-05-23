package com.example.foodtogether.loginSignup

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SignUp(navController: NavController){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password_rep by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val auth = Firebase.auth

    val db = FirebaseFirestore.getInstance()
    val nameRef = db.collection("user_names")

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .clickable { focusManager.clearFocus() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Логотип
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Логин",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text ="Регистрация"
        )
        Spacer(Modifier.height(24.dp))

        // Поле email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(Modifier.height(16.dp))

        // Поле имя
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя пользователя") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(Modifier.height(16.dp))

        // Поле пароля
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(Modifier.height(16.dp))

        //поле проверки пароля
        OutlinedTextField(
            value = password_rep,
            onValueChange = { password_rep = it },
            label = { Text("Повторите пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(24.dp))

        // Кнопка входа
        Button(
            onClick = {
                focusManager.clearFocus()
                if (email.isBlank() || password.isBlank() || name.isBlank()) {
                    error = "Заполните все поля"
                }
                else if(password_rep != password){
                    error = "Пароли не совпадают"
                }
                else {

                    isLoading = true
                    error = null
                    if (email.contains("@") && password.length >= 6) {
                        SignUpFun(
                            auth = auth,
                            email = email,
                            password = password,
                            name = name,
                            onSuccess = {
                                val user = hashMapOf(
                                    "userId" to auth.currentUser?.uid,
                                    "name" to name
                                )
                                val userName= hashMapOf(
                                        "name" to name
                                    )
                                   nameRef
                                        .document(auth.currentUser!!.uid)
                                        .set(user)
                                        .addOnSuccessListener {
                                            Log.d("Firestore SignUp", "имя успешно добавлен")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("Firestore", "Ошибка добавления имени", e)
                                        }
                                db.collection("users_location")
                                    .document(auth.currentUser!!.uid)
                                    .set(userName, SetOptions.merge())
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "имя успешно добавлено пользователю")
                                    }
                                    .addOnFailureListener {
                                        Log.d("Firestore", "Ошибка добавления имени к пользователю", it)
                                    }

                                // Только при успехе переходим на home
                                navController.navigate("home/") {
                                    popUpTo("signup") { inclusive = true }
                                }
                            },
                            onError = { errorMessage ->
                                isLoading = false
                                error = errorMessage
                            }
                        )

                    } else {
                        isLoading = false
                        error = "Неверный email или пароль"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {

                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)

                )
            } else {
                Text("Зарегестрироваться")
            }
        }

        // Отображение ошибки
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
private fun SignUpFun(
    auth: FirebaseAuth,
    email: String,
    password: String,
    name: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess() // Успешная регистрация

            } else {
                // Получаем понятное сообщение об ошибке
                val errorMsg = task.exception?.message ?: "Неизвестная ошибка регистрации"
                onError(errorMsg)
            }
        }
}



