package com.example.photogallery.data

import android.content.Context
import androidx.room.Room
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.GalleryItem
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class PhotoRepository(context: Context) {
    private val flickrApi: FlickrApi

    private val database: PhotoDatabase = Room.databaseBuilder(
        context.applicationContext,
        PhotoDatabase::class.java,
        "photo-database"
    ).fallbackToDestructiveMigration().build()

    val photoDao = database.photoDao()

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    suspend fun fetchPhotos(): List<GalleryItem> = flickrApi.fetchPhotos().photos.photo

    suspend fun searchPhotos(query: String): List<GalleryItem> = flickrApi.searchPhotos(query).photos.photo
}