package com.example.photogallery.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM favorite_photos")
    fun getAllFavorites(): Flow<List<GalleryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(photo: GalleryItemEntity)

    @Query("DELETE FROM favorite_photos")
    suspend fun deleteAll()
}