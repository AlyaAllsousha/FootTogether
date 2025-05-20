package com.example.foodtogether.Groups

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.Flow

class GroupsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val groupRef = db.collection("user_group")
    private val locationRef = db.collection("users_location")

    private val _groupsState = MutableStateFlow<GroupsState>(GroupsState.Loading)
    val groupsState: StateFlow<GroupsState> = _groupsState.asStateFlow()

    sealed class GroupsState {
        object Loading : GroupsState()
        data class Success(val groups: List<GroupsData>) : GroupsState()
        data class Error(val message: String) : GroupsState()
    }
    
    fun loadUserGroups(userId: String?) {
        if (userId == null) {
            _groupsState.value = GroupsState.Error("User ID is null")
            return
        }

        viewModelScope.launch {
            _groupsState.value = GroupsState.Loading
            try {
                val groupIds = getGroupIds(userId)
                val groups = loadGroupsInfo(groupIds)
                _groupsState.value = GroupsState.Success(groups)
            } catch (e: Exception) {
                _groupsState.value = GroupsState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun getGroupIds(userId: String): List<String> {
        val document = locationRef.document(userId).get().await()
        return when (val field = document.get("groupId")) {
            is List<*> -> field.filterIsInstance<String>()
            is String -> listOf(field)
            else -> emptyList()
        }.filter { it != "0" && it.isNotEmpty() }
    }

    private suspend fun loadGroupsInfo(groupIds: List<String>): List<GroupsData> {
        return groupIds.mapNotNull { groupId ->
            try {
                val doc = groupRef.document(groupId).get().await()
                GroupsData(
                    groupId = groupId,
                    groupName = doc.getString("groupName") ?: "Unnamed Group"
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}