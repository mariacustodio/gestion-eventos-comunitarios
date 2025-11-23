package com.example.eventoscomunitarios.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventoscomunitarios.viewmodel.EventViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    viewModel: EventViewModel = viewModel()
) {
    LaunchedEffect(eventId) {
        viewModel.selectEvent(eventId)
    }

    val event by viewModel.selectedEvent.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(event?.title ?: "Detalle Evento") }) }
    ) {
        event?.let {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Descripción: ${it.description}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Fecha: ${it.date}")
                Text(text = "Hora: ${it.time}")
                Text(text = "Lugar: ${it.location}")
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
