package com.example.eventoscomunitarios.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Crear Cuenta") }) }
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(20.dp)
        ) {

            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirmar Contraseña") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                if (password == confirmPassword) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener { authResult ->
                            // Guardar usuario en Firestore
                            authResult.user?.uid?.let { uid ->
                                val userData = mapOf("email" to email)
                                db.collection("users").document(uid).set(userData)
                                    .addOnSuccessListener { onRegisterSuccess() }
                                    .addOnFailureListener { errorMessage = it.message ?: "Error al guardar usuario" }
                            }
                        }
                        .addOnFailureListener { errorMessage = it.message ?: "Error al crear cuenta" }
                } else {
                    errorMessage = "Las contraseñas no coinciden"
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Registrar")
            }

            TextButton(onClick = onGoToLogin) { Text("Ya tengo cuenta") }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage, color = androidx.compose.ui.graphics.Color.Red)
            }
        }
    }
}
