package com.example.photogallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.photogallery.api.GalleryItem
import androidx.compose.foundation.clickable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.LightGray  // Серый фон
                ) {
                    PhotoGalleryScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryScreen(
    viewModel: PhotoGalleryViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showFavorites by remember { mutableStateOf(false) }

    val items by viewModel.galleryItems.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PhotoGallery") },
                actions = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Поиск") },
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { viewModel.searchPhotos(searchQuery) }) {
                        Text("Искать")
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Меню")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Показать избранное") },
                            onClick = {
                                showMenu = false
                                viewModel.getFavorites()
                                showFavorites = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Удалить все из БД") },
                            onClick = {
                                showMenu = false
                                viewModel.deleteAllFavorites()
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (showFavorites) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(2.dp),
                modifier = Modifier.padding(padding).fillMaxSize()
            ) {
                items(favorites) { item ->
                    PhotoItem(item = item, onClick = { /* Просмотр */ })
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(2.dp),
                modifier = Modifier.padding(padding).fillMaxSize()
            ) {
                items(items) { item ->
                    PhotoItem(
                        item = item,
                        onClick = { viewModel.toggleFavorite(item) }  // Обычный клик для избранного
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoItem(item: GalleryItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(1.dp)
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = item.url,
            contentDescription = item.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}