package com.example.foodtogether.Location

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodtogether.Groups.GroupsViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

@Composable
fun MergeRequestDialog(
    show: Boolean,
    user: String?,
    userName: String?,
    onDismiss: () -> Unit,
    db: FirebaseFirestore,
    onConfirm: (String) -> Unit,
    viewModel: GroupsViewModel = viewModel(),

    ) {
    if (show && user != null) {
        val auth = Firebase.auth.currentUser
        if(auth?.uid  != user) {

            val groupsState by viewModel.groupsState.collectAsState()
            var selectedOption by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                viewModel.loadUserGroups(auth?.uid)
            }

            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Объединиться c ${userName}?") },
                text = {
                    when (groupsState) {
                        is GroupsViewModel.GroupsState.Loading -> {
                            Text("Загрузка...")
                        }
                        is GroupsViewModel.GroupsState.Error -> {
                            Text(
                                text = "Ошибка загрузки групп",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        is GroupsViewModel.GroupsState.Success -> {
                            val options = (groupsState as GroupsViewModel.GroupsState.Success).groups
                            Column {
                                options.forEach { option ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedOption = option.groupId }
                                    ) {
                                        RadioButton(
                                            selected = (option.groupId == selectedOption),
                                            onClick = { selectedOption = option.groupId }
                                        )
                                        Text(
                                            text = option.groupName,
                                        )

                                    }
                                }
                            }
                        }
                        }

                },
                confirmButton = {
                    Button(

                        onClick = {
                            if(selectedOption != ""){
                                onConfirm(selectedOption)
                            }
                        },
                    ) {
                        Text("Да",
                            color = Color.White
                        )
                    }
                },
                dismissButton = {
                    TextButton (
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
                        onClick = { onConfirm("") },
                    ) {
                        Text("Да")
                    }
                }
            )
        }
    }
}

fun AddSelectedInGroup(userId: String, db:FirebaseFirestore, groupId: String){
    db.collection("users_location")
        .document(userId)
        .update("groupId", FieldValue.arrayUnion(groupId))
        .addOnSuccessListener {
            Log.d("Firestore", "Группа успешно добавлена к пользователю")
        }
        .addOnFailureListener {
            Log.d("Firestore", "Ошибка добавления группы к пользователю", it)
        }


}