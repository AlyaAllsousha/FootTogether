package com.example.foodtogether.Location

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

fun GroupUsers(
    selectedUser:String?,
    locationsRef: CollectionReference,
    groupsRef:CollectionReference
){
    var groupId= "";
    val auth = Firebase.auth.currentUser

}