package com.example.pollenapp

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _loginState = MutableStateFlow<LoginResult?>(null)
    val loginState = _loginState.asStateFlow()

    fun login(credentials: Credentials) {
        if (!credentials.isNotEmpty()) return
        
        auth.signInWithEmailAndPassword(credentials.login, credentials.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = LoginResult.Success
                } else {
                    _loginState.value = LoginResult.Error(task.exception?.message ?: "Login failed")
                }
            }
    }

    fun register(credentials: Credentials) {
        if (!credentials.isNotEmpty()) return

        auth.createUserWithEmailAndPassword(credentials.login, credentials.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = LoginResult.Success
                } else {
                    _loginState.value = LoginResult.Error(task.exception?.message ?: "Registration failed")
                }
            }
    }
    
    fun resetState() {
        _loginState.value = null
    }
}

sealed class LoginResult {
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
}
