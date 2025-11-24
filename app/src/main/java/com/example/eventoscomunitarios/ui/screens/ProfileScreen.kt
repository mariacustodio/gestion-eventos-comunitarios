package com.example.eventoscomunitarios.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    var myEvents by remember { mutableStateOf(listOf<Evento>()) }
    var attendingEvents by remember { mutableStateOf(listOf<Evento>()) }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            db.collection("events").whereEqualTo("creatorId", uid).addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    myEvents = snapshot.documents.map { doc ->
                        Evento(
                            id = doc.id,
                            nombre = doc.getString("title") ?: "",
                            descripcion = doc.getString("description") ?: "",
                            fecha = doc.getString("date") ?: "",
                            ubicacion = doc.getString("location") ?: ""
                        )
                    }
                }
            }

            db.collection("participation").whereEqualTo("userId", uid).addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    attendingEvents = snapshot.documents.mapNotNull { doc ->
                        val eventId = doc.getString("eventId") ?: return@mapNotNull null
                        db.collection("events").document(eventId).get().addOnSuccessListener { eDoc ->
                            // Ignorar la callback, se muestra en el LazyColumn
                        }
                        null
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Perfil de ${currentUser?.email}", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            auth.signOut()
            onLogout()
        }) { Text("Cerrar Sesión") }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Mis Eventos", style = MaterialTheme.typography.titleMedium)
        LazyColumn { items(myEvents) { e -> Text(e.nombre) } }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Eventos a los que asisto", style = MaterialTheme.typography.titleMedium)
        LazyColumn { items(attendingEvents) { e -> Text(e.nombre) } }
    }
}

data class Evento(val id: String = "", val nombre: String = "", val descripcion: String = "", val fecha: String = "", val ubicacion: String = "")
