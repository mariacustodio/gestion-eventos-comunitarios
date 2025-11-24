package com.example.eventoscomunitarios.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventoscomunitarios.data.models.Event
import com.example.eventoscomunitarios.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {

    private val repository = EventRepository()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _selectedEvent = MutableStateFlow<Event?>(null)
    val selectedEvent: StateFlow<Event?> = _selectedEvent

    fun loadEvents() {
        viewModelScope.launch { _events.value = repository.getAllEvents() }
    }

    fun addEvent(event: Event) {
        viewModelScope.launch {
            repository.addEvent(event)
            loadEvents()
        }
    }

    fun selectEvent(eventId: String) {
        viewModelScope.launch { _selectedEvent.value = repository.getEventById(eventId) }
    }
}
