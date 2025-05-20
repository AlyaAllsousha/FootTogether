package com.example.foodtogether.Groups

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

@Composable
fun Bascket(navController: NavController){
    val db = FirebaseFirestore.getInstance()
    val auth = Firebase.auth.currentUser?.uid ?: ""
    val textFields = remember { mutableStateListOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(22.dp)
    ) {
        Text(
            text = "Корзина",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 24.sp,
        )
        Spacer(Modifier.height(24.dp))
        textFields.forEachIndexed { index, value ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { textFields[index] = it },
                    label = { Text("Название товара") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )

                if (index > 0) {
                    IconButton(
                        onClick = { textFields.removeAt(index) }
                    ) {
                        Icon(Icons.Default.Delete, "Удалить")
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

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
                onClick = { textFields.add("")}
            ) {

                Text(modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "+")
            }
            Text(
                modifier = Modifier.padding(10.dp, 0.dp)
                    .clickable { textFields.add("") },
                text = "Добавить товар"
            )
        }
        Spacer(Modifier.height(24.dp))
        Button(modifier = Modifier.fillMaxWidth(),
            onClick = {}) {
            Text("Далее",
                color = Color.White)
        }
    }


}