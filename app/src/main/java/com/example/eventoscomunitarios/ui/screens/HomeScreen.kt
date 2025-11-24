package com.example.eventoscomunitarios.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

data class Event(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val ubicacion: String = ""
)

@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Evento) -> Unit
) {
    var eventos by remember { mutableStateOf(listOf<Evento>()) }
    val db = FirebaseFirestore.getInstance()
    var listener: ListenerRegistration? = null

    LaunchedEffect(Unit) {
        listener = db.collection("eventos")
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null) {
                    eventos = snapshot.documents.map { doc ->
                        Evento(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            descripcion = doc.getString("descripcion") ?: "",
                            fecha = doc.getString("fecha") ?: "",
                            ubicacion = doc.getString("ubicacion") ?: ""
                        )
                    }
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Eventos", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onNavigateToCreate) {
            Text("Crear Nuevo Evento")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(eventos) { evento ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onNavigateToDetail(evento) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(evento.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(evento.fecha)
                        Text(evento.ubicacion)
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { listener?.remove() }
    }
}
