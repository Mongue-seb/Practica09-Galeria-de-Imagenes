package com.example.practica09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppGaleria()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppGaleria() {
    // URLs extraídas de la API analizada en la práctica anterior
    val listaImagenes = (1..30).map { "https://rickandmortyapi.com/api/character/avatar/$it.jpeg" }

    // Estado para controlar qué imagen se está viendo en detalle (null = vista de galería)
    var imagenSeleccionada by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (imagenSeleccionada == null) "Galería Dinámica" else "Detalle de Imagen") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                navigationIcon = {
                    if (imagenSeleccionada != null) {
                        IconButton(onClick = { imagenSeleccionada = null }) {
                            Icon(Icons.Filled.Close, contentDescription = "Cerrar detalle")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (imagenSeleccionada != null) {
                // 4. Vista detallada o ampliada
                VisorDetalle(url = imagenSeleccionada!!)
            } else {
                // 2. Cuadrícula adaptativa
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 120.dp),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(listaImagenes) { url ->
                        ItemGaleria(url = url, onClick = { imagenSeleccionada = url })
                    }
                }
            }
        }
    }
}

@Composable
fun ItemGaleria(url: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Mantiene las tarjetas cuadradas
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // 3. Renderizado asíncrono con Coil y estados de carga
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true) // Transición suave al terminar de cargar
                .build(),
            contentDescription = "Imagen de la galería",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            // Indicador mientras se descarga la imagen
            placeholder = rememberVectorPainter(Icons.Filled.Image),
            // Indicador si la URL falla o no hay internet
            error = rememberVectorPainter(Icons.Filled.Warning)
        )
    }
}

@Composable
fun VisorDetalle(url: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            contentDescription = "Imagen ampliada",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit // Ajusta la imagen completa sin recortar
        )
    }
}