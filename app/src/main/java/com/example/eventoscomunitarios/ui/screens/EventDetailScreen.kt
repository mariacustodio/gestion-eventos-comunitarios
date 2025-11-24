package com.example.eventoscomunitarios.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventoscomunitarios.viewmodel.EventViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    viewModel: EventViewModel = viewModel()
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var commentText by remember { mutableStateOf("") }
    var userAttending by remember { mutableStateOf(false) }
    var commentsList by remember { mutableStateOf(listOf<Comment>()) }

    LaunchedEffect(eventId) {
        viewModel.selectEvent(eventId)

        // Verificar asistencia del usuario
        currentUser?.uid?.let { uid ->
            db.collection("participation")
                .document("${uid}_$eventId")
                .get()
                .addOnSuccessListener { doc ->
                    userAttending = doc.exists()
                }
        }

        // Cargar comentarios
        db.collection("comments")
            .whereEqualTo("eventId", eventId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    commentsList = snapshot.documents.map { doc ->
                        Comment(
                            userId = doc.getString("userId") ?: "",
                            eventId = doc.getString("eventId") ?: "",
                            comment = doc.getString("comment") ?: "",
                            rating = (doc.getLong("rating") ?: 0L).toInt()
                        )
                    }
                }
            }
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
                Spacer(modifier = Modifier.height(16.dp))

                // Confirmar asistencia
                Button(onClick = {
                    currentUser?.uid?.let { uid ->
                        val docRef = db.collection("participation").document("${uid}_$eventId")
                        if (!userAttending) {
                            val data = mapOf(
                                "userId" to uid,
                                "eventId" to eventId,
                                "timestamp" to System.currentTimeMillis()
                            )
                            docRef.set(data).addOnSuccessListener {
                                userAttending = true
                            }
                        } else {
                            docRef.delete().addOnSuccessListener {
                                userAttending = false
                            }
                        }
                    }
                }) {
                    Text(if (userAttending) "Cancelar asistencia" else "Confirmar asistencia")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Comentarios
                Text("Comentarios", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                commentsList.forEach { comment ->
                    Text("- ${comment.comment} (Rating: ${comment.rating})")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Agregar comentario
                if (currentUser != null) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text("Agregar comentario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(onClick = {
                        val rating = 5 // opcional: agregar selector de rating
                        val data = mapOf(
                            "userId" to currentUser.uid,
                            "eventId" to eventId,
                            "comment" to commentText,
                            "rating" to rating,
                            "timestamp" to System.currentTimeMillis()
                        )
                        db.collection("comments").add(data)
                        commentText = ""
                    }) {
                        Text("Enviar comentario")
                    }
                }
            }
        }
    }
}

data class Comment(
    val userId: String = "",
    val eventId: String = "",
    val comment: String = "",
    val rating: Int = 0
)
