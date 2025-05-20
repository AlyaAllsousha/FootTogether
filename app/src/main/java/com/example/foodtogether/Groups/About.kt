package com.example.foodtogether.Groups

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodtogether.loginSignup.AuthScreen
import com.example.foodtogether.ui.theme.White
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.toList
import kotlin.math.log

@Composable
fun About(navController: NavController) {
    val auth = Firebase.auth.currentUser
    val selectedGroup = remember { mutableStateOf("") }
    if(auth == null){
        AuthScreen(navController)
    }
    else {
        if(selectedGroup.value==""){
            SelectGroup(auth.uid)
        }
    }
}

@Composable
fun SelectGroup(
    userId: String?,
    viewModel: GroupsViewModel = viewModel(),
) {
    val groupsState by viewModel.groupsState.collectAsState()
    val db = Firebase.firestore
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadUserGroups(userId)

    }
    if (showDialog) {

        addDialog(onDiss = {showDialog = false},
            onSave = { groupName ->
                addNewGroup(groupName, userId!!, db)
                showDialog = false
            }
            )

    }

    DisposableEffect(showDialog) {
        if (!showDialog && userId != null) {
            Log.d("Firestore", "Начало обновления данных")

            viewModel.loadUserGroups(userId)
            Log.d("Firestore", "Конец обновления данных")

        }
        onDispose { }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp)
    ) {
        Text(
            text = "Группа",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 24.sp,
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Выберите, в какой группе вы будете создавать заказ, или добавьте новую",
            fontWeight = FontWeight.Light
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier.size(45.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary),
                onClick = {showDialog = true}
            ) {

                Text(modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "+")
            }
            Text(
                modifier = Modifier.padding(10.dp, 0.dp),
                text = "Добавить новую группу"
            )
        }

        Spacer(Modifier.height(16.dp))

        // Состояние загрузки/отображения групп
        when (groupsState) {
            is GroupsViewModel.GroupsState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            is GroupsViewModel.GroupsState.Error -> {
                Text(
                    text = "Ошибка загрузки групп",
                    color = MaterialTheme.colorScheme.error
                )
            }
            is GroupsViewModel.GroupsState.Success -> {
                val groups = (groupsState as GroupsViewModel.GroupsState.Success).groups
                if (groups.isEmpty()) {
                    Text("У вас нет доступных групп")
                } else {
                    LazyColumn {
                        items(groups) { group ->
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                elevation = ButtonDefaults.buttonElevation(4.dp),
                                onClick = {}

                            ) {
                                Text(modifier = Modifier.fillMaxWidth().padding(5.dp, 10.dp),
                                    textAlign = TextAlign.Left,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Normal,
                                    text = group.groupName)
                            }
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}




