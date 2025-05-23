
package com.example.foodtogether.Groups

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodtogether.Groups.Products.Items
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

@Composable
fun Bascket(navController: NavController, groupId: String){
    val db = FirebaseFirestore.getInstance()
    val auth = Firebase.auth.currentUser?.uid ?: ""
    val textFields = remember { mutableStateListOf("") }
    val textFieldsPrice = remember { mutableStateListOf("") }
    val shop = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(22.dp)
            .clickable { focusManager.clearFocus() },

        ) {
            Text(
                text = "Корзина",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 24.sp,
            )
        getShop(db, groupId, onSucc ={
            shop.value = it
        } )
        Spacer(Modifier.height(24.dp))
        Row {
            Text("Магазин:")
            Spacer(Modifier.width(10.dp))
            Text(shop.value,
                modifier = Modifier.clickable {
                    navController.navigate("home/${shop.value}")
                },
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxHeight(0.9f)
        ) {
            itemsIndexed(textFields){index, value ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = value,
                        singleLine = true,
                        onValueChange = { textFields[index] = it },
                        shape = RoundedCornerShape(16.dp),
                        label = { Text("Название товара") },
                        modifier = Modifier.weight(2f) ,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF6F6F6),
                            unfocusedBorderColor = Color(0xFFF6F6F6),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFF6F6F6)
                        )
                    )
                    Spacer(Modifier.width(7.dp))
                    OutlinedTextField(
                        value = textFieldsPrice[index],
                        singleLine = true,

                        onValueChange = { textFieldsPrice[index] = it },
                        label = { Text("руб") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF6F6F6),
                            unfocusedBorderColor = Color(0xFFF6F6F6),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFF6F6F6)
                        )
                    )

                    if (index > 0) {
                        IconButton(
                            onClick = { textFields.removeAt(index)
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(Icons.Default.Delete, "Удалить")
                        }
                    }
                }
            }
        item {
        Spacer(Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                  containerColor = Color.White,
                 contentColor = MaterialTheme.colorScheme.primary),
            onClick = { textFields.add("")
                focusManager.clearFocus()

                textFieldsPrice.add("")},
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = MaterialTheme.colorScheme.primary, fontSize = 24.sp)
                }

                Spacer(Modifier.width(20.dp))
                Text(
                    "Добавить новую группу",
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(modifier = Modifier.fillMaxWidth(),
            onClick = {
                focusManager.clearFocus()

                val fullTextField = mutableListOf<Items>()
                textFields.forEachIndexed { index, s ->
                    val item = Items(s, textFieldsPrice[index].toDoubleOrNull() ?: 0.0)
                    fullTextField.add(item)
                }
                addProductList(fullTextField, db, auth, groupId)
                navController.navigate("pay/${groupId}")
            }) {
            Text("Далее")
        }
    }
}

fun addProductList(textFields: MutableList<Items>, db: FirebaseFirestore, uid: String, groupId: String){
    if(uid != "" ){
        val prodText = textFields.mapNotNull { prod ->
            hashMapOf(
                "products" to prod.prodName,
                "price" to prod.prodPrice
            )
        }
        val prodTextDoc = hashMapOf(
            "products" to prodText
        )
            db.collection("users_location")
                .document(uid)
                .set(prodTextDoc, SetOptions.merge())
                .addOnSuccessListener {
                    prodText.forEach {
                        Log.d("Firestore", "Продукты добавлены пользователю ${it}")
                    }
                }
                .addOnFailureListener {
                    Log.d("Firestore", "Ошибка добавления продуктов пользователю", it)
                }
        textFields.forEach {it->
            db.collection("user_group")
                .document(groupId)
                .update("ActiveProdList", FieldValue.arrayUnion(it.prodName))
                .addOnSuccessListener {
                    Log.d("Firestore", "Продукты добавлены  в группу ")
                }
               .addOnFailureListener {
                Log.d("Firestore", "Ошибка добавления продуктов в группу", it)
            }
               }
        }
    }
fun getShop(db:FirebaseFirestore, groupId:String, onSucc:(String)->Unit){
    if(groupId!=""){
        db.collection("user_group")
            .document(groupId)
            .get()
            .addOnSuccessListener {doc->
                val shop = doc.getString("selectedShop").toString()
                onSucc(shop)
                Log.d("Firestore", "Значение магазина получено ${shop}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Ошибка получения магазина", e)
            }
    }
}
