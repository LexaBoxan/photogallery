package com.example.photogallery.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FlickrResponse(
    val photos: PhotoMetaData
)

@JsonClass(generateAdapter = true)
data class PhotoMetaData(
    val page: Int,
    val photo: List<GalleryItem>
)

@JsonClass(generateAdapter = true)
data class GalleryItem(
    val title: String,
    val id: String,
    val owner: String,
    val secret: String? = null,  // Опционально
    val server: String? = null,  // Опционально
    val farm: Int? = null,       // Опционально
    @Json(name = "url_s") val url_s: String? = null
) {
    val url: String
        get() = url_s ?: (if (farm != null && server != null && secret != null) {
            "https://farm$farm.staticflickr.com/$server/${id}_${secret}.jpg"
        } else "")
}