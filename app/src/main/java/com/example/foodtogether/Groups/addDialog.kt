package com.example.foodtogether.Groups

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.math.abs

@Composable
fun addDialog(
    onDiss: ()->Unit,
    onSave: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDiss
    ) {
        var name by remember { mutableStateOf("") }
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(10.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Создать группу",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))


                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onDiss
                    ) {
                        Text("Отмена")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick ={
                            if (name.isNotEmpty()) {
                                onSave(name)
                            }
                        }
                    ) {
                        Text(
                            color = Color.White,
                           text= "Создать")
                    }
                }
            }
        }

    }
}
fun addNewGroup(name: String, userId:String, db: FirebaseFirestore){
    val user = hashMapOf(
        "groupName" to name
    )
        db.collection("user_group")
            .add(user)
            .addOnSuccessListener { documentReference ->
                val groupId = documentReference.id
                bindUserGroup(db, userId, groupId)
                Log.d("Firestore", "Группа успешно добавлена")

            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Ошибка добавления группы", e)
            }

}
fun bindUserGroup(db: FirebaseFirestore, userId: String, groupId:String){
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
