package com.example.foodtogether.Location

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.foodtogether.loginSignup.AuthScreen
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



    @Composable
    fun Contacts(navController: NavController) {
        val context = LocalContext.current
        val db = Firebase.firestore
        val auth = Firebase.auth.currentUser
        val viewModel = LocationViewModel()
        // Для запроса разрешений
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                navController.navigate("contacts")
            }
        }

        // Проверка разрешений
        val hasLocationPermission = remember {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

        if (auth != null) {
            if (hasLocationPermission) {
                // Основной контент с доступом к местоположению
                ContactsContent(viewModel, auth, db, navController)
            } else {
                // Экран с запросом разрешений
                PermissionRequestScreen {
                    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        } else {
            // Экран авторизации
            AuthScreen(navController)
        }
    }

    @Composable
    fun ContactsContent(
        viewModel: LocationViewModel,
        auth: FirebaseUser,
        db: FirebaseFirestore,
        navController: NavController

    ) {
        val email = auth.email ?: ""
        val userId = auth.uid
        val state by viewModel.state.collectAsState()
        lateinit var locationClient: FusedLocationProviderClient

        @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
        @Composable
        @SuppressLint("MissingPermission")
         fun initUpdates(viewModel: LocationViewModel) {
            locationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current);
            if (ActivityCompat.checkSelfPermission(
                    LocalContext.current,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    LocalContext.current,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {}
            locationClient.requestLocationUpdates(
                LocationRequest.Builder(5000).build(),
                {location -> viewModel.update(location.latitude, location.longitude)},
                Looper.getMainLooper()
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            initUpdates(viewModel)
            YandexMapScreen(state.latitude, state.longitude)
            LaunchedEffect(state) {

                val user = hashMapOf(
                    "userId" to userId,
                    "email" to email,
                    "position" to GeoPoint(state.latitude, state.longitude),
                    "timestamp" to FieldValue.serverTimestamp()
                )

                db.collection("users_location")
                    .document(userId)
                    .set(user)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Документ успешно добавлен")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Ошибка добавления документа", e)
                    }
            }

        }
    }

    @Composable
    private fun PermissionRequestScreen(onRequestPermission: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Для работы с картой необходимо разрешение на доступ к местоположению",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )

            Button(onClick = onRequestPermission) {
                Text("Предоставить разрешение")
            }
        }
    }


