package com.tecsup.lab10.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tecsup.lab10.data.SerieApiService
import com.tecsup.lab10.data.SerieModel
import kotlinx.coroutines.launch

@Composable
fun ContenidoSeriesListado(navController: NavHostController, servicio: SerieApiService) {
    var listaOriginal by remember { mutableStateOf<List<SerieModel>>(emptyList()) }
    var categoriaSeleccionada by remember { mutableStateOf("TODAS") }

    LaunchedEffect(Unit) {
        try {
            val listado = servicio.selectSeries()
            listaOriginal = listado
        } catch (e: Exception) {
            Log.e("ERROR-API", "Error al listar: ${e.message}")
        }
    }

    // Filtrado dinámico según el botón seleccionado
    val listaFiltrada = remember(listaOriginal, categoriaSeleccionada) {
        if (categoriaSeleccionada == "TODAS") {
            listaOriginal
        } else {
            listaOriginal.filter { it.category.equals(categoriaSeleccionada, ignoreCase = true) }
        }
    }

    // Obtener la lista única de categorías disponibles
    val categoriasDisponibles = remember(listaOriginal) {
        listOf("TODAS") + listaOriginal.map { it.category.lowercase() }.distinct()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Barra superior con botones de filtro por Categoría
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categoriasDisponibles) { cat ->
                FilterChip(
                    selected = (categoriaSeleccionada == cat),
                    onClick = { categoriaSeleccionada = cat },
                    label = { Text(cat.uppercase(), fontSize = 12.sp) }
                )
            }
        }

        // Encabezado de la Tabla
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ID",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.12f)
            )
            Text(
                text = "SERIE",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.45f)
            )
            Text(
                text = "CAT.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.23f)
            )
            Text(
                text = "ACCIÓN",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.20f)
            )
        }

        // Listado de series
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(listaFiltrada) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.id}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.12f)
                    )
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.45f)
                    )
                    Text(
                        text = item.category,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(0.23f)
                    )
                    Row(
                        modifier = Modifier.weight(0.20f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { navController.navigate("serieVer/${item.id}") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Editar"
                            )
                        }
                        IconButton(
                            onClick = { navController.navigate("serieDel/${item.id}") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Eliminar"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContenidoSerieEditar(
    navController: NavHostController,
    servicio: SerieApiService,
    pid: Int = 0
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var id by remember { mutableStateOf(pid) }
    var name by remember { mutableStateOf("") }
    var release_date by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    if (id != 0) {
        LaunchedEffect(Unit) {
            try {
                val res = servicio.selectSerie(id.toString())
                if (res.isSuccessful) {
                    val obj = res.body()
                    name = obj?.name ?: ""
                    release_date = obj?.release_date ?: ""
                    rating = obj?.rating?.toString() ?: ""
                    category = obj?.category ?: ""
                }
            } catch (e: Exception) {
                Log.e("ERROR-API", "Error al obtener serie: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TextField(
            value = id.toString(),
            onValueChange = { },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name: ") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = release_date,
            onValueChange = { release_date = it },
            label = { Text("Release Date (YYYY-MM-DD):") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = rating,
            onValueChange = { rating = it },
            label = { Text("Rating (Número):") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category (ej: horror, drama, action):") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (name.isBlank() || release_date.isBlank() || rating.isBlank() || category.isBlank()) {
                    Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val objSerie = SerieModel(
                    id = id,
                    name = name,
                    release_date = release_date,
                    rating = rating.toIntOrNull() ?: 0,
                    category = category.trim().lowercase() // Formatea en minúsculas para Django
                )

                coroutineScope.launch {
                    try {
                        val respuesta = if (id == 0) {
                            servicio.insertSerie(objSerie)
                        } else {
                            servicio.updateSerie(id.toString(), objSerie)
                        }

                        if (respuesta.isSuccessful) {
                            Toast.makeText(context, "Guardado exitosamente", Toast.LENGTH_SHORT).show()
                            navController.navigate("series") {
                                popUpTo("series") { inclusive = true }
                            }
                        } else {
                            val errorBody = respuesta.errorBody()?.string()
                            Log.e("ERROR-DJANGO", "Codigo: ${respuesta.code()} Detalle: $errorBody")
                            Toast.makeText(context, "Error Django: $errorBody", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Log.e("ERROR-API", "Error al guardar: ${e.message}", e)
                        Toast.makeText(context, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text("Grabar", fontSize = 16.sp)
        }
    }
}

@Composable
fun ContenidoSerieEliminar(
    navController: NavHostController,
    servicio: SerieApiService,
    id: Int
) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirmar Eliminación") },
            text = { Text("¿Está seguro de eliminar la Serie con ID $id?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        coroutineScope.launch {
                            try {
                                servicio.deleteSerie(id.toString())
                                navController.navigate("series") {
                                    popUpTo("series") { inclusive = true }
                                }
                            } catch (e: Exception) {
                                Log.e("ERROR-API", "Error al eliminar: ${e.message}")
                            }
                        }
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigate("series")
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}