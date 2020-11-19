package com.mjob.feednewsstore4

import android.widget.ImageView
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import java.text.SimpleDateFormat
import java.util.*

fun ImageView.load(uri: String, imageLoader: ImageLoader) {
    val request = ImageRequest.Builder(context)
        .data(uri)
        .target(this)
        .crossfade(true)
        .crossfade(4000)
        .transformations(RoundedCornersTransformation(topLeft = 16f, topRight = 16f))
        .scale(Scale.FILL)
        .build()
    imageLoader.enqueue(request)
}

fun String.toDate(): Date {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    return dateFormat.parse(this)!!
}