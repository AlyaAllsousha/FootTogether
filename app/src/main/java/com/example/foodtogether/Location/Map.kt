package com.example.foodtogether.Location

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.foodtogether.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun YandexMapScreen(latitude: Double, longitude: Double) {
    val context = LocalContext.current
    val auth = Firebase.auth.currentUser
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val lat = remember { mutableStateOf<Double>(0.0) }
    val longt = remember { mutableStateOf<Double>(0.0) }
    var newLat = latitude
    var newLong = longitude
    val db = FirebaseFirestore.getInstance()
    val locationsRef = db.collection("users_location")
    var snapshotListener: ListenerRegistration? = null
    val markerCollection = remember { mutableStateOf<MapObjectCollection?>(null) }
    val markers = remember { mutableStateMapOf<Point, PlacemarkMapObject>() }

    LaunchedEffect(Unit) {
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()
        MapKitFactory.initialize(context)  // Инициализация
    }

    //Местоположение остальных пользователей
    fun listenToUsersLocations(
        onUpdate: (List<Pair<String, GeoPoint>>) -> Unit,
        onError: (Exception) -> Unit = { Log.e("Firestore", "Error", it) }
    ): ListenerRegistration {
        snapshotListener = locationsRef.addSnapshotListener { snapshot, error ->
            error?.let(onError) ?: run {
                val locations = snapshot?.documents
                    ?.mapNotNull { doc ->
                        doc.getGeoPoint("position")?.let { doc.id to it }
                    } ?: emptyList()
                onUpdate(locations)
            }
        }
        return snapshotListener!!
    }

    fun cancelListening() {
        snapshotListener?.remove()
    }
    //Добавить маркер
    fun addMarker(mapView: MapView, point: Point,img: ImageProvider, user: String?) {
        if (markerCollection.value == null) {
            markerCollection.value = mapView.map.mapObjects.addCollection()
        }
        markers[point]?.let { oldMarker ->
            markerCollection.value?.remove(oldMarker)
        }
        val placemark = markerCollection.value?.addPlacemark(point).apply {
            this?.setIcon(img)
            this?.addTapListener { _, _ ->
                Log.d("MapClick", "Маркер кликнут: (${point.latitude}, ${point.longitude}, $user)")
                true
            }
        }

        // Сохраняем ссылку
        placemark?.let { markers[point] = it }

    }

    //Карта

    Column(
        modifier = Modifier.fillMaxWidth().height(500.dp)
    ) {

        AndroidView(
            factory = { ctx ->
                MapView(ctx).also {
                    mapView = it
                    val point = Point(newLat, newLong)
                    mapView!!.map.move(
                        CameraPosition(point, 15f, 0f, 0f)
                    )
                    // Создаем коллекцию объектов
                    addMarker(it, point, ImageProvider.fromResource(context, R.drawable.location_on_black), auth?.uid)
                }
            },
            // МЯУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУУ
            modifier = Modifier.fillMaxWidth(),
            update = { view ->
                val point = Point(newLat, newLong)
                if (abs(lat.value - newLat) > 0.01 || abs(longt.value - newLong) > 0.01) {
                    lat.value = newLat
                    longt.value = newLong
                    mapView!!.map.move(
                        CameraPosition(point, 15f, 0f, 0f)
                    )
                    // Очищаем старые маркеры
                    markers.keys.removeAll { it != point }
                    markerCollection.value?.clear()
                    addMarker(
                        view,
                        point,
                        ImageProvider.fromResource(context, R.drawable.location_on_black),
                        auth?.uid
                    )                }


                //Пользователи вокруг
                listenToUsersLocations(
                    onUpdate = { locations ->
                        locations.forEach { (userId, geoPoint) ->
                            Log.d("Users", "${userId}, ${geoPoint.latitude}, ${geoPoint.longitude}")
                            val point = Point(geoPoint.latitude, geoPoint.longitude)
                            if(userId != auth?.uid ) {
                                addMarker(
                                    view,
                                    point,
                                    ImageProvider.fromResource(context, R.drawable.location_on_red),
                                    userId
                                )
                            }

                        }
                        // Обновляем UI с новыми координатами
                    },
                    onError = { error ->
                        // Показываем ошибку пользователю
                        Log.d("Other users error", error.message.toString())
                    }
                )
            }
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            markerCollection.value?.clear()
            markers.clear()
            mapView?.onStop()
            cancelListening()
        }
    }
}
