package com.example.eventoscomunitarios.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eventoscomunitarios.data.models.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onEventClick: (String) -> Unit,
    onCreateEvent: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Eventos Comunitarios") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateEvent) {
                Text("+")
            }
        }
    ) { padding ->
        EventList(
            events = sampleEvents(),
            modifier = Modifier.padding(padding),
            onEventClick = onEventClick
        )
    }
}

@Composable
fun EventList(
    events: List<Event>,
    modifier: Modifier = Modifier,
    onEventClick: (String) -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(events) { event ->
            EventItem(event = event, onClick = { onEventClick(event.id) })
        }
    }
}

@Composable
fun EventItem(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Fecha: ${event.date}")
            Text(text = "Lugar: ${event.location}")
        }
    }
}

fun sampleEvents(): List<Event> = listOf(
    Event(id="1", title="Picnic Comunitario", description="", date="2025-12-05", location="Parque Central"),
    Event(id="2", title="Recolección de Basura", description="", date="2025-12-10", location="Río Norte"),
    Event(id="3", title="Feria de Artesanías", description="", date="2025-12-15", location="Plaza Mayor")
)
