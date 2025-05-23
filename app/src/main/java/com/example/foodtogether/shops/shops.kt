package com.example.foodtogether.shops

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.foodtogether.R
import com.example.foodtogether.ui.theme.White
import com.yandex.runtime.image.ImageProvider



@Composable
fun shops(shop: String) {
    val markets = ListOfMarkets().markets
    val WebUrl = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    if(shop!="" && !shop.isNullOrBlank()){
        when (shop){
            "Перекресток" -> WebUrl.value="https://www.perekrestok.ru"
            "Ашан" -> WebUrl.value="https://www.auchan.ru/"
            "Пятерочка" -> WebUrl.value="https://5ka.ru/"
            "Лента" -> WebUrl.value="https://lenta.com"
            "Окей" -> WebUrl.value="https://www.okeydostavka.ru"
            "Дикси" -> WebUrl.value="https://dixy.ru"
            "Спар" -> WebUrl.value="https://spar-online.ru"
            else -> WebUrl.value=""
        }
    }
    Box (
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                markets.forEach { market ->

                    Image(
                        painter = painterResource(id = market.iconRes),
                        contentDescription = market.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .clickable { WebUrl.value = market.url }

                    )
                }

            }
            if (WebUrl.value != "") {
                WebViewContent(WebUrl.value)
            } else {
                Spacer(Modifier.height(16.dp))
                Text("Выберете магазин",
                    fontWeight = FontWeight.Light
                )
            }
        }
    }

}
@Composable
fun WebViewContent(url: String) {
    val context = LocalContext.current

    AndroidView(
        factory = {   context->
            WebView(context).apply {
            webViewClient = WebViewClient()
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
            }
        }
        },
        update = { webView ->
            if (webView.url != url) {
                webView.loadUrl(url)
            }
        }
    )
}