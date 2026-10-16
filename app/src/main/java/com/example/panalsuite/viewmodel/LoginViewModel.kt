package com.example.panalsuite.viewmodel

import androidx.lifecycle.ViewModel
import com.example.panalsuite.model.User
import com.example.panalsuite.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _loggedUser = MutableStateFlow<User?>(null)
    val loggedUser: StateFlow<User?> = _loggedUser.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Debe ingresar correo y contraseña"
            return
        }
        val user = repository.login(email, password)

        if (user != null) {
            _loggedUser.value = user
            _errorMessage.value = null
        } else {
            _errorMessage.value = "Correo o contraseña incorrectos"
        }
    }

    fun logout() {
        _loggedUser.value = null
        _errorMessage.value = null
    }
}