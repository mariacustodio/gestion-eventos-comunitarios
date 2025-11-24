package com.example.eventoscomunitarios.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventoscomunitarios.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(onEventClick: (String) -> Unit, viewModel: EventViewModel = viewModel()) {
    LaunchedEffect(Unit) { viewModel.loadEvents() }
    val events by viewModel.events.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Eventos") }) }) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(events) { event ->
                Column(modifier = Modifier.fillMaxWidth().clickable { onEventClick(event.id) }.padding(16.dp)) {
                    Text(event.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${event.date} ${event.time}")
                    Text(event.location)
                }
                Divider()
            }
        }
    }
}
