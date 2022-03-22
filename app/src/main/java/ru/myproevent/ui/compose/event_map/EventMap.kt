package ru.myproevent.ui.compose

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.support.annotation.DrawableRes
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.domain.utils.convertPixelsToDp
import ru.myproevent.ui.compose.theme.ProeventTheme
import java.lang.Math.abs


fun getDrawableSize(
    @DrawableRes
    id: Int
): Size {
    val o = BitmapFactory.Options()
    o.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
    val bmp = BitmapFactory.decodeResource(
        ProEventApp.instance.resources,
        id, o
    )
    val w = bmp.width
    val h = bmp.height
    return Size(
        w.toFloat(),
        h.toFloat()
    ) // Size(convertPixelsToDp(w.toFloat(), ProEventApp.instance), convertPixelsToDp(h.toFloat(), ProEventApp.instance))
}

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

// https://stackoverflow.com/a/66027497/11883985
@Composable
fun EventMap() {
//    Log.d(
//        "[COMPOSE]",
//        "getDrawableSize: ${getDrawableSize(R.drawable.map)}"
//    )

    var mapImageFillScale by remember { mutableStateOf(0F) }
    var mapImageSize by remember { mutableStateOf(Size.Unspecified) }

    val scale = remember { mutableStateOf(1f) }
    //val rotationState = remember { mutableStateOf(1f) }
    val offsetState = remember { mutableStateOf(Offset(0f, 0f)) }
    Box(
        modifier = Modifier
            .clip(RectangleShape) // Clip the box content
            .fillMaxSize() // Give the size you want...
            .onGloballyPositioned { coordinates ->
                val rootSize = coordinates.size.toSize()
                mapImageSize = getDrawableSize(R.drawable.map)
                mapImageFillScale =
                    if (kotlin.math.abs(rootSize.height - mapImageSize.height) < kotlin.math.abs(
                            rootSize.width - mapImageSize.width
                        )
                    ) {
                        rootSize.height / mapImageSize.height
                    } else {
                        rootSize.width / mapImageSize.width
                    }
                scale.value = mapImageFillScale
            }
            .background(colorResource(R.color.ProEvent_blue_900))
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    val prevScale = scale.value

                    Log.d(
                        "[MYLOG]",
                        "centroid($centroid) pan($pan) zoom($zoom) rotation($rotation)"
                    )

                    val scaleFactor = if (scale.value * zoom > mapImageFillScale * 5) {
                        (mapImageFillScale * 5) / scale.value
                    } else if (scale.value * zoom < mapImageFillScale / 2) {
                        mapImageFillScale / 2 / scale.value
                    } else {
                        zoom
                    }

                    scale.value *= scaleFactor
                    Log.d("[COMPOSE]", "scale.value: ${scale.value}")
                    Log.d("[COMPOSE]", "zoom: ${zoom}")
                    Log.d("[COMPOSE]", "offsetState: ${offsetState.value}")
                    if (zoom == 1f) {
                        var newOffset = offsetState.value + pan
                        val offsetBoundaries = Rect(
                            topLeft = Offset(
                                -mapImageSize.width / 2 * scale.value,
                                -mapImageSize.height / 2 * scale.value
                            ),
                            bottomRight = Offset(
                                mapImageSize.width / 2 * scale.value,
                                mapImageSize.height / 2 * scale.value
                            )
                        )
                        if (newOffset.x > offsetBoundaries.right) {
                            newOffset = Offset(offsetBoundaries.right, newOffset.y)
                        }
                        if (newOffset.x < offsetBoundaries.left) {
                            newOffset = Offset(offsetBoundaries.left, newOffset.y)
                        }
                        if (newOffset.y > offsetBoundaries.bottom) {
                            newOffset = Offset(newOffset.x, offsetBoundaries.bottom)
                        }
                        if (newOffset.y < offsetBoundaries.top) {
                            newOffset = Offset(newOffset.x, offsetBoundaries.top)
                        }
                        offsetState.value = newOffset
                    } else if (scale.value != prevScale) {
                        offsetState.value *= scaleFactor
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value,
                    translationX = offsetState.value.x,
                    translationY = offsetState.value.y,
                )
                .background(Color.White),
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.Center),
                //.fillMaxSize()
                contentDescription = "Map",
                //contentScale = ContentScale.Fit,
                painter = painterResource(R.drawable.map)
            )
            Image(
                painter = painterResource(R.drawable.point),
                contentDescription = "point",
                Modifier
                    .offset(21.dp, 82.dp)
                    .graphicsLayer(
                        scaleX = 1 / scale.value,
                        scaleY = 1 / scale.value,
                    )
                    .offset(0.dp, (-37 / 2).dp),
                colorFilter = ColorFilter.tint(Color.Blue)
            )
        }
        Image(
            painter = painterResource(R.drawable.point),
            contentDescription = "point",
            Modifier
                .align(
                    Alignment.Center
                )
                .offset(0.dp, (-37 / 2).dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProeventTheme {
        EventMap()
    }
}