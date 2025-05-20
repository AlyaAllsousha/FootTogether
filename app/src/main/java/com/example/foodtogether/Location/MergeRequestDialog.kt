package com.example.foodtogether.Location

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.ktx.Firebase

@Composable
fun MergeRequestDialog(
    show: Boolean,
    user: String?,
    userName: String?,
    locationRef: CollectionReference,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (show && user != null) {
        val auth = Firebase.auth.currentUser
        if(auth?.uid  != user) {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Объединиться?") },
                text = { Text("Вы хотите объединиться пользователем ${userName}?") },
                confirmButton = {
                    Button(
                        onClick = onConfirm,
                    ) {
                        Text("Да")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = onDismiss,
                    ) {
                        Text("Нет")
                    }
                }
            )
        }
        else{
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Это вы\uD83D\uDE0A") },
                text = { Text("Всё еще вы") },
                confirmButton = {
                    Button(
                        onClick = onConfirm,
                    ) {
                        Text("Да")
                    }
                }
            )
        }
    }
}