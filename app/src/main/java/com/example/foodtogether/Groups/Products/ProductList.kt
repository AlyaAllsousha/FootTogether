package com.example.foodtogether.Groups.Products

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProductList: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val groupRef = db.collection("user_group")
    private var snapshotListener: ListenerRegistration? = null
    private val locationRef = db.collection("users_location")

    private val _productsState = MutableStateFlow<ProductState>(ProductState.Loading)
    val productsState: StateFlow<ProductState> = _productsState.asStateFlow()

    sealed class ProductState {
        object Loading : ProductState()
        data class Success(val products: List<Products>) : ProductState()
        data class Error(val message: String) : ProductState()
    }

    fun setupGroupListener(groupId: String, currentUserId: String) {
        // Очищаем предыдущий слушатель
        snapshotListener?.remove()
        snapshotListener = groupRef.document(groupId)
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    _productsState.value = ProductState.Error(it.message ?: "Unknown error")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val userIds = when (val field = snapshot.get("ActiveUsers")) {
                        is List<*> -> field.filterIsInstance<String>()
                        is String -> listOf(field)
                        else -> emptyList()
                    }.filter { it != "0" && it.isNotEmpty() }
                    loadProductsForUsers(userIds)
                } else {
                    _productsState.value = ProductState.Success(emptyList())
                }
            }
    }


private fun loadProductsForUsers(userIds: List<String>) {
    viewModelScope.launch {
        _productsState.value = ProductState.Loading
        try {
            val products = userIds.mapNotNull { userId ->
                val doc = locationRef.document(userId).get().await()
                val name = doc.get("name").toString()
                var prodList = when (val field = doc.get("products")) {
                    is List<*> -> field.filterIsInstance<HashMap<String, Any>>()
                        .mapNotNull { map ->
                            // Проверяем структуру и типы данных
                            when {
                                map["products"] is String && map["price"] is Double && map["products"] != ""-> {
                                    Items(
                                        prodName = map["products"] as String,
                                        prodPrice = map["price"] as Double
                                    )
                                }
                                map["products"] is String && map["price"] is Int && map["products"] != ""-> {
                                    // Если price пришел как Int - конвертируем в Double
                                    Items(
                                        prodName = map["products"] as String,
                                        prodPrice = (map["price"] as Int).toDouble()
                                    )
                                }
                                else -> null // Пропускаем некорректные элементы
                            }
                        }
                    else -> emptyList()
                }
                    .filter { it != null }
                Products(userId = userId, prodList = prodList, userName = name)
            }
            _productsState.value = ProductState.Success(products)
        } catch (e: Exception) {
            _productsState.value = ProductState.Error(e.message ?: "Unknown error")
        }
    }
}

override fun onCleared() {
    super.onCleared()
    snapshotListener?.remove()
}
}