package com.example.eventoscomunitarios.data.repository

import com.example.eventoscomunitarios.data.models.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EventRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("events")

    suspend fun getAllEvents(): List<Event> {
        val snapshot = collection.get().await()
        return snapshot.documents.map { doc ->
            Event(
                id = doc.id,
                title = doc.getString("title") ?: "",
                description = doc.getString("description") ?: "",
                date = doc.getString("date") ?: "",
                time = doc.getString("time") ?: "",
                location = doc.getString("location") ?: ""
            )
        }
    }

    suspend fun getEventById(eventId: String): Event? {
        val doc = collection.document(eventId).get().await()
        return doc.toObject(Event::class.java)?.copy(id = doc.id)
    }

    suspend fun addEvent(event: Event) {
        collection.add(event).await()
    }
}
