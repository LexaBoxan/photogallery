package com.example.photogallery.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_photos")
data class GalleryItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val url: String
)