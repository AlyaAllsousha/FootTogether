package com.example.foodtogether.Location

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject

@Composable
fun YandexMapScreen(latitude: Double, longitude: Double) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(Unit) {
        MapKitFactory.initialize(context)  // Инициализация
    }

    DisposableEffect(Unit) {
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()

        onDispose {
            mapView?.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                mapView = this
                map.move(
                    CameraPosition(
                        Point(latitude, longitude),
                        15.0f,  // Хороший зум
                        0.0f,
                        0.0f
                    )
                )
                // Добавляем маркер
                map.mapObjects.addPlacemark(Point(latitude, longitude))
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}