package com.example.photogallery.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GalleryItemEntity::class], version = 2 , exportSchema = false)  // Добавьте это
abstract class PhotoDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}
