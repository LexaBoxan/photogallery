package com.example.photogallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.photogallery.api.GalleryItem
import com.example.photogallery.data.GalleryItemEntity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF6200EE))) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: PhotoGalleryViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var detailInfo by remember { mutableStateOf<Pair<GalleryItem, Boolean>?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Search, "Поиск") },
                    label = { Text("Поиск") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        viewModel.getFavorites()
                    },
                    icon = { Icon(Icons.Default.Favorite, "Избранное") },
                    label = { Text("Избранное") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> SearchScreen(viewModel) { detailInfo = it to false }
                1 -> FavoritesScreen(viewModel) { detailInfo = it to true }
            }
        }
    }

    // ИСПРАВЛЕННАЯ АНИМАЦИЯ
    AnimatedVisibility(
        visible = detailInfo != null,
        enter = slideInVertically { fullHeight -> fullHeight } + fadeIn(),
        exit = slideOutVertically { fullHeight -> fullHeight } + fadeOut()
    ) {
        detailInfo?.let { (item, isFromFav) ->
            FullScreenDetail(
                item = item,
                isAlreadyFavorite = isFromFav,
                onDismiss = { detailInfo = null },
                onAction = {
                    if (isFromFav) viewModel.deleteFavorite(item) else viewModel.toggleFavorite(item)
                    detailInfo = null
                }
            )
        }
    }
}

@Composable
fun SearchScreen(viewModel: PhotoGalleryViewModel, onPhotoClick: (GalleryItem) -> Unit) {
    var query by remember { mutableStateOf("") }
    val items by viewModel.galleryItems.collectAsState()
    val focusManager = LocalFocusManager.current

    Column {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            placeholder = { Text("Введите запрос...") },
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.searchPhotos(query)
                    focusManager.clearFocus()
                }) {
                    Icon(Icons.Default.Search, "Найти")
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.searchPhotos(query)
                focusManager.clearFocus()
            }),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        PhotoGrid(items = items, onPhotoClick = onPhotoClick)
    }
}

@Composable
fun FavoritesScreen(viewModel: PhotoGalleryViewModel, onPhotoClick: (GalleryItem) -> Unit) {
    val favorites by viewModel.favorites.collectAsState()

    Column {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ваша коллекция", style = MaterialTheme.typography.headlineSmall)
            if (favorites.isNotEmpty()) {
                TextButton(onClick = { viewModel.deleteAllFavorites() }) {
                    Text("Удалить всё", color = Color.Red)
                }
            }
        }

        if (favorites.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("В избранном пока ничего нет", color = Color.Gray)
            }
        } else {
            PhotoGrid(
                items = favorites,
                onPhotoClick = onPhotoClick,
                showDeleteOverlay = true,
                onDeleteClick = { viewModel.deleteFavorite(it) }
            )
        }
    }
}

@Composable
fun PhotoGrid(
    items: List<GalleryItem>,
    onPhotoClick: (GalleryItem) -> Unit,
    showDeleteOverlay: Boolean = false,
    onDeleteClick: (GalleryItem) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            Box {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.aspectRatio(1f).clickable { onPhotoClick(item) }
                ) {
                    AsyncImage(
                        model = item.url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (showDeleteOverlay) {
                    Surface(
                        onClick = { onDeleteClick(item) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(28.dp),
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenDetail(
    item: GalleryItem,
    isAlreadyFavorite: Boolean,
    onDismiss: () -> Unit,
    onAction: () -> Unit
) {
    BackHandler(onBack = onDismiss)
    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Column {
                    Text(item.title, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("by ${item.owner}", color = Color.White.copy(0.7f), style = MaterialTheme.typography.bodySmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AsyncImage(
                model = item.url,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().align(Alignment.Center),
                contentScale = ContentScale.Fit
            )

            Button(
                onClick = onAction,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAlreadyFavorite) Color.Red else Color.White,
                    contentColor = if (isAlreadyFavorite) Color.White else Color.Black
                )
            ) {
                Icon(if (isAlreadyFavorite) Icons.Default.Delete else Icons.Default.Favorite, null)
                Spacer(Modifier.width(8.dp))
                Text(if (isAlreadyFavorite) "Удалить из избранного" else "Добавить в избранное")
            }
        }
    }
}