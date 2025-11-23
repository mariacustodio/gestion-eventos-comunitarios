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
fun ProfileScreen(
    onLogout: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    var misEventos by remember { mutableStateOf(listOf<Evento>()) }

    LaunchedEffect(currentUser?.uid) {
        if (currentUser != null) {
            db.collection("eventos")
                .whereEqualTo("usuarioId", currentUser.uid)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        misEventos = snapshot.documents.map { doc ->
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
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Perfil de ${currentUser?.email}", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            auth.signOut()
            onLogout()
        }) {
            Text("Cerrar Sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Mis Eventos", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(misEventos) { evento ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(evento.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(evento.fecha)
                        Text(evento.ubicacion)
                    }
                }
            }
        }
    }
}
