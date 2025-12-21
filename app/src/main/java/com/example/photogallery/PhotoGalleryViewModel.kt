package com.example.photogallery

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.photogallery.api.GalleryItem
import com.example.photogallery.data.GalleryItemEntity
import com.example.photogallery.data.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhotoGalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val photoRepository = PhotoRepository(application)

    private val _galleryItems = MutableStateFlow<List<GalleryItem>>(emptyList())
    val galleryItems: StateFlow<List<GalleryItem>> = _galleryItems.asStateFlow()

    private val _favorites = MutableStateFlow<List<GalleryItem>>(emptyList())
    val favorites: StateFlow<List<GalleryItem>> = _favorites.asStateFlow()

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch {

            try {
                _galleryItems.value = photoRepository.fetchPhotos()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun searchPhotos(query: String) {
        viewModelScope.launch {
            try {
                _galleryItems.value = photoRepository.searchPhotos(query)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleFavorite(item: GalleryItem) {
        viewModelScope.launch {
            photoRepository.photoDao.insertFavorite(
                GalleryItemEntity(item.id, item.title, item.url)
            )
        }
    }

    fun getFavorites() {
        viewModelScope.launch {
            photoRepository.photoDao.getAllFavorites().collect { entities ->
                _favorites.value = entities.map {
                    GalleryItem(it.title, it.id, null, null, null, it.url)  // null для secret, server, farm; url_s = it.url
                }
            }
        }
    }

    fun deleteAllFavorites() {
        viewModelScope.launch {
            photoRepository.photoDao.deleteAll()
            getFavorites()  // Обновить список
        }
    }
    fun addToFavorite(item: GalleryItem) {
        viewModelScope.launch {
            photoRepository.photoDao.insertFavorite(
                GalleryItemEntity(item.id, item.title, item.url)
            )
        }
    }

    fun deleteFavorite(item: GalleryItem) {
        viewModelScope.launch {
            photoRepository.photoDao.deleteFavorite(
                GalleryItemEntity(item.id, item.title, item.url)
            )
            // Обновляем список избранного сразу после удаления
            getFavorites()
        }
    }
}